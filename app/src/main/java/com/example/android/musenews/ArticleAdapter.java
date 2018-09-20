package com.example.android.musenews;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class ArticleAdapter extends ArrayAdapter<Article> {
    public ArticleAdapter(@NonNull Context context, List<Article> articles) {
        super(context, 0, articles);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(
                    R.layout.list_item, parent, false);
        }

        // get current article in the list
        Article currentArticle = getItem(position);

        // get article name text view
        TextView articleName = convertView.findViewById(R.id.article_title);
        // get current article name
        String articleTitle = currentArticle.getWebTitle();
        // set the text from the array
        articleName.setText(articleTitle);

        // get publication date view
        TextView pubDate = convertView.findViewById(R.id.publication_date);
        // get current pub date
        String publicDate = currentArticle.getPubDate();
        // format the pub date
        String[] dateTime = publicDate.split("T");
        // set the text from the array on the list item
        pubDate.setText(dateTime[0]);

        // get section name view
        TextView sectionNameView = convertView.findViewById(R.id.section_name);
        // get current section name
        String sectionName = currentArticle.getSectionName();
        // set the text from the array on  the list item
        sectionNameView.setText(sectionName);

        return convertView;
    }
}
