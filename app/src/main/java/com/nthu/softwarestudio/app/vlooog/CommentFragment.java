package com.nthu.softwarestudio.app.vlooog;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.nthu.softwarestudio.app.vlooog.data.AccountHelper;
import com.nthu.softwarestudio.app.vlooog.data.PostContract;
import com.nthu.softwarestudio.app.vlooog.data.PostHelper;
import com.nthu.softwarestudio.app.vlooog.data.WebServerContract;

import org.json.JSONArray;
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
import java.util.ArrayList;
import java.util.List;


/**
 * Created by Ywuan on 22/06/2016.
 */
public class CommentFragment extends Fragment {
    private final String LOG_TAG = this.getClass().getSimpleName();

    private Post postData;
    List<Comment> Data = new ArrayList<Comment>();

    int userId;
    String profileNickName;
    String userDataPath;

    boolean onClickedButton = false;

    ImageButton imageButtonProfilePicture;
    TextView textViewProfileName;
    ImageView imageViewPostImage;
    RatingBar ratingBar;
    TextView textViewRateValue;
    TextView textViewPostContent;
    TextView commentValue;
    EditText commentEditText;
    Button commentButton;

    RecyclerView recyclerView;
    RecyclerViewCommentAdapter recyclerViewCommentAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_comment, container, false);

        userId = new AccountHelper(getContext()).getUserId();
        profileNickName = new AccountHelper(getContext()).getNickName();
        userDataPath = new AccountHelper(getContext()).getDataPath();

        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        recyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerview_comment_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(
                new LinearLayoutManager(getContext())
        );
        recyclerViewCommentAdapter = new RecyclerViewCommentAdapter(Data);
        recyclerView.setAdapter(recyclerViewCommentAdapter);

        byte[] byteArrayProfilePicture = getActivity().getIntent().getByteArrayExtra(PostContract.POST_PROFILE_PICTURE);
        Bitmap profileBitmap = BitmapFactory.decodeByteArray(byteArrayProfilePicture, 0, byteArrayProfilePicture.length);

        byte[] byteArrayContentImage = getActivity().getIntent().getByteArrayExtra(PostContract.POST_CONTENT_IMAGE);
        Bitmap contentBitmap = BitmapFactory.decodeByteArray(byteArrayContentImage, 0, byteArrayContentImage.length);

        postData = new Post(profileBitmap,
                            getActivity().getIntent().getExtras().getString(PostContract.POST_POFILE_NAME),
                            contentBitmap,
                            getActivity().getIntent().getExtras().getInt(PostContract.POST_RATING),
                            getActivity().getIntent().getExtras().getString(PostContract.POST_RATING_VALUE),
                            getActivity().getIntent().getExtras().getString(PostContract.POST_CONTENT),
                            getActivity().getIntent().getExtras().getString(PostContract.POST_COMMENT),
                            getActivity().getIntent().getExtras().getInt(PostContract.POST_ID),
                            getActivity().getIntent().getExtras().getString(PostContract.POST_DATE),
                            getActivity().getIntent().getExtras().getInt(PostContract.POST_USER_ID)
                );

        imageButtonProfilePicture = (ImageButton) rootView.findViewById(R.id.scrollview_post_item_profile_picture);
        textViewProfileName = (TextView) rootView.findViewById(R.id.scrollview_post_item_profile_name);
        imageViewPostImage = (ImageView) rootView.findViewById(R.id.scrollview_post_item_imageview);
        ratingBar = (RatingBar) rootView.findViewById(R.id.scrollview_post_item_ratingBar);
        textViewRateValue = (TextView) rootView.findViewById(R.id.scrollview_post_item_rateValue);
        textViewPostContent = (TextView) rootView.findViewById(R.id.scrollview_post_content);
        commentValue = (TextView) rootView.findViewById(R.id.scrollview_post_item_comments);
        commentEditText = (EditText) rootView.findViewById(R.id.scrollview_post_comment_editText);
        commentButton = (Button) rootView.findViewById(R.id.scrollview_post_comment_button);

        imageButtonProfilePicture.setImageBitmap(postData.getProfilePicture());
        textViewProfileName.setText(postData.getProfileName());
        imageViewPostImage.setImageBitmap(postData.getContentImage());
        ratingBar.setRating(postData.getRatingBar());
        textViewRateValue.setText(postData.getRatingValue());
        textViewPostContent.setText(postData.getContent());
        commentValue.setText(postData.getComments());

        DownloadComment downloadComment = new DownloadComment();
        downloadComment.execute(String.valueOf(postData.getPostId()));

        if(postData.getPostBy() == userId){
            ratingBar.setIsIndicator(true);

            if(postData.getRatingBar() != 0){
                ratingBar.setRating(postData.getRatingBar());
            }

        }else{
            ratingBar.setIsIndicator(false);

            ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
                @Override
                public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                    if (fromUser) {
                        Rating ratingUpload = new Rating(getContext(), postData, rootView,
                                getActivity().getIntent().getExtras().getInt("recyclerViewPosition"));
                        //Toast.makeText(getContext(), "Rating Changed! " + getActivity().getIntent().getExtras().getInt("recyclerViewPosition"), Toast.LENGTH_SHORT).show();

                        ratingUpload.execute(Integer.toString(postData.getPostId()),
                                Integer.toString(postData.getPostBy()),
                                Integer.toString((int)rating));

                        Toast.makeText(getContext(), "Rating Changed! " + rating, Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }

        commentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Vibrator vibe = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);
                vibe.vibrate(100);

                UploadComment uploadComment = new UploadComment();
                uploadComment.execute(String.valueOf(postData.getPostId()),
                                        String.valueOf(userId),
                                        commentEditText.getText().toString());
                onClickedButton = true;
                commentEditText.setText("");
            }
        });

        return rootView;
    }

    class UploadComment extends AsyncTask<String, Void, String> {
        private final String LOG_TAG = this.getClass().getSimpleName();

        HttpURLConnection urlConnection = null;
        BufferedReader bufferedReader = null;

        final String COMMENT_URL = WebServerContract.BASE_URL + "/comment.php";
        final String SUCCESS = "SUCCESS";
        final String FAILED = "UNFOUND";
        boolean networkService = true;

        Comment comment;

        @Override
        protected String doInBackground(String... params) {
            try {
                URL url = new URL(COMMENT_URL);
                String urlParameters = PostContract.POST_ID + "=" + params[0] + "&" +
                                        PostContract.POST_USER_ID + "=" + params[1] + "&" +
                                        PostContract.POST_COMMENT + "=" + params[2];
                Log.v(LOG_TAG, urlParameters);

                Bitmap profileBitmap;
                final String profileImageBitmapUrl = WebServerContract.BASE_URL + "/" + userDataPath + "/profilephoto/eimage.jpg";
                URL profilePhotoUrl = new URL(profileImageBitmapUrl);
                profileBitmap = BitmapFactory.decodeStream((InputStream) profilePhotoUrl.openConnection().getContent(), null, null);

                comment = new Comment(profileNickName, profileBitmap, params[2]);

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
                Log.e(LOG_TAG, e.getMessage(),e);
                e.printStackTrace();
            } catch (ProtocolException e) {
                Log.e(LOG_TAG, e.getMessage(),e);
                e.printStackTrace();
            } catch (IOException e) {
                Log.e(LOG_TAG, e.getMessage(),e);
                e.printStackTrace();
            } finally{
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
        protected void onPostExecute(String jsonObject) {
            if(jsonObject == null || jsonObject.length() == 0){
                return;
            }

            JSONObject respone = null;
            try {
                Log.v(LOG_TAG, jsonObject);
                respone = new JSONObject(jsonObject);
                String result = respone.getString(WebServerContract.SERVER);
                if(result.equals(SUCCESS)){
                    Data.add(comment);
                    recyclerViewCommentAdapter.updateData(Data);

                }else{
                    Log.e(LOG_TAG, "failed update comment!");
                    return;
                }
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(),e);
                e.printStackTrace();
            }


            super.onPostExecute(jsonObject);
        }
    }

    class DownloadComment extends AsyncTask<String, Void, Void> {
        final String LOG_TAG = this.getClass().getSimpleName();

        HttpURLConnection httpUrlConnection;
        BufferedReader bufferedReader;

        final String SERVER_DATA = "vloooog_data";
        final String EMPTY = "EMPTY";
        final String NONEMPTY = "NON-EMPTY";
        final String SUCCESS = "SUCCESS";
        final String DATA = "data";

        boolean networkService = true;
        Boolean updated = false;

        public DownloadComment() {
        }

        @Override
        protected Void doInBackground(String... params) {
            final String COMMENT_URL = WebServerContract.BASE_URL + "/getcomment.php";

            try {
                URL url = new URL(COMMENT_URL);
                String urlParameters = WebServerContract.POST_ID + "=" + params[0];

                ConnectivityManager connectivityManager = (ConnectivityManager) getContext()
                        .getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
                if(networkInfo == null || !networkInfo.isConnected()){
                    networkService = false;
                    return null;
                }

                httpUrlConnection = (HttpURLConnection) url.openConnection();
                httpUrlConnection.setRequestMethod("POST");
                httpUrlConnection.setDoOutput(true);

                DataOutputStream dataOutputStream = new DataOutputStream(httpUrlConnection.getOutputStream());
                dataOutputStream.writeBytes(urlParameters);
                dataOutputStream.flush();
                dataOutputStream.close();

                InputStream inputStream = httpUrlConnection.getInputStream();
                StringBuffer stringBuffer = new StringBuffer();

                if(inputStream == null){
                    Log.e(LOG_TAG, "inputStream null");
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

                String jsonString = stringBuffer.toString();
                Data = new ArrayList<Comment>();

                if(jsonString.length() == 0){
                    Log.e(LOG_TAG, "json failed!");
                    return null;
                }

                JSONObject vloooog_server = new JSONObject(jsonString);
                String result = vloooog_server.getString(WebServerContract.SERVER);
                if(result.equals(SUCCESS)) {
                    String dataEmptiness = vloooog_server.getString(SERVER_DATA);

                    if(dataEmptiness.equals(NONEMPTY)){
                        JSONArray data = vloooog_server.getJSONArray(DATA);

                        for (int i = 0; i < data.length(); i++) {
                            JSONObject jObject = data.getJSONObject(i);

                            String postContent = jObject.getString(PostContract.POST_COMMENT);
                            String userDataPath = jObject.getString(WebServerContract.USER_DATAPATH);
                            String userNickName = jObject.getString(WebServerContract.NICKNAME);
                            Bitmap profileBitmap;

                            final String profileImageBitmapUrl = WebServerContract.BASE_URL + "/" + userDataPath + "/profilephoto/eimage.jpg";

                            URL profilePhotoUrl = new URL(profileImageBitmapUrl);

                            profileBitmap = BitmapFactory.decodeStream((InputStream) profilePhotoUrl.openConnection().getContent(), null, null);

                            Comment tmpcomment = new Comment(userNickName, profileBitmap, postContent);

                            Data.add(tmpcomment);
                        }
                        updated = true;

                    }else{
                        updated = false;
                    }
                }

            } catch (MalformedURLException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            } catch (ProtocolException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            } catch (IOException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            } finally {
                if (httpUrlConnection != null) {
                    httpUrlConnection.disconnect();
                }
                if (bufferedReader != null) {
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
        protected void onPostExecute(Void aVoid) {
            if(updated){
                recyclerViewCommentAdapter.updateData(Data);
            }
            super.onPostExecute(aVoid);
        }
    }

    class RecyclerViewCommentAdapter extends RecyclerView.Adapter<RecyclerViewCommentAdapter.ViewHolder>{
        List<Comment> Data;

        public class ViewHolder extends RecyclerView.ViewHolder{
            public ImageView profileImage;
            public TextView profileName;
            public TextView commentContent;

            public ViewHolder(View itemView) {
                super(itemView);
                profileImage = (ImageView) itemView.findViewById(R.id.recyclerview_comment_list_item_imageView);
                profileName = (TextView) itemView.findViewById(R.id.recyclerview_comment_list_item_textView);
                commentContent = (TextView) itemView.findViewById(R.id.recyclerview_comment_list_item_textView_Content);
            }
        }

        public RecyclerViewCommentAdapter(List<Comment> Data) {
            this.Data = Data;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.recyclerview_comment_item, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            holder.profileImage.setImageBitmap(Data.get(position).getProfile_image());
            holder.profileName.setText(Data.get(position).getProfile_name());
            holder.commentContent.setText(Data.get(position).getComment_content());
        }

        @Override
        public int getItemCount() {
            return Data.size();
        }

        public void updateData(List<Comment> Data){
            this.Data = Data;
            if(onClickedButton){
                PostHelper postHelper = new PostHelper(getContext());
                postHelper.deleteData();
                int postId = postData.getPostId();
                int rate = postData.getRatingBar();
                int rateValue = Integer.parseInt(postData.getRatingValue_raw());
                int comment = Integer.parseInt(postData.getComments_raw());
                comment++;
                postHelper.insertData(getActivity().getIntent().getExtras().getInt("recyclerViewPosition"), rate, Integer.toString(rateValue), Integer.toString(comment));
                postData.setComments(comment);
                commentValue.setText(postData.getComments());
                onClickedButton = false;
            }
            notifyDataSetChanged();
            recyclerView.smoothScrollToPosition(Data.size());
        }

    }
}
