package com.nthu.softwarestudio.app.vlooog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.kosalgeek.android.photoutil.CameraPhoto;
import com.kosalgeek.android.photoutil.GalleryPhoto;
import com.kosalgeek.android.photoutil.ImageLoader;
import com.kosalgeek.android.photoutil.PhotoLoader;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.jar.Manifest;

/**
 * Created by Ywuan on 19/06/2016.
 */
public class MainFragment extends Fragment {
    private final String TAG_LOG = this.getClass().getSimpleName();

    private AlphaAnimation buttonClick = new AlphaAnimation(1F, 0.3F);

    private ImageButton profile_button = null;
    private ImageButton camera_button = null;
    private ImageButton friend_button = null;

    private CameraPhoto cameraPhoto = null;
    private GalleryPhoto galleryPhoto = null;

    final int CAMERA_REQUEST = 6361;
    final int PHOTO_LIB_REQUEST = 7738;
    final int PERMISSION_REQUEST = 8829;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        profile_button = (ImageButton) rootView.findViewById(R.id.main_profile_button);
        camera_button = (ImageButton) rootView.findViewById(R.id.main_camera_button);
        friend_button = (ImageButton) rootView.findViewById(R.id.main_friend_button);

        profile_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.startAnimation(buttonClick);
            }
        });

        camera_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.startAnimation(buttonClick);
                getAccessible();
                final Dialog dialog = new Dialog(getContext(), R.style.AppTheme_Dialog);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.post_dialog);

                Window window = dialog.getWindow();
                window.setLayout(WindowManager.LayoutParams.WRAP_CONTENT,
                        WindowManager.LayoutParams.WRAP_CONTENT);

                final ImageButton cameraImageButton = (ImageButton) dialog.findViewById(R.id.post_camera_button);
                final ImageButton photolibImageButton = (ImageButton) dialog.findViewById(R.id.post_photo_lib_button);

                cameraImageButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        v.startAnimation(buttonClick);

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
                        v.startAnimation(buttonClick);

                        if(galleryPhoto != null){
                            startActivityForResult(galleryPhoto.openGalleryIntent(), PHOTO_LIB_REQUEST);
                        }
                    }
                });

                if(cameraPhoto != null || galleryPhoto != null) dialog.show();
            }

        });

        friend_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.startAnimation(buttonClick);
            }
        });

        return  rootView;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == Activity.RESULT_OK){
            if(requestCode == CAMERA_REQUEST){
                String photoPath = cameraPhoto.getPhotoPath();
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
                            dialog.onBackPressed();
                        }
                    });

                    postButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            v.startAnimation(buttonClick);
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
                            dialog.onBackPressed();
                        }
                    });

                    postButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            v.startAnimation(buttonClick);
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
}
