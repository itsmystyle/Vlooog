package com.nthu.softwarestudio.app.vlooog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.kosalgeek.android.photoutil.CameraPhoto;
import com.kosalgeek.android.photoutil.GalleryPhoto;
import com.kosalgeek.android.photoutil.ImageLoader;
import com.nthu.softwarestudio.app.vlooog.data.AccountHelper;
import com.nthu.softwarestudio.app.vlooog.data.FriendListActivity;
import com.nthu.softwarestudio.app.vlooog.data.PostContract;
import com.nthu.softwarestudio.app.vlooog.data.PostHelper;
import com.nthu.softwarestudio.app.vlooog.data.WebServerContract;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ywuan on 19/06/2016.
 */
public class MainFragment extends Fragment {
    private final String TAG_LOG = this.getClass().getSimpleName();

    private AlphaAnimation buttonClick = new AlphaAnimation(1F, 0.3F);
    private AlphaAnimation buttonClick2 = new AlphaAnimation(0.3F, 1F);

    int userId;

    private SwipeRefreshLayout swipeContainer;

    private ImageButton profile_button = null;
    private ImageButton camera_button = null;
    private ImageButton friend_button = null;

    private CameraPhoto cameraPhoto = null;
    private GalleryPhoto galleryPhoto = null;

    String path = null;

    List<Post> Data = new ArrayList<Post>();

    final int CAMERA_REQUEST = 6361;
    final int PHOTO_LIB_REQUEST = 7738;
    final int PERMISSION_REQUEST = 8829;

    RecyclerView recyclerView;
    RecyclerViewPostAdapter recyclerViewPostAdapter;

    Dialog cameraPhotoDialog;

    boolean onUploadCompleted = false;

    @Override
    public void onResume() {
        super.onResume();
        //DownloadPost downloadPost = new DownloadPost();
        //downloadPost.execute();
        PostHelper postHelper = new PostHelper(getContext());
        if(postHelper != null && !postHelper.isEmpty()) {
            //Log.v(TAG_LOG, "update!");
            //Log.v(TAG_LOG, "position =" + postHelper.getPostId() + " size = " + Data.size());
            //Log.v(TAG_LOG, String.valueOf(postHelper.getPostId()));
            if(Data.size() > postHelper.getPostId()){
                Data.get(postHelper.getPostId()).setRatingBar(postHelper.getRate());
                Data.get(postHelper.getPostId()).setRatingValue(postHelper.getRateValue());
                Data.get(postHelper.getPostId()).setComments(postHelper.getComment());
                Log.v(TAG_LOG, postHelper.getComment());
            }
        }
        recyclerViewPostAdapter.updateData(Data);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        userId = new AccountHelper(getContext()).getUserId();

        swipeContainer = (SwipeRefreshLayout) rootView.findViewById(R.id.recyclerview_swipe);
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {

            @Override
            public void onRefresh() {
                DownloadPost downloadPost = new DownloadPost();
                downloadPost.execute();
            }
        });

        DownloadPost downloadPost = new DownloadPost();
        downloadPost.execute();

