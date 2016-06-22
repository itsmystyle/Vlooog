package com.nthu.softwarestudio.app.vlooog;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
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

/**
 * Created by Ywuan on 22/06/2016.
 */
public class CommentFragment extends Fragment {
    private final String LOG_TAG = this.getClass().getSimpleName();

    private Post postData;

    int userId;

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



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_comment, container, false);

        userId = new AccountHelper(getContext()).getUserId();

        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

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
                        Rating ratingUpload = new Rating(getContext(), postData, rootView);
                        ratingUpload.execute(Integer.toString(postData.getPostId()),
                                Integer.toString(postData.getPostBy()),
                                Integer.toString((int)rating));

                        Toast.makeText(getContext(), "Rating Changed! " + rating, Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }



        return rootView;
    }

    @Override
    public void onStop() {
        Toast.makeText(getContext(), "Hello" , Toast.LENGTH_SHORT).show();
        super.onStop();
    }

    class RecyclerViewCommentAdapter extends RecyclerView.Adapter<RecyclerViewCommentAdapter.ViewHolder>{

        public class ViewHolder extends RecyclerView.ViewHolder{

            public ViewHolder(View itemView) {
                super(itemView);
            }
        }

        public RecyclerViewCommentAdapter() {
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return null;
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {

        }

        @Override
        public int getItemCount() {
            return 0;
        }

    }
}
