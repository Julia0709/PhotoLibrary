package com.sample.photolibrary;

import android.net.Uri;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Julia on 2017/07/26.
 */

class FlickerFetcher {

    private static final String TAG = "FlickrFetcher";
    private static final String API_KEY = "";

    private byte[] getUrlBytes(String urlSpec) throws IOException {
        URL url = new URL(urlSpec);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            InputStream in = connection.getInputStream();

            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                throw new IOException(connection.getResponseMessage() + ": with " + urlSpec);
            }

            int bytesRead = 0;
            byte[] buffer = new byte[1024];

            while ((bytesRead = in.read(buffer)) > 0) {
                out.write(buffer, 0, bytesRead);
            }
            out.close();
            return out.toByteArray();

        } finally {
            connection.disconnect();
        }
    }

    String getUrlString(String urlSpec) throws IOException {
        return new String(getUrlBytes(urlSpec));
    }

    public List<PhotoItem> fetchItems() {
        List<PhotoItem> items = new ArrayList<>();

        try {
            String url = Uri.parse("https://api.flickr.com.services/rest/")
                    .buildUpon()
                    .appendQueryParameter("method", "flickr.photos.getRecent")
                    .appendQueryParameter("api_key", API_KEY)
                    .appendQueryParameter("format", "json")
                    .appendQueryParameter("nojsoncallback", "1")
                    .appendQueryParameter("extras", "url_s")
                    .build().toString();

            String jsonString = getUrlString(url);
            Log.i(TAG, "Received JSON: " + jsonString);
            JSONObject jsonBody = new JSONObject(jsonString);
            parseItems(items, jsonBody);

        } catch (IOException ioe) {
            Log.e(TAG, "Failed to fetch items: ", ioe);
        } catch (JSONException je) {
            Log.e(TAG, "Failed to parse JSON: ", je);
        }

        return items;
    }

    private void parseItems(List<PhotoItem> items, JSONObject jsonBody) throws IOException, JSONException {
        JSONObject photosObject = jsonBody.getJSONObject("photos");
        JSONArray photosArray = photosObject.getJSONArray("photo");

        for (int i = 0; i < photosArray.length(); i++) {
            JSONObject photoObject = photosArray.getJSONObject(i);

            PhotoItem item = new PhotoItem();
            item.setId(photoObject.getString("id"));
            item.setCaption(photoObject.getString("title"));
            if (!photoObject.has("url_s")) {
                continue;
            }
            item.setUrl(photoObject.getString("url_s"));
            items.add(item);
        }
    }

}
