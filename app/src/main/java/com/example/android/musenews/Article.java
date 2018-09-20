package com.example.android.musenews;

public class Article {
    private String mSectionName;
    private String mWebTitle;
    private String mWebUrl;
    private String mPubDate;

    public Article(String sectionName, String webTitle, String webUrl, String pubDate){
        mSectionName = sectionName;
        mWebTitle = webTitle;
        mWebUrl = webUrl;
        mPubDate = pubDate;
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
}
