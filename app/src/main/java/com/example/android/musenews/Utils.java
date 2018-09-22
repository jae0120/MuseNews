package com.example.android.musenews;

import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public class Utils {
    /** Tag for the log messages */
    private static final String LOG_TAG = Utils.class.getSimpleName();
    private static final int READ_TIMEOUT_LIMIT = 10000;
    private static final int CONNECT_TIMEOUT_LIMIT = 15000;

    /**
     * Query the Guardian API and return an {@link Article} object to represent a single earthquake.
     */
    public static List<Article> fetchArticleData(String requestUrl) {
        // Create URL object
        URL url = createUrl(requestUrl);

        // Perform HTTP request to the URL and receive a JSON response back
        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error closing input stream", e);
        }

        // Extract relevant fields from the JSON response and create an {@link Event} object
        List<Article> article = extractArticleFromJson(jsonResponse);

        // Return the {@link Event}
        return article;
    }

    /**
     * Returns new URL object from the given string URL.
     */
    public static URL createUrl(String stringUrl) {
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Error with creating URL ", e);
        }
        return url;
    }

    /**
     * Make an HTTP request to the given URL and return a String as the response.
     */
    public static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";

        // If the URL is null, then return early.
        if (url == null) {
            return jsonResponse;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(READ_TIMEOUT_LIMIT /* milliseconds */);
            urlConnection.setConnectTimeout(CONNECT_TIMEOUT_LIMIT /* milliseconds */);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // If the request was successful (response code 200),
            // then read the input stream and parse the response.
            if (urlConnection.getResponseCode() == urlConnection.HTTP_OK) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem retrieving the earthquake JSON results.", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    /**
     * Convert the {@link InputStream} into a String which contains the
     * whole JSON response from the server.
     */
    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }

    /**
     * Return an {@link Article} object by parsing out information
     * about the articles from the result query.
     */
    public static List<Article> extractArticleFromJson(String jsonResponse) {
        // If the JSON string is empty or null, then return early.
        if (TextUtils.isEmpty(jsonResponse)) {
            return null;
        }
        List<Article> articles = new ArrayList<Article>();

        try {
            JSONObject baseJsonResponse = new JSONObject(jsonResponse);
            JSONObject responseObject = baseJsonResponse.optJSONObject("response");
            JSONArray resultsArray = responseObject.optJSONArray("results");

            // If there are results in the features array
            for (int i = 0; i < resultsArray.length(); i++){
                JSONObject c = resultsArray.optJSONObject(i);
                String sectionName = c.optString("sectionName");
                String webTitle = c.optString("webTitle");
                String pubDate = c.optString("webPublicationDate");
                String webUrl = c.optString("webUrl");
                String author = "";
                JSONArray tags = c.optJSONArray("tags");
                if (tags.length() != 0) {
                    JSONObject d = tags.optJSONObject(0);
                    author = d.optString("webTitle");
                }

                // Create a new {@link Event} object
                articles.add(new Article (sectionName, webTitle, webUrl, pubDate, author));
            }
        } catch (JSONException e) {
            Log.e(LOG_TAG, "Problem parsing the earthquake JSON results", e);
        }
        return articles;
    }
}


