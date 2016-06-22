package com.nthu.softwarestudio.app.vlooog;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.RatingBar;
import android.widget.TextView;

import com.nthu.softwarestudio.app.vlooog.data.PostContract;
import com.nthu.softwarestudio.app.vlooog.data.WebServerContract;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by Ywuan on 22/06/2016.
 */
public class Rating extends AsyncTask<String, Void, String>{
    final String LOG_TAG = this.getClass().getSimpleName();

    final String RATE_BASE_URL = WebServerContract.BASE_URL + "/rating.php";
    final String FAIL = "UNFOUND";
    final String SUCCESS = "SUCCESS";

    HttpURLConnection httpURLConnection = null;
    BufferedReader bufferedReader = null;

    Boolean networkService = true;

    Context context;
    Post post;
    MainFragment.RecyclerViewPostAdapter.ViewHolder holder;
    View view;

    public Rating(Context context, Post post, MainFragment.RecyclerViewPostAdapter.ViewHolder holder){
        this.context = context;
        this.post = post;
        this.holder = holder;
    }

    public Rating(Context context, Post post, View view){
        this.context = context;
        this.post = post;
        this.view = view;
    }

    @Override
    protected String doInBackground(String... params) {
        //params[0] post-id
        //params[1] user-id
        //params[2] post-rate

        try {
            URL url = new URL(RATE_BASE_URL);
            String urlParameters = WebServerContract.POST_ID + "=" + params[0] + "&" +
                                    WebServerContract.USER_ID + "=" + params[1] + "&" +
                                    WebServerContract.POST_RATE + "=" + params[2];

            ConnectivityManager connectivityManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            if(networkInfo == null || !networkInfo.isConnected()){
                networkService = false;
                return null;
            }

            httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setDoOutput(true);

            DataOutputStream dataOutputStream = new DataOutputStream(httpURLConnection.getOutputStream());
            dataOutputStream.writeBytes(urlParameters);
            dataOutputStream.flush();
            dataOutputStream.close();

            InputStream inputStream = httpURLConnection.getInputStream();
            StringBuffer stringBuffer = new StringBuffer();

            if(inputStream == null){
                return null;
            }

            bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while((line = bufferedReader.readLine()) != null){
                stringBuffer.append(line + "\n");
            }
            inputStream.close();

            if(stringBuffer.length() == 0) return null;

            Log.v(LOG_TAG, stringBuffer.toString());

            return stringBuffer.toString();

        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        } catch (IOException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }finally{
            if(httpURLConnection != null){
                httpURLConnection.disconnect();
            }
            if(bufferedReader != null){
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    Log.e(LOG_TAG, "Error closing buffer.", e);
                }
            }
        }

        return null;
    }

    @Override
    protected void onPostExecute(String jsonObject) {
        if(jsonObject == null || jsonObject.length() == 0){
            Log.e(LOG_TAG, "ERROR jsonObject");
            return;
        }

        try {
            JSONObject respone = new JSONObject(jsonObject);
            String result = respone.getString(WebServerContract.SERVER);
            if(result.equals(SUCCESS)){
                Log.v(LOG_TAG, "Success rating!");

                int rate = respone.getInt(WebServerContract.POST_RATE);
                int rateValue = respone.getInt(WebServerContract.POST_RATE_VALUE);

                post.setRatingBar(rate);
                post.setRatingValue(rateValue);

                if(holder != null){
                    holder.ratingBar.setRating(post.getRatingBar());
                    holder.textViewRateValue.setText(post.getRatingValue());
                }else{
                    RatingBar rb = (RatingBar) view.findViewById(R.id.scrollview_post_item_ratingBar);
                    TextView rx = (TextView) view.findViewById(R.id.scrollview_post_item_rateValue);

                    rb.setRating(post.getRatingBar());
                    rx.setText(post.getRatingValue());
                }


            }else if(result.equals(FAIL)){
                Log.v(LOG_TAG, "Fail rating!");
            }else{
                Log.v(LOG_TAG, "unknow...");
            }

        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }

        super.onPostExecute(jsonObject);
    }
}
