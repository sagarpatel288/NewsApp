package com.example.android.newsapp.newslist.utils;

import android.text.TextUtils;

import com.example.android.newsapp.AppConstants;
import com.example.android.newsapp.newslist.model.News;

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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static android.util.Log.d;
import static com.library.android.common.appconstants.AppConstants.TAG;

public final class NewsHelper {

    private static final int TIMEOUT = 10000;
    private static final int CONNECT_TIMEOUT = 15000;
    private static final long RETRY_DELAY_MS = 3000;
    private static final int RETRIES = 3;

    private NewsHelper() {
    }

    /**
     * called by loader to fetch the data
     *
     * @param reqUrl         request URL from which to fetch the data
     * @param mRequestMethod Type of HTTP Request method (verbs) like get, post etc...
     * @return list of custom objects parsed from JSON response
     */
    static List<News> getNews(String reqUrl, String mRequestMethod) {

        String jsonResponse = "";
        try {
            jsonResponse = makeHttpRequest(createUrl(reqUrl), mRequestMethod);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return parseNews(jsonResponse);
    }

    /**
     * opens a http request for the required url
     * reads the data from the input stream
     *
     * @param url            url from which to fetch the results
     * @param mRequestMethod Type of HTTP Request e.g. get, post etc...
     * @return return json response from the url
     * @throws IOException exception while reading from stream objects
     */
    private static String makeHttpRequest(URL url, String mRequestMethod) throws IOException {
        String jsonResponse = "";
        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;

        //validate if the request url is null
        if (url == null) {
            return null;
        }

        try {
            //set up the http url connection
            urlConnection = verifyConnectionResponse(url);

            if (urlConnection != null) {
                urlConnection.setRequestMethod(mRequestMethod);
                urlConnection.setReadTimeout(TIMEOUT);
                urlConnection.setConnectTimeout(CONNECT_TIMEOUT);
                urlConnection.connect();

                //get the input stream to read data from
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            //disconnect the url connection
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            //close input stream
            if (inputStream != null) {
                inputStream.close();
            }
        }

        return jsonResponse;
    }

    /**
     * creates a url from a string
     *
     * @param reqUrl request url in string format
     * @return URL for the API call
     */
    private static URL createUrl(String reqUrl) {
        URL url = null;
        try {
            url = new URL(reqUrl);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return url;
    }

    /**
     * parse json response obtained by the API call
     *
     * @param jsonResponse json string to be parsed
     * @return list of parsed objects
     */
    private static List<News> parseNews(String jsonResponse) {
        //validate if the json string is empty
        if (TextUtils.isEmpty(jsonResponse)) {
            return null;
        }

        List<News> news = new ArrayList<>();

        try {
            //create new json object for the json string
            JSONObject jsonObject = new JSONObject(jsonResponse);
            //extract the root object
            JSONObject response = jsonObject.optJSONObject(AppConstants.JsonKeys.RESPONSE);

            if (response != null) {
                //fetch the results array
                JSONArray jsonArray = response.optJSONArray(AppConstants.JsonKeys.RESULTS);

                if (jsonArray != null) {
                    //loop through the array elements and parse the individual fields
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject newsObj = jsonArray.optJSONObject(i);

                        if (newsObj != null) {
                            String title = newsObj.optString(AppConstants.JsonKeys.WEB_TITLE);
                            String section = newsObj.optString(AppConstants.JsonKeys.SECTION_NAME);
                            String publishDate = newsObj.optString(AppConstants.JsonKeys.WEB_PUBLICATION_DATE);
                            publishDate = formatDate(publishDate);
                            String webUrl = newsObj.optString(AppConstants.JsonKeys.WEB_URL);

                            String thumbnailUrl = "";
                            JSONObject fields = newsObj.optJSONObject(AppConstants.JsonKeys.FIELDS);
                            if (fields != null) {
                                thumbnailUrl = fields.optString(AppConstants.JsonKeys.THUMBNAIL);
                            }

                            JSONArray tags = newsObj.optJSONArray(AppConstants.JsonKeys.TAGS);
                            String authorName = "";
                            if (tags != null && tags.length() > 0) {
                                JSONObject authorProfile = (JSONObject) tags.get(0);
                                authorName = authorProfile.optString(AppConstants.JsonKeys.WEB_TITLE);
                            }

                            //initialize and add news items to the array list
                            News newsItem = new News(title, section, webUrl, thumbnailUrl, publishDate, authorName);
                            news.add(newsItem);
                        }
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return news;
    }

    private static HttpURLConnection verifyConnectionResponse(URL url) throws InterruptedException, IOException {
        int retry = 0;
        boolean delay = false;
        HttpURLConnection urlConnection;
        do {
            if (delay) {
                Thread.sleep(RETRY_DELAY_MS);
            }
            urlConnection = (HttpURLConnection) url.openConnection();
            //verify http connection response status
            switch (urlConnection.getResponseCode()) {
                case HttpURLConnection.HTTP_OK:
                    d(TAG, "verifyConnectionResponse: HTTP_OK");
                    return urlConnection;
                case HttpURLConnection.HTTP_GATEWAY_TIMEOUT:
                    d(TAG, "verifyConnectionResponse: GATEWAY_TIMEOUT");
                    break;
                case HttpURLConnection.HTTP_UNAVAILABLE:
                    d(TAG, "verifyConnectionResponse: PAGE UNAVAILABLE");
                    break;
                default:
                    d(TAG, "verifyConnectionResponse: UNKNOWN ERROR");
                    break;
            }
            urlConnection.disconnect();
            retry++;
            d(TAG, "verifyConnectionResponse: Failed retry: " + retry + " Total Retries: " + RETRIES);
            delay = true;

        } while (retry < RETRIES);
        d(TAG, "verifyConnectionResponse: Aborted");
        return urlConnection;
    }

    /**
     * reads the data from the input stream
     *
     * @param inputStream stream of data to be read
     * @return read data in the form of string
     * @throws IOException exceptions while reading from the input stream
     */
    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            //initialize an input stream reader
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            //connect the input stream reader to a buffered reader
            BufferedReader reader = new BufferedReader(inputStreamReader);
            //read the data
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }

    /**
     * formats the date in the required format
     *
     * @param publishDate date to be formatted
     * @return formatted date in the form of a string
     */
    public static String formatDate(String publishDate) {
        String formattedDate = "";
        //define input date format
        SimpleDateFormat inputSdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
        //define output date format
        SimpleDateFormat outputSdf = new SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault());
        try {
            //parse and format the input date
            Date date = inputSdf.parse(publishDate);
            formattedDate = outputSdf.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return formattedDate;
    }
}
