package com.nthu.softwarestudio.app.vlooog;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Switch;
import android.widget.Toast;

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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static android.widget.Toast.LENGTH_SHORT;

/**
 * Created by doda on 2016/6/21.
 */
public class FriendListDataHandler {
    private Context mContext;
    public Bitmap[] mBitmaps;
    public String[] mLastMsg;
    public Integer[] mUnreadnum;
    public String[] mNamelist;
    public String[] mPhotoList;
    public Integer tmpuid;
    public RecyclerView mRecyclerview;
    public FriendlistAdaptor mRecycleAdapter;
    Integer mState;
    public FriendListDataHandler(Context context, RecyclerView retmp, FriendlistAdaptor atmp){
        mContext=context;
        mRecyclerview=retmp;
        mRecycleAdapter=atmp;
    }
    String forecastJsonStr = null;
    public void connecthttpexs(int uid){
        SenderShell tester=new SenderShell();
        Integer tmp=uid;
        tester.execute(tmp.toString());
    }

    public class SenderShell extends AsyncTask<String, Void, String> {
        private final String LOG_TAG = SenderShell.class.getSimpleName();


        public SenderShell(){
        }


        @Override
        protected void onPostExecute(String s) {
            if(s!=null){
                try{

                    JSONArray dataarray = new JSONArray(s);
                    int j=0;
                    List<String> namelist = new ArrayList<String>();
                    List<String> photolist = new ArrayList<String>();
                    List<Integer> tmpunreadnum = new ArrayList<Integer>();
                    List<String> tmplastmsg = new ArrayList<String>();
                    List<Integer> tmpfrienid = new ArrayList<Integer>();
                    for(j=0;j<dataarray.length();j++){
                        namelist.add(dataarray.getJSONObject(j).getString("friendname"));
                        photolist.add(dataarray.getJSONObject(j).getString("url"));
                        tmpunreadnum.add(dataarray.getJSONObject(j).getInt("unreadnum"));
                        tmplastmsg.add(dataarray.getJSONObject(j).getString("lastmsg"));
                        tmpfrienid.add(dataarray.getJSONObject(j).getInt("fid"));
                        //Log.v("test sender",mNamelist[j]);
                    }
                    mNamelist = namelist.toArray(new String[0]);
                    mPhotoList = photolist.toArray(new String[0]);
                    mLastMsg = tmplastmsg.toArray(new String[0]);
                    mUnreadnum = tmpunreadnum.toArray(new Integer[0]);


                    mRecycleAdapter.SetStringArrayData(mNamelist, mBitmaps, mLastMsg, mUnreadnum, tmpfrienid.toArray(new Integer[0]));
                    mRecyclerview.setAdapter(mRecycleAdapter);
                    //Toast.makeText(mContext,"Complete to update!!!",LENGTH_SHORT).show();
                } catch (JSONException e) {e.printStackTrace();}
            }
        }

        @Override
        protected String doInBackground(String... params) {

            if (params.length == 0) {
                return null;
            }

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            try {
                final String FORECAST_BASE_URL =
                        "http://s103062325.web.2y.idv.tw/javafinal/friend_list_handler.php?";
                final String FIELD1 = "uid";
                final String API_READ_KEY = "key";

                Uri builtUri = Uri.parse(FORECAST_BASE_URL).buildUpon()
                        .appendQueryParameter(FIELD1, params[0])
                        .build();

                URL url = new URL(builtUri.toString());

                Log.v(LOG_TAG, "Built URI " + builtUri.toString());

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

                Log.v(LOG_TAG, "Forecast string: " + forecastJsonStr);
            } catch (IOException e) {
                Log.e(LOG_TAG, "Error ", e);
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
                        Log.e(LOG_TAG, "Error closing stream", e);
                    }
                }
            }

            try{
                JSONArray dataarray = new JSONArray(forecastJsonStr);
                int j=0;
                List<Bitmap> tmpbitarray = new ArrayList<Bitmap>();
                for(j=0;j<dataarray.length();j++){
                    try{
                        URL newurl = new URL(dataarray.getJSONObject(j).getString("url"));
                        try{
                            tmpbitarray.add(BitmapFactory.decodeStream((InputStream) newurl.openConnection().getContent(), null, null));
                        } catch (IOException e){}
                    } catch (MalformedURLException e){}
                    //Log.v("test sender",mNamelist[j]);
                }
                mBitmaps =tmpbitarray.toArray(new Bitmap[0]);
            }catch(JSONException e){e.printStackTrace();}



            // This will only happen if there was an error getting or parsing the forecast.
            return forecastJsonStr;
        }
    }
}
