package com.example.android.musenews;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import static com.example.android.musenews.ArticleLoader.LOG_TAG;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<Article>> {
    private final static String apiKey = BuildConfig.ApiKey;
    private final static String GUARDIAN_QUERY ="https://content.guardianapis.com/search?";
    private static final int ARTICLE_LOADER_ID = 1;
    private TextView emptyElement;
    private ProgressBar progress;
    private ListView articleListView;
    private ArticleAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        articleListView = findViewById(R.id.list_view);
        emptyElement = (TextView) findViewById(R.id.empty_view);
        progress = (ProgressBar) findViewById(R.id.progress);

        // set up a connectivity manager so that I can check if the user has internet
        ConnectivityManager cm =
                (ConnectivityManager)this.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

        // if user has internet, proceed to initialize the loader.
        if (activeNetwork != null && activeNetwork.isConnectedOrConnecting()) {

            getLoaderManager().initLoader(ARTICLE_LOADER_ID, null, this);
        }
        // if no internet, tell the user
        else {
            emptyElement.setText(R.string.no_network);
            progress.setVisibility(View.GONE);
        }
        // Create a new {@link ArrayAdapter} of articles & set it
        adapter = new ArticleAdapter(this, new ArrayList<Article>());
        articleListView.setAdapter(adapter);
        // set Empty View method so that the empty view will take over the listview when there are no
        // list view items to display
        articleListView.setEmptyView(emptyElement);


    }
    // creates a loader if one does not exist. necessary for using the loader
    @Override
    public ArticleLoader onCreateLoader(int i, Bundle bundle) {

        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);

        String orderBy  = sharedPrefs.getString(
                getString(R.string.settings_order_by_key),
                getString(R.string.settings_order_by_default)
        );

        String numArticles = sharedPrefs.getString(getString(R.string.settings_num_articles_key), getString(R.string.settings_num_articles_label));
        // parse breaks apart the URI string that's passed into its parameter
        Uri baseUri = Uri.parse(GUARDIAN_QUERY);

        // buildUpon prepares the baseUri that we just parsed so we can add query parameters to it
        Uri.Builder uriBuilder = baseUri.buildUpon();

        // Append query parameter and its value. For example, the `q=music``
        uriBuilder.appendQueryParameter("order-by", orderBy);
        uriBuilder.appendQueryParameter("show-tags", "contributor");
        uriBuilder.appendQueryParameter("page-size", numArticles);
        uriBuilder.appendQueryParameter("q", "music");
        uriBuilder.appendQueryParameter("api-key", apiKey);
        Log.i(LOG_TAG, uriBuilder.toString());
        return new ArticleLoader(this, uriBuilder.toString());

    }

    // tells app what to do when the background work is completed.
    @Override
    public void onLoadFinished(Loader<List<Article>> loader, final List<Article> articles) {
        // If we didn't get any articles back, tell the user
        if (articles == null) {
            emptyElement.setText(R.string.no_results);
            progress.setVisibility(View.GONE);
            return;
        }
        // update the screen with the articles we pulled from the JSON
        updateUi(articles);
        emptyElement.setText(R.string.empty_text);
        progress.setVisibility(View.GONE);

        // create an instance of onItemClickListener to listen for user clicks
        articleListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                // get the earthquake the user clicked on
                Article article = articles.get(position);
                // get the url associated with that article
                String url = article.getWebUrl();
                // Send intent to open webpage
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
            }
        });
    }

    // necessary method for Loaders. reset the adapter
    @Override
    public void onLoaderReset(Loader<List<Article>> loader) {
        adapter.clear();
    }

    // make sure the adapter doesn't have old data in it, then update articles if applicable
    private void updateUi(List articles){
        adapter.clear();
        if (articles != null && !articles.isEmpty()) {
            adapter.addAll(articles);
        }
    }
    @Override
    // This method initialize the contents of the Activity's options menu
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    // This method is called whenever an item in the options menu is selected.
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent settingsIntent = new Intent(this, SettingsActivity.class);
            startActivity(settingsIntent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
