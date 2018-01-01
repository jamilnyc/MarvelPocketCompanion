package com.jamil.companion.gateway;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import com.jamil.companion.BaseActivity;
import com.jamil.companion.cache.EntityCache;
import com.jamil.companion.config.ApiConfig;
import com.jamil.companion.model.Character;
import com.jamil.companion.model.Creator;
import com.jamil.companion.model.Entity;
import com.jamil.companion.model.Image;
import com.jamil.companion.model.Series;
import com.jamil.companion.ui.Alertable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ApiGateway {

    public static final String TAG = ApiGateway.class.getSimpleName();
    private static final OkHttpClient client = new OkHttpClient();
    private static final boolean cacheEnabled = true;

    private static void makeRequest(final Context context, final Alertable alertable, final String entityType, Map<String, String> filterParams)
    {
        long currentTimestamp = getTimestamp();
        Uri.Builder builder = new Uri.Builder();
        builder.scheme("https")
                .authority("gateway.marvel.com")
                .appendPath("v1")
                .appendPath("public")
                ;

        if (entityType != null && !entityType.isEmpty()) {
            builder.appendPath(entityType);
        }

        // Add query string parameters to request
        if(filterParams != null) {
            Iterator it = filterParams.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry pair = (Map.Entry) it.next();
                builder.appendQueryParameter((String) pair.getKey(), (String) pair.getValue());
                it.remove();
            }
        }

        final String cachingUrl = builder.build().toString();
        Log.d(TAG, "Caching URL: " + cachingUrl);
        String cachedResponse = getCachedResponse(context, cachingUrl);

        if (cachedResponse != null) {
            Log.d(TAG, "Found a cached request");
            Log.d(TAG, cachedResponse);
            try {
                parseJsonResponse(entityType, cachedResponse, alertable);
                return;
                // Stop here to prevent making a network call
            } catch (JSONException e) {
                Log.e(TAG, "Unable to parse JSON String");
                e.printStackTrace();
            }
        } else {
            Log.d(TAG, "No cached entry found");
        }

        // TODO: Hacky way to check network from activity. Refactor it.
        if (!((BaseActivity) context).isNetworkAvailable()) {
            // Send listener no data
            alertable.receiveData(new ArrayList<Entity>(), 0, 0, 0, 0);
        }

        // Add API keys
        builder.appendQueryParameter("ts", "" + currentTimestamp)
            .appendQueryParameter("apikey", ApiConfig.API_KEY)
            .appendQueryParameter("hash", getHash(currentTimestamp, ApiConfig.PRIVATE_KEY, ApiConfig.API_KEY));


        String requestUrl = builder.build().toString();
        Log.d(TAG, "URL: " + requestUrl);

        Request request = new Request.Builder().url(requestUrl).build();
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                ((BaseActivity) context).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(context, "There was a problem connecting to Marvel's API. Please try again later.", Toast.LENGTH_LONG).show();
                        ((BaseActivity) context).finish();
                    }
                });

                Log.e(TAG, "Network Call Failed!");
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String jsonData = response.body().string();
                Log.d(TAG, jsonData);

                try {
                    if (response.isSuccessful()) {
                        cacheResponse(context, cachingUrl, jsonData);
                        parseJsonResponse(entityType, jsonData, alertable);
                    }
                } catch (JSONException e) {
                    // Show message to user
                    Log.e(TAG, "Unable to parse JSON");
                    e.printStackTrace();
                }
            }
        });
    }

    private static String getCachedResponse(Context context, String cachingUrl)
    {
        if (!cacheEnabled) {
            return null;
        }

        EntityCache cache = new EntityCache(context);
        try {
            Log.d(TAG, "Reading cache now . . .");
            com.jamil.companion.cache.model.Response response = cache.getResponse(getMd5FromString(cachingUrl));
            return response != null ? response.getResponseBody() : null;
        } catch (NoSuchAlgorithmException e) {
            Log.e(TAG, "Error hashing URL for caching!");
        }
        return null;
    }

    private static void cacheResponse(Context context, String cachingUrl, String jsonData)
    {
        if (!cacheEnabled) {
            return;
        }

        EntityCache cache = new EntityCache(context);
        try {
            Log.d(TAG, "Writing cache now . . . ");
            com.jamil.companion.cache.model.Response response = new com.jamil.companion.cache.model.Response(getMd5FromString(cachingUrl), jsonData, null);
            cache.addResponse(response);
        } catch (NoSuchAlgorithmException e) {
            Log.e(TAG, "Unable to save response to cache");
            e.printStackTrace();
        }
    }

    /**
     * Searches for Character entities whose name begins with the given query string.
     *
     * @param query The term to search by
     * @param offset The offset of the result set, used for pagination (default to 0)
     * @param limit The maximum number of results in the result set
     * @param alertable The object to receive the entities parsed out of the API response
     */
    public static void searchCharacters(Context context, String query, int offset, int limit, Alertable alertable)
    {
        Map<String, String> params = new HashMap<>();
        params.put("nameStartsWith", query);
        params.put("orderBy", "-modified");
        params.put("offset", "" + offset);
        params.put("limit", "" + limit);
        makeRequest(context, alertable, "characters", params);
    }

    public static void searchSeriesByCharacter(Context context, int characterId, int offset, int limit, Alertable alertable)
    {
        Log.d(TAG, "About to make request for series");
        Map<String, String> params = new HashMap<>();
        params.put("characters", "" + characterId);
        params.put("orderBy", "startYear");
        params.put("offset", "" + offset);
        params.put("limit", "" + limit);
        makeRequest(context, alertable, "series", params);
    }

    // TODO: Move to util class
    public static long getTimestamp()
    {
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        return timestamp.getTime();
    }

    /**
     * Returns the required hash of keys and time stamp to access the API.
     *
     * @param currentTimestamp The current system time
     * @param privateKey The private key of the API
     * @param apiKey The public key of the API
     * @return The MD5 hash of the concatenation of these values
     */
    private static String getHash(long currentTimestamp, String privateKey, String apiKey)
    {
        String unencrypted = currentTimestamp + privateKey + apiKey;
        String hash;
        try {
            hash = getMd5FromString(unencrypted);
        } catch (NoSuchAlgorithmException e) {
            hash = "nope";
            Log.e(TAG, "Unable to generate hash for API.");
        }

        return hash;
    }

    // TODO: Move to util class
    /**
     * Returns the MD5 hash of a given string.
     *
     * @param data The data to create a checksum of
     * @return the MD5 hash of the given string
     * @throws NoSuchAlgorithmException when the hashing algorithm doesn't exist
     */
    public static String getMd5FromString(String data) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("MD5");
        md.update(data.getBytes());

        byte byteData[] = md.digest();

        // Convert bytes to hex format
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < byteData.length; i++) {
            sb.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16).substring(1));
        }

        return sb.toString();
    }

    /**
     * Parses the given raw JSON response into a list of Entities of the given type and alerts
     * the Alertable object.
     *
     * @param entityType The type of the entity to parse out into model objects
     * @param jsonData The raw JSON response from the API
     * @param alertable The object to receive the parsed entity list
     * @throws JSONException when the response is malformed or incorrect
     */
    private static void parseJsonResponse(String entityType, String jsonData, Alertable alertable) throws JSONException {
        JSONObject response = new JSONObject(jsonData);
        JSONObject data = response.getJSONObject("data");
        JSONArray results = data.getJSONArray("results");

        int offset = data.getInt("offset");
        int limit = data.getInt("limit");
        int total = data.getInt("total");
        int count = data.getInt("count");

        // Loop through each result and parse an entity out of it.
        ArrayList<Entity> entities = new ArrayList<>();
        for (int i = 0; i < results.length(); ++i) {
            JSONObject result = results.getJSONObject(i);

            if (entityType.equals("characters")) {
                entities.add(getCharacterFromResult(result));
            } else if (entityType.equals("series")) {
                entities.add(getSeriesFromResult(result));
            }
        }

        alertable.receiveData(entities, offset, limit, total, count);
    }

    private static Image getThumbnailFromResult(JSONObject result) throws JSONException {
        JSONObject thumbnailObj = result.getJSONObject("thumbnail");
        String thumbnailPath = thumbnailObj.getString("path");
        String thumbnailExtension = thumbnailObj.getString("extension");
        return new Image(thumbnailPath, thumbnailExtension);
    }

    private static String getUrlByType(JSONObject result, String type) throws JSONException {
        String link = "";
        JSONArray urls = result.getJSONArray("urls");
        for (int i = 0; i < urls.length(); ++i) {
            JSONObject urlObj = urls.getJSONObject(i);
            if (urlObj.getString("type").equals(type)) {
                link = urlObj.getString("url");
            }
        }

        return link;
    }

    private static Character getCharacterFromResult(JSONObject result) throws JSONException {
        int id = result.getInt("id");
        String name = result.getString("name");
        String description = result.getString("description");
        Image thumbnail = getThumbnailFromResult(result);
        String wikiLink = getUrlByType(result, "wiki");

        return new Character(id, thumbnail, name, description, wikiLink);
    }

    private static Series getSeriesFromResult(JSONObject result) throws JSONException
    {
        int id = result.getInt("id");
        String title = result.getString("title");
        String description = result.getString("description");
        description = (description == null || description.equals("null"))? "" : description;
        Image thumbnail = getThumbnailFromResult(result);
        String detailUrl = getUrlByType(result, "detail");
        String rating = result.getString("rating");
        String type = result.getString("type");
        int startYear = result.getInt("startYear");
        int endYear = result.getInt("endYear");

        ArrayList<Creator> creators = new ArrayList<>();
        JSONObject creatorsObj = result.getJSONObject("creators");
        JSONArray creatorsArray = creatorsObj.getJSONArray("items");
        for (int i = 0; i < creatorsArray.length(); ++i) {
            JSONObject creatorItem = creatorsArray.getJSONObject(i);
            String creatorName = creatorItem.getString("name");
            String creatorRole = creatorItem.getString("role");
            Creator c = new Creator(creatorName, creatorRole);
            creators.add(c);
        }

        Log.d(TAG, "Series " + id + ", title: " + title);
        return new Series(id, thumbnail, title, description, detailUrl, rating, type, startYear, endYear, creators);
    }
}
