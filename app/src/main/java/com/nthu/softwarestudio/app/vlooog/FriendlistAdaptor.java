package com.nthu.softwarestudio.app.vlooog;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nthu.softwarestudio.app.vlooog.data.AccountHelper;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by doda on 2016/6/21.
 */
public  class FriendlistAdaptor extends RecyclerView.Adapter<FriendlistAdaptor.ViewHolder> {
    private String[] mDataset;
    private String[] friendlisttext={"loading...","loading...","loading..."};
    private String[] friendphototext={"loading...","loading...","loading..."};
    private String[] lastmsgtmp;
    private Integer[] unreadtmp;
    private Bitmap[] friendphoto;
    private Integer[] mFriendid;
    private Context mContext;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        // each data item is just a string in this case
        public TextView mTextView;
        public ImageView mImageView;
        public TextView mLastMsgView;
        public TextView mUnreadNum;
        public Integer FriendId;
        public String FriendName;
        public ViewHolder(View v) {
            super(v);
            v.setOnClickListener(this);

            //v.setBackgroundResource(R.attr.selectableItemBackground);
            mTextView=(TextView) v.findViewById(R.id.frien_list_name_text);
            mImageView=(ImageView)v.findViewById(R.id.recyclerview_imageView);
            mLastMsgView=(TextView) v.findViewById(R.id.recyclerview_latest);
            mUnreadNum=(TextView)v.findViewById(R.id.recyclerview_unread);
        }

        @Override
        public void onClick(View v) {
            Log.v("click test",FriendId.toString());
            Intent intent = new Intent(mContext, ChatActivity.class);
            intent.putExtra("yourFriendID",FriendId);
            intent.putExtra("yourFriendName",FriendName);
            mContext.startActivity(intent);
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public FriendlistAdaptor(Context context) {
        mContext=context;

    }
    public void SetStringArrayData(String[] myfriendlisttext, Bitmap[] myfriendphototext , String[] msgtmp1, Integer[] unreadnumtmp1, Integer[] useridtmp){
        friendlisttext = myfriendlisttext;
        friendphoto = myfriendphototext;
        unreadtmp=unreadnumtmp1;
        lastmsgtmp=msgtmp1;
        mFriendid=useridtmp;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public FriendlistAdaptor.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                   int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.friend_list_item, parent, false);
        // set the view's size, margins, paddings and layout parameters

        ViewHolder vh = new ViewHolder((LinearLayout) v);

        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        holder.mTextView.setText(friendlisttext[position]);
        holder.FriendName=friendlisttext[position];
        holder.mImageView.setImageBitmap(friendphoto[position]);
        holder.mLastMsgView.setText(lastmsgtmp[position]);
        if(unreadtmp[position] == 0){
            holder.mLastMsgView.setTextColor(Color.rgb(162, 162, 162));
            holder.mLastMsgView.setTypeface(null, Typeface.NORMAL);
        }
        holder.mUnreadNum.setText(unreadtmp[position].toString());
        //holder.mUnreadNum.setText(mFriendid[position].toString());
        holder.FriendId=mFriendid[position];

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return friendlisttext.length;
    }
}
