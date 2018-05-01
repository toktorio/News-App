package com.timotiusoktorio.newsapp.data;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import com.timotiusoktorio.newsapp.R;
import com.timotiusoktorio.newsapp.data.model.News;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import static com.timotiusoktorio.newsapp.data.ApiConstants.NEWS_JSON_DATE;
import static com.timotiusoktorio.newsapp.data.ApiConstants.NEWS_JSON_RESPONSE;
import static com.timotiusoktorio.newsapp.data.ApiConstants.NEWS_JSON_RESULTS;
import static com.timotiusoktorio.newsapp.data.ApiConstants.NEWS_JSON_TAGS;
import static com.timotiusoktorio.newsapp.data.ApiConstants.NEWS_JSON_TAGS_TYPE;
import static com.timotiusoktorio.newsapp.data.ApiConstants.NEWS_JSON_TAGS_TYPE_CONTRIBUTOR;
import static com.timotiusoktorio.newsapp.data.ApiConstants.NEWS_JSON_TITLE;
import static com.timotiusoktorio.newsapp.data.ApiConstants.NEWS_JSON_URL;
import static com.timotiusoktorio.newsapp.data.ApiConstants.NEWS_SEARCH_PARAM_API_KEY;
import static com.timotiusoktorio.newsapp.data.ApiConstants.NEWS_SEARCH_PARAM_SECTION;
import static com.timotiusoktorio.newsapp.data.ApiConstants.NEWS_SEARCH_PARAM_SECTION_VALUE;
import static com.timotiusoktorio.newsapp.data.ApiConstants.NEWS_SEARCH_PARAM_TAGS;
import static com.timotiusoktorio.newsapp.data.ApiConstants.NEWS_SEARCH_PARAM_TAGS_VALUE;
import static com.timotiusoktorio.newsapp.data.ApiConstants.NEWS_SEARCH_URL;

public class NewsLoader extends AsyncTaskLoader<List<News>> {

    private static final String TAG = NewsLoader.class.getSimpleName();
    private static final int READ_TIMEOUT = 10000;
    private static final int CONNECT_TIMEOUT = 15000;

    public NewsLoader(Context context) {
        super(context);
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    @Override
    public List<News> loadInBackground() {
        return makeHttpRequest();
    }

    private List<News> makeHttpRequest() {
        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        BufferedReader reader = null;
        List<News> newsList = null;

        try {
            Uri uri = Uri.parse(NEWS_SEARCH_URL).buildUpon()
                    .appendQueryParameter(NEWS_SEARCH_PARAM_SECTION, NEWS_SEARCH_PARAM_SECTION_VALUE)
                    .appendQueryParameter(NEWS_SEARCH_PARAM_TAGS, NEWS_SEARCH_PARAM_TAGS_VALUE)
                    .appendQueryParameter(NEWS_SEARCH_PARAM_API_KEY, getContext().getString(R.string.theguardian_api_key))
                    .build();

            URL url = new URL(uri.toString());
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setReadTimeout(READ_TIMEOUT);
            urlConnection.setConnectTimeout(CONNECT_TIMEOUT);
            urlConnection.connect();

            int responseCode = urlConnection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                inputStream = urlConnection.getInputStream();
                reader = new BufferedReader(new InputStreamReader(inputStream, Charset.forName("UTF-8")));

                String line;
                StringBuilder builder = new StringBuilder();
                while ((line = reader.readLine()) != null) {
                    builder.append(line).append("\n");
                }

                String jsonString = builder.toString();
                if (!TextUtils.isEmpty(jsonString)) {
                    newsList = extractNewsListFromJSONString(jsonString);
                }
            }
        } catch (IOException | JSONException e) {
            Log.e(TAG, e.getMessage(), e);
        } finally {
            try {
                if (urlConnection != null) urlConnection.disconnect();
                if (inputStream != null) inputStream.close();
                if (reader != null) reader.close();
            } catch (IOException e) {
                Log.e(TAG, e.getMessage(), e);
            }
        }
        return newsList;
    }

    private List<News> extractNewsListFromJSONString(String jsonString) throws JSONException {
        List<News> newsList = new ArrayList<>();
        JSONObject response = new JSONObject(jsonString).getJSONObject(NEWS_JSON_RESPONSE);
        JSONArray results = response.getJSONArray(NEWS_JSON_RESULTS);

        for (int i = 0; i < results.length(); i++) {
            JSONObject object = results.getJSONObject(i);
            String title = object.optString(NEWS_JSON_TITLE);
            String date = object.optString(NEWS_JSON_DATE);
            String url = object.optString(NEWS_JSON_URL);

            JSONArray tags = object.getJSONArray(NEWS_JSON_TAGS);
            String tag = tags.getJSONObject(0).optString(NEWS_JSON_TITLE);

            List<String> contributors = new ArrayList<>();
            for (int j = 0; j < tags.length(); j++) {
                JSONObject tagObject = tags.getJSONObject(j);
                if (tagObject.optString(NEWS_JSON_TAGS_TYPE).equals(NEWS_JSON_TAGS_TYPE_CONTRIBUTOR)) {
                    contributors.add(tagObject.optString(NEWS_JSON_TITLE));
                }
            }

            News news = new News(title, date, url, tag, contributors);
            newsList.add(news);
        }
        return newsList;
    }
}