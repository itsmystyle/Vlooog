package com.nthu.softwarestudio.app.vlooog;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.nthu.softwarestudio.app.vlooog.data.AccountHelper;
import com.nthu.softwarestudio.app.vlooog.data.PostHelper;
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

public class ProfileFragment extends Fragment {

    Bitmap profileBitmap;

    ImageView profilePicture;
    TextView profileName;
    TextView profileEmail;
    Button friendsButton;
    Button accountSettingButton;
    Button logoutButton;

    AccountHelper accountHelper;
    Dialog dialog;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_profile, container, false);

        accountHelper = new AccountHelper(getContext());

        DownloadProfilePicture downloadProfilePicture = new DownloadProfilePicture();
        downloadProfilePicture.execute(accountHelper.getDataPath());

        profilePicture = (ImageView) rootView.findViewById(R.id.profile_image_imageView);
        profileName = (TextView) rootView.findViewById(R.id.profile_nickname_textView);
        profileEmail = (TextView) rootView.findViewById(R.id.profile_email_textView);
        friendsButton = (Button) rootView.findViewById(R.id.profile_friends_button);
        accountSettingButton = (Button) rootView.findViewById(R.id.profile_account_setting_button);
        logoutButton = (Button) rootView.findViewById(R.id.profile_logout_button);

        profileName.setText(accountHelper.getNickName());
        profileEmail.setText(accountHelper.getEmail());

        friendsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        accountSettingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog = new Dialog(getContext(), R.style.AppTheme_Dialog);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.account_setting_dialog);

                Window window = dialog.getWindow();
                window.setLayout(WindowManager.LayoutParams.MATCH_PARENT,
                        WindowManager.LayoutParams.WRAP_CONTENT);

                final EditText oldPassword = (EditText) dialog.findViewById(R.id.account_setting_dialog_old_password);
                final EditText newPassword = (EditText) dialog.findViewById(R.id.account_setting_dialog_new_password);
                final EditText reenterNewPassword = (EditText) dialog.findViewById(R.id.account_setting_dialog_reenter_new_password);
                final Button sumbitButton = (Button) dialog.findViewById(R.id.account_setting_submit_button);

                sumbitButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String oldPasswordStr = oldPassword.getText().toString();
                        String newPasswordStr = newPassword.getText().toString();
                        String reenterNewPasswordStr = reenterNewPassword.getText().toString();

                        if(oldPasswordStr == null || oldPasswordStr.length() == 0 ||
                                newPasswordStr == null || newPasswordStr.length() == 0 ||
                                reenterNewPasswordStr == null || newPasswordStr.length() == 0){
                            Toast.makeText(getContext(), "Password fields cannot be empty!", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        if((newPasswordStr.length() != reenterNewPasswordStr.length()) || (!newPasswordStr.equals(reenterNewPasswordStr))){
                            Toast.makeText(getContext(), "New password and Re-enter new password must be same!", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        UpdateProfilePassword updateProfilePassword = new UpdateProfilePassword();
                        updateProfilePassword.execute(oldPasswordStr, newPasswordStr, accountHelper.getAccessToken());

                    }
                });

                dialog.show();
            }
        });

        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AccountHelper accountHelper = new AccountHelper(getContext());
                accountHelper.deleteData();
                PostHelper postHelper = new PostHelper(getContext());
                postHelper.deleteData();

                Intent intent = new Intent(getContext(), LoginActivity.class);
                intent.putExtra("finish", true);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                        Intent.FLAG_ACTIVITY_CLEAR_TASK |
                        Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                getActivity().finish();
            }
        });


        return rootView;
    }

    class UpdateProfilePassword extends AsyncTask<String, Void, String>{
        final String LOG_TAG = this.getClass().getSimpleName();

        HttpURLConnection urlConnection;
        BufferedReader bufferedReader;

        final String ACCOUNT_SETTING_URL = WebServerContract.BASE_URL + "/updatePassword.php";
        final String SECURE = "secure";
        final String SUCCESS = "SUCCESS";
        final String WRONG_PASSWORD = "WRONG_OLD_PASSWORD";
        final String UPDATED_PASSWORD = "PASSWORD_UPDATED";

        boolean networkService = true;

        @Override
        protected String doInBackground(String... params) {
            try {
                URL url = new URL(ACCOUNT_SETTING_URL);
                String urlParameters = WebServerContract.OLD_PASSWORD + "=" + params[0] + "&" +
                                        WebServerContract.NEW_PASSWORD + "=" + params[1] + "&" +
                                        WebServerContract.ACCESS_TOKEN + "=" + params[2];

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
                    return null;
                }

                bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while((line = bufferedReader.readLine()) != null){
                    stringBuffer.append(line + "\n");
                }
                inputStream.close();

                if(stringBuffer.length() == 0) return null;

                return stringBuffer.toString();

            } catch (MalformedURLException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            } catch (ProtocolException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            } catch (IOException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String jsonObject) {
            if(jsonObject == null || jsonObject.length() == 0){
                Log.e(LOG_TAG, "Failed updated password, json fail.");
                return;
            }

            try {
                JSONObject respone = new JSONObject(jsonObject);
                String result = respone.getString(WebServerContract.SERVER);

                if(result.equals(SUCCESS)){
                    String secure = respone.getString(SECURE);

                    if(secure != null && secure.length() != 0 && secure.equals(UPDATED_PASSWORD)){
                        Toast.makeText(getContext(), "Password update succesfully. Please logout and login again.", Toast.LENGTH_SHORT).show();
                        dialog.onBackPressed();
                    }else{
                        Toast.makeText(getContext(), "Password update failed. Wrong old password.", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(getContext(), "Password update failed. Please contact us to fix this.", Toast.LENGTH_SHORT).show();
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

            super.onPostExecute(jsonObject);
        }
    }

    class DownloadProfilePicture extends AsyncTask<String, Void, Void>{
        final String LOG_TAG = this.getClass().getSimpleName();

        public DownloadProfilePicture() {
        }

        @Override
        protected Void doInBackground(String... params) {
            try {

                final String profileImageBitmapUrl = WebServerContract.BASE_URL + "/" + params[0] + "/profilephoto/eimage.jpg";
                URL profilePhotoUrl = new URL(profileImageBitmapUrl);
                profileBitmap = BitmapFactory.decodeStream((InputStream) profilePhotoUrl.openConnection().getContent(), null, null);

            } catch (MalformedURLException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            } catch (IOException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            if(profileBitmap != null){
                profilePicture.setImageBitmap(profileBitmap);
            }

            super.onPostExecute(aVoid);
        }
    }
}
