package com.example.android.musenews;

public class Article {
    private String mSectionName;
    private String mWebTitle;
    private String mWebUrl;
    private String mPubDate;
    private String mAuthor;

    public Article(String sectionName, String webTitle, String webUrl, String pubDate, String author){
        mSectionName = sectionName;
        mWebTitle = webTitle;
        mWebUrl = webUrl;
        mPubDate = pubDate;
        mAuthor = author;
    }

    public String getSectionName(){
        return mSectionName;
    }

    public String getWebTitle(){
        return mWebTitle;
    }

    public String getWebUrl(){
        return mWebUrl;
    }

    public String getPubDate(){
        return mPubDate;
    }

    public String getAuthor(){
        return mAuthor;
    }
}
