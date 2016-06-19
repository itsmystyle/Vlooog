package com.nthu.softwarestudio.app.vlooog;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.nthu.softwarestudio.app.vlooog.data.AccountHelper;
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
import java.net.ProtocolException;
import java.net.URL;

/**
 * Created by Ywuan on 19/06/2016.
 */
public class LoginFragment extends Fragment {
    private final String LOG_TAG = this.getClass().getSimpleName();

    private EditText mUsername;
    private EditText mPassword;
    private Button mSignInButton;

    public LoginFragment(){ super();}

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_login,container,false);

        mUsername = (EditText) rootView.findViewById(R.id.username_input);
        mPassword = (EditText) rootView.findViewById(R.id.userpassword_input);
        mSignInButton = (Button) rootView.findViewById(R.id.button_signin);

        mSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mUsername.getText().length() == 0 || mPassword.getText().length() == 0){
                    Toast.makeText(getContext(),"Username or Password cannot be empty."
                            , Toast.LENGTH_SHORT).show();
                    return;
                }

                LoginAuthorization loginAuthorization = new LoginAuthorization();
                loginAuthorization.execute(mUsername.getText().toString(),
                        mPassword.getText().toString());
            }
        });

        return rootView;
    }

    public class LoginAuthorization extends AsyncTask<String, Void, String>{
        private final String LOG_TAG = this.getClass().getSimpleName();

        HttpURLConnection urlConnection = null;
        BufferedReader bufferedReader = null;

        final String WEB_SERVER = "vloooog_server";
        final String ERROR = "UNFOUND";
        final String SECURE = "secure";

        final String AUTHORIZED = "AUTHORIZED";
        final String UNAUTHORIZED = "UNAUTHORIZED";

        Boolean networkService = true;

        @Override
        protected String doInBackground(String... params) {
            try{
                final String LOGIN_BASE_URL = WebServerContract.BASE_URL + "/login.php";

                URL url = new URL(LOGIN_BASE_URL);
                String urlParameters = WebServerContract.USER_NAME + "=" + params[0] + "&" +
                                        WebServerContract.USER_PASSWORD + "=" + params[1];

                ConnectivityManager connectivityManager = (ConnectivityManager) getContext()
                                    .getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

                if(networkInfo == null || !networkInfo.isConnected()){
                    networkService = false;
                    return null;
                }

                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("POST");
                urlConnection.setDoOutput(true);

                DataOutputStream dataOutputStream = new DataOutputStream(urlConnection.getOutputStream());
                dataOutputStream.writeBytes(urlParameters);
                dataOutputStream.flush();
                dataOutputStream.close();

                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer stringBuffer = new StringBuffer();
                if(inputStream == null){
                    Log.e(LOG_TAG, "InputStream Error");
                    return null;
                }

                bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = bufferedReader.readLine()) != null){
                    stringBuffer.append(line + "\n");
                }
                inputStream.close();

                if(stringBuffer.length() == 0){
                    Log.e(LOG_TAG, "String Buffer Error");
                    return null;
                }

                return stringBuffer.toString();

            } catch (ProtocolException e) {
                Log.e(LOG_TAG,"ProtocolException", e);
                e.printStackTrace();

            } catch (MalformedURLException e) {
                Log.e(LOG_TAG,"MalformedURLException", e);
                e.printStackTrace();

            } catch (IOException e) {
                Log.e(LOG_TAG,"IOException", e);
                e.printStackTrace();
            } finally {
                if(urlConnection != null){
                    urlConnection.disconnect();
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
        protected void onPostExecute(String b) {
            try{
                if(b == null || b.length() == 0){
                    Log.e(LOG_TAG, "Unable to get json");
                    if(!networkService)
                        Toast.makeText(getContext(),"Unable to connect to internet. Please check for network service."
                                , Toast.LENGTH_SHORT).show();
                    return;
                }

                Log.v(LOG_TAG, b);

                JSONObject respone = new JSONObject(b);
                if(respone.getString(WEB_SERVER).equals(ERROR)){
                    Log.e(LOG_TAG, "Web Server Database Failed! Please contact us to fix this.");
                    Toast.makeText(getContext(),"Web Server Database Failed! Please contact us to fix this."
                            , Toast.LENGTH_SHORT).show();
                    return;
                }

                JSONObject web_server = respone.getJSONObject(WEB_SERVER);
                String secure = web_server.getString(SECURE);
                if(secure.equals(AUTHORIZED)){
                    String access_token = web_server.getString(WebServerContract.ACCESS_TOKEN);
                    int user_id = web_server.getInt(WebServerContract.USER_ID);
                    Log.v(LOG_TAG, access_token + " " + Integer.toString(user_id));

                    AccountHelper accountHelper = new AccountHelper(getContext());
                    accountHelper.deleteData();
                    if(accountHelper.insertData(mUsername.getText().toString(), access_token, user_id))
                        Log.v(LOG_TAG, "inserted to database");
                    else
                        Log.e(LOG_TAG, "failed to insert to database");

                    Intent intent = new Intent(getActivity(), MainActivity.class)
                            .putExtra(WebServerContract.ACCESS_TOKEN, access_token)
                            .putExtra(WebServerContract.USER_ID, user_id)
                            .putExtra(WebServerContract.USER_NAME, mUsername.getText().toString());
                    startActivity(intent);
                    getActivity().finish();

                }else{
                    if(networkService)
                        Toast.makeText(getContext(),"Wrong Username or Password.",
                                Toast.LENGTH_SHORT).show();
                    else
                        Toast.makeText(getContext(),"Unable to connect to internet. Please check for network service."
                                , Toast.LENGTH_SHORT).show();
                }

            } catch (JSONException e) {
                Log.e(LOG_TAG, "JSONException", e);
                e.printStackTrace();
            }
        }
    }
}
