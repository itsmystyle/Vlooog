package com.nthu.softwarestudio.app.vlooog;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.v7.graphics.drawable.DrawableWrapper;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by doda on 2016/6/22.
 */
public class MsgListAdaptor extends RecyclerView.Adapter<MsgListAdaptor.ViewHolder> {

    private String[] mThisMsgLog;
    private Integer[] mIdArray;
    private Integer mUserId;
    private List<Integer> tmpidarraylist;
    private List<String> new_loglist;

    public MsgListAdaptor(Integer tmpid){
        tmpidarraylist = new ArrayList<Integer>();
        new_loglist = new ArrayList<String>();
        mUserId = tmpid;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        public TextView OneMsgLine;
        public FrameLayout out_frame;
        public Integer uid;

        public ViewHolder(View v) {
            super(v);
            OneMsgLine = (TextView) v.findViewById(R.id.msg_view_id);
            out_frame = (FrameLayout) v.findViewById(R.id.out_frame);
        }
    }

    public void addMsg(Integer tmpidarray ,String new_log){
        tmpidarraylist.add(tmpidarray);
        new_loglist.add(new_log);
        //mThisMsgLog= new_loglist.toArray(new String[0]);
        //mIdArray = tmpidarraylist.toArray(new Integer[0]);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.msgline, parent, false);
        // set the view's size, margins, paddings and layout parameters

        ViewHolder vh = new ViewHolder(v);

        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.OneMsgLine.setText(new_loglist.get(position));
        holder.OneMsgLine.setHorizontallyScrolling(true);
        holder.uid=tmpidarraylist.get(position);

        if(holder.uid.equals(mUserId)){
            holder.OneMsgLine.setBackgroundResource(R.drawable.rounded_corner_me);
            FrameLayout.LayoutParams params2 = (FrameLayout.LayoutParams)holder.OneMsgLine.getLayoutParams();
            params2.gravity = Gravity.RIGHT;
            holder.OneMsgLine.setLayoutParams(params2);
            //Log.v("test", tmp.toString());
        }
        else {
            holder.OneMsgLine.setBackgroundResource(R.drawable.rounded_corner);
            FrameLayout.LayoutParams params2 = (FrameLayout.LayoutParams)holder.OneMsgLine.getLayoutParams();
            params2.gravity = Gravity.LEFT;
            holder.OneMsgLine.setLayoutParams(params2);
        }

    }

    @Override
    public int getItemCount() {

        return new_loglist.size();
    }
}
