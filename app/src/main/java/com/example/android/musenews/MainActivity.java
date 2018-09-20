package com.example.android.musenews;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<Article>> {
    private final static String GUARDIAN_QUERY ="https://content.guardianapis.com/search?page=1&q=music&api-key=";
    private static final int ARTICLE_LOADER_ID = 1;
    TextView emptyElement;
    ProgressBar progress;

    private ArticleAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ListView articleListView = findViewById(R.id.list_view);
        emptyElement = (TextView) findViewById(R.id.empty_view);
        progress = (ProgressBar) findViewById(R.id.progress);

        ConnectivityManager cm =
                (ConnectivityManager)this.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

        final List<Article> articles = new ArrayList<Article>();

        if (activeNetwork != null && activeNetwork.isConnectedOrConnecting()) {


            getLoaderManager().initLoader(ARTICLE_LOADER_ID, null, this);
        }
        else {
            emptyElement.setText(R.string.no_network);
            progress.setVisibility(View.GONE);
        }
        // Create a new {@link ArrayAdapter} of earthquakes
        adapter = new ArticleAdapter(this, new ArrayList<Article>());
        articleListView.setAdapter(adapter);


        articleListView.setEmptyView(emptyElement);
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

    }   @Override
    public ArticleLoader onCreateLoader(int i, Bundle bundle) {
        return new ArticleLoader(this, GUARDIAN_QUERY);

    }

    @Override
    public void onLoadFinished(Loader<List<Article>> loader, List<Article> articles) {
        if (articles == null) {
            return;
        }
        updateUi(articles);
        emptyElement.setText(R.string.empty_text);
        progress.setVisibility(View.GONE);
    }

    @Override
    public void onLoaderReset(Loader<List<Article>> loader) {
        adapter.clear();
    }

    private void updateUi(List articles){
        adapter.clear();
        if (articles != null && !articles.isEmpty()) {
            adapter.addAll(articles);
        }
    }
}