        recyclerView = (RecyclerView) rootView.findViewById(R.id.main_recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(
                new LinearLayoutManager(getContext())
        );
        recyclerViewPostAdapter = new RecyclerViewPostAdapter(Data, getContext());
        recyclerView.setAdapter(recyclerViewPostAdapter);

        profile_button = (ImageButton) rootView.findViewById(R.id.main_profile_button);
        camera_button = (ImageButton) rootView.findViewById(R.id.main_camera_button);
        friend_button = (ImageButton) rootView.findViewById(R.id.main_friend_button);

        profile_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.startAnimation(buttonClick);
                Intent intent = new Intent(getContext(), ProfileActivity.class);
                startActivity(intent);
            }
        });

        camera_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.startAnimation(buttonClick);
                getAccessible();
                cameraPhotoDialog = new Dialog(getContext(), R.style.AppTheme_Dialog);
                cameraPhotoDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                cameraPhotoDialog.setContentView(R.layout.post_dialog);

                Window window = cameraPhotoDialog.getWindow();
                window.setLayout(WindowManager.LayoutParams.WRAP_CONTENT,
                        WindowManager.LayoutParams.WRAP_CONTENT);

                final ImageButton cameraImageButton = (ImageButton) cameraPhotoDialog.findViewById(R.id.post_camera_button);
                final ImageButton photolibImageButton = (ImageButton) cameraPhotoDialog.findViewById(R.id.post_photo_lib_button);

                cameraImageButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        v.startAnimation(buttonClick2);

                        if(cameraPhoto != null){
                            try {
                                startActivityForResult(cameraPhoto.takePhotoIntent(), CAMERA_REQUEST);
                            } catch (IOException e) {
                                Log.e(TAG_LOG, "Camera Error", e);
                                e.printStackTrace();
                            }
                        }
                    }
                });

                photolibImageButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        v.startAnimation(buttonClick2);

                        if(galleryPhoto != null){
                            startActivityForResult(galleryPhoto.openGalleryIntent(), PHOTO_LIB_REQUEST);
                        }
                    }
                });

                if(cameraPhoto != null || galleryPhoto != null) {
                    cameraPhotoDialog.show();
                }else{
                    cameraPhotoDialog.dismiss();
                }
            }

        });

        friend_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.startAnimation(buttonClick);
                Intent intent = new Intent(getContext(), FriendListActivity.class);
                startActivity(intent);
            }
        });

        return  rootView;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == Activity.RESULT_OK){
            if(requestCode == CAMERA_REQUEST){
                String photoPath = cameraPhoto.getPhotoPath();
                path = photoPath;
                try {
                    final Dialog dialog = new Dialog(getContext(), R.style.AppTheme_Dialog);
                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    dialog.setContentView(R.layout.post_content_dialog);

                    Window window = dialog.getWindow();
                    window.setLayout(WindowManager.LayoutParams.MATCH_PARENT,
                            WindowManager.LayoutParams.WRAP_CONTENT);

                    final Button postButton = (Button) dialog.findViewById(R.id.post_button);
                    final Button cancelButton = (Button) dialog.findViewById(R.id.cancel_post_button);
                    final ImageView contentImageView = (ImageView) dialog.findViewById(R.id.post_imageview);
                    final EditText contentEditText = (EditText) dialog.findViewById(R.id.post_editText);

                    Bitmap bitmap = ImageLoader.init().from(photoPath).requestSize(512, 512).getBitmap();
                    int nh = (int) ( bitmap.getHeight() * (512.0 / bitmap.getWidth()) );
                    Bitmap scaled = Bitmap.createScaledBitmap(bitmap, 512, nh, true);

                    contentImageView.setImageBitmap(scaled);

                    cancelButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            v.startAnimation(buttonClick);
                            Vibrator vibe = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);
                            vibe.vibrate(100);

                            dialog.onBackPressed();
                        }
                    });

                    postButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            v.startAnimation(buttonClick);
                            Vibrator vibe = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);
                            vibe.vibrate(100);

                            onUploadCompleted = false;
                            UploadPost uploadPost = new UploadPost();
                            uploadPost.execute(contentEditText.getText().toString());

                            DownloadPost downloadPost = new DownloadPost();
                            downloadPost.execute();

                            dialog.onBackPressed();
                            cameraPhotoDialog.onBackPressed();
                        }
                    });

                    dialog.show();

                } catch (FileNotFoundException e) {
                    Log.e(TAG_LOG, "FileNotFoundException", e);
                    e.printStackTrace();
                }
            }else if(requestCode == PHOTO_LIB_REQUEST){
                Uri uri = data.getData();
                galleryPhoto.setPhotoUri(uri);
                String photoPath = galleryPhoto.getPath();
                path = photoPath;
                try{
                    final Dialog dialog = new Dialog(getContext(), R.style.AppTheme_Dialog);
                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    dialog.setContentView(R.layout.post_content_dialog);

                    Window window = dialog.getWindow();
                    window.setLayout(WindowManager.LayoutParams.MATCH_PARENT,
                            WindowManager.LayoutParams.WRAP_CONTENT);

                    final Button postButton = (Button) dialog.findViewById(R.id.post_button);
                    final Button cancelButton = (Button) dialog.findViewById(R.id.cancel_post_button);
                    final ImageView contentImageView = (ImageView) dialog.findViewById(R.id.post_imageview);
                    final EditText contentEditText = (EditText) dialog.findViewById(R.id.post_editText);

                    Bitmap bitmap = ImageLoader.init().from(photoPath).requestSize(512, 512).getBitmap();
                    int nh = (int) ( bitmap.getHeight() * (512.0 / bitmap.getWidth()) );
                    Bitmap scaled = Bitmap.createScaledBitmap(bitmap, 512, nh, true);

                    contentImageView.setImageBitmap(scaled);

                    cancelButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            v.startAnimation(buttonClick);
                            Vibrator vibe = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);
                            vibe.vibrate(100);

                            dialog.onBackPressed();
                        }
                    });

                    postButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            v.startAnimation(buttonClick);
                            Vibrator vibe = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);
                            vibe.vibrate(100);

                            onUploadCompleted = false;
                            UploadPost uploadPost = new UploadPost();
                            uploadPost.execute(contentEditText.getText().toString());

                            DownloadPost downloadPost = new DownloadPost();
                            downloadPost.execute();

                            dialog.onBackPressed();
                            cameraPhotoDialog.onBackPressed();
                        }
                    });

                    dialog.show();

                } catch (FileNotFoundException e) {
                    Log.e(TAG_LOG, "FileNotFoundException", e);
                    e.printStackTrace();
                }
            }
        }
    }

    private void getAccessible() {
        boolean shouldShow = false;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if ((ContextCompat.checkSelfPermission(getContext(),
                    android.Manifest.permission.CAMERA)
                    != PackageManager.PERMISSION_GRANTED) ||
                    (ContextCompat.checkSelfPermission(getContext(),
                            android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            != PackageManager.PERMISSION_GRANTED) ||
                    (ContextCompat.checkSelfPermission(getContext(),
                            android.Manifest.permission.READ_EXTERNAL_STORAGE)
                            != PackageManager.PERMISSION_GRANTED)) {
                String camera = null, external_storage = null;
                if (shouldShowRequestPermissionRationale(android.Manifest.permission.CAMERA)) {
                    camera = "camera";
                    shouldShow = true;
                }
                if (shouldShowRequestPermissionRationale(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) ||
                        shouldShowRequestPermissionRationale(android.Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    external_storage = "external storage";
                    shouldShow = true;
                }
                if(shouldShow)
                    Toast.makeText(getContext(), "Need permission to access " + camera + " " + external_storage ,
                            Toast.LENGTH_SHORT).show();

                requestPermissions(new String[]{
                        android.Manifest.permission.CAMERA,
                        android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        android.Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST);
            } else {
                cameraPhoto = new CameraPhoto(getActivity().getApplicationContext());
                galleryPhoto = new GalleryPhoto(getActivity().getApplicationContext());
            }
        } else {
            cameraPhoto = new CameraPhoto(getActivity().getApplicationContext());
            galleryPhoto = new GalleryPhoto(getActivity().getApplicationContext());
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == PERMISSION_REQUEST){
            if(grantResults.length == 3 && (
                    grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                    grantResults[1] == PackageManager.PERMISSION_GRANTED &&
                    grantResults[2] == PackageManager.PERMISSION_GRANTED
                    )){
                Log.v(TAG_LOG, permissions[0] + " " + permissions[1] + " " + permissions[2]);
                cameraPhoto = new CameraPhoto(getActivity().getApplicationContext());
                galleryPhoto = new GalleryPhoto(getActivity().getApplicationContext());
            }else{
                Toast.makeText(getContext(), "Unable to permission access.", Toast.LENGTH_SHORT).show();
                return;
            }
        }else{
            Toast.makeText(getContext(), "Unable to permission access.", Toast.LENGTH_SHORT).show();
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    class UploadPost extends AsyncTask<String, Void, String> {
        private final String LOG_TAG = this.getClass().getSimpleName();

        HttpURLConnection urlConnection = null;
        BufferedReader bufferedReader = null;

        final String POST_URL = WebServerContract.BASE_URL + "/post.php";
        int userid = -1;
        String accesstoken = null;
        Bitmap imageToUpload = null;
        String encodedImage = null;

        boolean networkService = true;

        public UploadPost() {
            AccountHelper accountHelper = new AccountHelper(getContext());
            userid = accountHelper.getUserId();
            accesstoken = accountHelper.getAccessToken();
            try {
                imageToUpload = ImageLoader.init().from(path).requestSize(512, 512).getBitmap();
                int nh = (int) ( imageToUpload.getHeight() * (512.0 / imageToUpload.getWidth()) );
                Bitmap scaled = Bitmap.createScaledBitmap(imageToUpload, 512, nh, true);
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                scaled.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
                encodedImage = Base64.encodeToString(byteArrayOutputStream.toByteArray(), Base64.DEFAULT);

            } catch (FileNotFoundException e) {
                Log.e(TAG_LOG, "FileNotFoundException", e);
                e.printStackTrace();
            }
        }

        @Override
        protected String doInBackground(String... params) {
            try{
                URL url = new URL(POST_URL);
                String urlParameters = WebServerContract.USER_ID + "=" + userid + "&" +
                                        WebServerContract.ACCESS_TOKEN + "=" + accesstoken + "&" +
                                        WebServerContract.POST_CONTENT + "=" + params[0] + "&" +
                                        WebServerContract.POST_IMAGE + "=" + encodedImage;

                //Log.v(LOG_TAG, urlParameters);

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

            }catch (MalformedURLException e){
                Log.e(LOG_TAG,e.getMessage(),e);
            }catch (IOException e){
                Log.e(LOG_TAG,e.getMessage(),e);
            }finally{
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
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(String jsonObject) {
            try {
                if(jsonObject == null || jsonObject.length() == 0){
                    return;
                }
                JSONObject respone = new JSONObject(jsonObject);
                String result = respone.getString("vloooog_server");

                if(result.equals("success")){
                    Log.v(LOG_TAG, result);
                    //Intent intent = new Intent(getContext(), MainActivity.class);
                    //getActivity().finish();
                    //getContext().startActivity(intent);
                    onUploadCompleted = true;
                    return;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    class DownloadPost extends AsyncTask<Void, Void, Void>{
        final String LOG_TAG = this.getClass().getSimpleName();

        final String SUCCESS = "success";

        HttpURLConnection httpURLConnection = null;
        BufferedReader bufferedReader = null;

        Boolean networkService = true;
        Boolean updated = false;

        @Override
        protected Void doInBackground(Void... params) {
            final String POST_URL = WebServerContract.BASE_URL + "/userdata.php";

            try {
                URL url = new URL(POST_URL);

                ConnectivityManager connectivityManager = (ConnectivityManager) getContext()
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
                dataOutputStream.writeBytes("");
                dataOutputStream.flush();
                dataOutputStream.close();

                InputStream inputStream = httpURLConnection.getInputStream();
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

                Log.v(LOG_TAG, stringBuffer.toString());

                String jsonString = stringBuffer.toString();
                Data = new ArrayList<Post>();

                if(jsonString.length() == 0){
                    Log.e(LOG_TAG, "json failed!");
                    return null;
                }

                JSONObject vloooog_server = new JSONObject(jsonString);
                String result = vloooog_server.getString(WebServerContract.SERVER);
                if(result.equals(SUCCESS)) {
                    JSONArray data = vloooog_server.getJSONArray("data");

                    for (int i = 0; i < data.length(); i++) {
                        JSONObject jObject = data.getJSONObject(i);

                        int postId = jObject.getInt(WebServerContract.POST_ID);
                        String postContent = jObject.getString(WebServerContract.POST_CONTENT);
                        String postImageName = jObject.getString(WebServerContract.POST_IMAGENAME);
                        int postRate = jObject.getInt(WebServerContract.POST_RATE);
                        int postRateValue = jObject.getInt(WebServerContract.POST_RATE_VALUE);
                        int postComments = jObject.getInt(WebServerContract.POST_COMMENTS);
                        String postDate = jObject.getString(WebServerContract.POST_DATE);
                        String userDataPath = jObject.getString(WebServerContract.USER_DATAPATH);
                        String userNickName = jObject.getString(WebServerContract.NICKNAME);
                        int userId = jObject.getInt(WebServerContract.USER_ID);
                        Bitmap profileBitmap;
                        Bitmap imageContentBitmap;

                        final String profileImageBitmapUrl = WebServerContract.BASE_URL + "/" + userDataPath + "/profilephoto/eimage.jpg";
                        final String postContentImageBitmapUrl = WebServerContract.BASE_URL + "/" + userDataPath + "/" + postImageName + ".jpeg";

                        URL profilePhotoUrl = new URL(profileImageBitmapUrl);
                        URL postContentPhotoUrl = new URL(postContentImageBitmapUrl);

                        profileBitmap = BitmapFactory.decodeStream((InputStream) profilePhotoUrl.openConnection().getContent(), null, null);
                        imageContentBitmap = BitmapFactory.decodeStream((InputStream) postContentPhotoUrl.openConnection().getContent(), null, null);

                        Post tmppost = new Post(profileBitmap, userNickName, imageContentBitmap, postRate, postRateValue, postContent, postComments, postId, postDate, userId);

                        Data.add(tmppost);
                    }
                    updated = true;
                }

            } catch (MalformedURLException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            } catch (IOException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            if(updated){
                recyclerViewPostAdapter.updateData(Data);
            }
            super.onPostExecute(aVoid);
            swipeContainer.setRefreshing(false);
        }
    }

    class RecyclerViewPostAdapter extends RecyclerView.Adapter<RecyclerViewPostAdapter.ViewHolder>{
        private final String LOG_TAG = this.getClass().getSimpleName();

        List<Post> Data;

        Context mContext;

        public RecyclerViewPostAdapter(List<Post> data, Context context) {
            Data = data;
            mContext = context;
        }

        public class ViewHolder extends RecyclerView.ViewHolder{
            ImageButton imageButtonProfilePicture;
            TextView textViewProfileName;
            ImageView imageViewPostImage;
            RatingBar ratingBar;
            TextView textViewRateValue;
            TextView textViewPostContent;
            FrameLayout frameLayoutCommentButton;
            TextView commentValue;
            Context context;

            public ViewHolder(View itemView, final Context context) {
                super(itemView);
                this.context = context;
                imageButtonProfilePicture = (ImageButton) itemView.findViewById(R.id.recyclerview_post_item_profile_picture);
                textViewProfileName = (TextView) itemView.findViewById(R.id.recyclerview_post_item_profile_name);
                imageViewPostImage = (ImageView) itemView.findViewById(R.id.recyclerview_post_item_imageview);
                ratingBar = (RatingBar) itemView.findViewById(R.id.recyclerview_post_item_ratingBar);
                textViewRateValue = (TextView) itemView.findViewById(R.id.recyclerview_post_item_rateValue);
                textViewPostContent = (TextView) itemView.findViewById(R.id.recyclerview_post_content);
                frameLayoutCommentButton = (FrameLayout) itemView.findViewById(R.id.recyclerview_comment_framelayout);
                commentValue = (TextView) itemView.findViewById(R.id.recyclverview_post_item_comments);
            }
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.recyclerview_post_item, parent, false);
            return new ViewHolder(view, mContext);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, final int position) {
            holder.imageButtonProfilePicture.setImageBitmap(Data.get(position).getProfilePicture());
            holder.textViewProfileName.setText(Data.get(position).getProfileName());
            holder.imageViewPostImage.setImageBitmap(Data.get(position).getContentImage());
            holder.ratingBar.setRating(Data.get(position).getRatingBar());
            holder.textViewRateValue.setText(Data.get(position).getRatingValue());
            holder.textViewPostContent.setText(Data.get(position).getContent());
            holder.commentValue.setText(Data.get(position).getComments());

            if(Data.get(position).getPostBy() == userId){
                holder.ratingBar.setIsIndicator(true);

                if(Data.get(position).getRatingBar() != 0){
                    holder.ratingBar.setRating(Data.get(position).getRatingBar());
                }

            }else{
                holder.ratingBar.setIsIndicator(false);

                holder.ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
                    @Override
                    public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                        if (fromUser) {
                            Rating ratingUpload = new Rating(getContext(), Data.get(position), holder);
                            ratingUpload.execute(Integer.toString(Data.get(position).getPostId()),
                                                    Integer.toString(Data.get(position).getPostBy()),
                                    Integer.toString((int)rating));


                            notifyDataSetChanged();

                            Toast.makeText(getContext(), "Rating Changed! " + rating, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }

            holder.frameLayoutCommentButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    v.startAnimation(buttonClick);

                    Intent intent = new Intent(mContext, CommentActivity.class);

                    intent.putExtra(PostContract.POST_POFILE_NAME, Data.get(position).getProfileName());
                    intent.putExtra(PostContract.POST_CONTENT, Data.get(position).getContent());
                    intent.putExtra(PostContract.POST_RATING, Data.get(position).getRatingBar());
                    intent.putExtra(PostContract.POST_RATING_VALUE, Data.get(position).getRatingValue_raw());
                    intent.putExtra(PostContract.POST_COMMENT, Data.get(position).getComments_raw());
                    intent.putExtra(PostContract.POST_DATE, Data.get(position).getDate());
                    intent.putExtra(PostContract.POST_ID, Data.get(position).getPostId());
                    intent.putExtra(PostContract.POST_USER_ID, Data.get(position).getPostBy());
                    intent.putExtra("recyclerViewPosition", position);

                    ByteArrayOutputStream streamProfilePicture = new ByteArrayOutputStream();
                    Data.get(position).getProfilePicture().compress(Bitmap.CompressFormat.JPEG, 100, streamProfilePicture);
                    byte[] byteArrayProfilePicture = streamProfilePicture.toByteArray();

                    intent.putExtra(PostContract.POST_PROFILE_PICTURE, byteArrayProfilePicture);

                    ByteArrayOutputStream streamContentPicture = new ByteArrayOutputStream();
                    Data.get(position).getContentImage().compress(Bitmap.CompressFormat.JPEG, 100, streamContentPicture);
                    byte[] byteArrayContentPicture = streamContentPicture.toByteArray();

                    intent.putExtra(PostContract.POST_CONTENT_IMAGE, byteArrayContentPicture);


                    startActivity(intent);
                }
            });
        }

        @Override
        public int getItemCount() {
            return Data.size();
        }

        public void updateData(List<Post> DataSet){
            this.Data = DataSet;
            notifyDataSetChanged();
        }

        public void clear() {
            Data.clear();
            notifyDataSetChanged();
        }

        public void addAll(List<Post> list) {
            Data.addAll(list);
            notifyDataSetChanged();
        }

    }
}
