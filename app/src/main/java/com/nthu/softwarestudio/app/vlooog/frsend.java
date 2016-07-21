package com.nthu.softwarestudio.app.vlooog;

import android.net.Uri;
import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

/**
 * Created by doda on 2016/6/23.
 */
public class frsend extends AsyncTask<String, Void, String> {
        String forecastJsonStr;

@Override
protected String doInBackground(String... params) {
        if (params.length == 0) {
        return null;
        }

        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        try {
final String FORECAST_BASE_URL =
        "http://s103062325.web.2y.idv.tw/javafinal/frhandler.php?";
final String FIELD1 = "uid";
final String F2 = "email";
            final String F3 = "name";
final String API_READ_KEY = "key";

        Uri builtUri = Uri.parse(FORECAST_BASE_URL).buildUpon()
        .appendQueryParameter(FIELD1, params[0])
        .appendQueryParameter(F2, params[1])
                .appendQueryParameter(F3, params[2])
        .build();

        URL url = new URL(builtUri.toString());

        //Log.v(LOG_TAG, "Built URI " + builtUri.toString());

        // Create the request to OpenWeatherMap, and open the connection
        urlConnection = (HttpURLConnection) url.openConnection();
        urlConnection.setRequestMethod("GET");
        urlConnection.connect();
        // Read the input stream into a String
        InputStream inputStream = urlConnection.getInputStream();
        StringBuffer buffer = new StringBuffer();
        if (inputStream == null) {
        // Nothing to do.
        return null;
        }
        reader = new BufferedReader(new InputStreamReader(inputStream));

        String line;
        while ((line = reader.readLine()) != null) {
        // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
        // But it does make debugging a *lot* easier if you print out the completed
        // buffer for debugging.
        buffer.append(line + "\n");
        }

        if (buffer.length() == 0) {
        // Stream was empty.  No point in parsing.
        return null;
        }
        forecastJsonStr = buffer.toString();

        //Log.v(LOG_TAG, "Forecast string: " + forecastJsonStr);
        } catch (IOException e) {
        // Log.e(LOG_TAG, "Error ", e);
        // If the code didn't successfully get the weather data, there's no point in attemping
        // to parse it.
        return null;
        } finally {
        if (urlConnection != null) {
        urlConnection.disconnect();
        }
        if (reader != null) {
        try {
        reader.close();
        } catch (final IOException e) {
        //Log.e(LOG_TAG, "Error closing stream", e);
        }
        }
        }
        // This will only happen if there was an error getting or parsing the forecast.
        return params[1];
        }
        }
