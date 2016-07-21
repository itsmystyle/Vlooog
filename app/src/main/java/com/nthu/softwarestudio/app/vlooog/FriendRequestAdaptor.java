package com.nthu.softwarestudio.app.vlooog;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by doda on 2016/6/23.
 */
public class FriendRequestAdaptor extends RecyclerView.Adapter<FriendRequestAdaptor.ViewHolder>{
    List<String> mNameList;
    List<Integer> mIdlist;
    List<Integer> mUIdlist;


    public FriendRequestAdaptor(Context context){
        mNameList = new ArrayList<String>();
        mIdlist = new ArrayList<Integer>();
        mUIdlist = new ArrayList<Integer>();
    }

    public void addRequest(String nametmp, Integer idtmp, Integer uidtmp){
        mNameList.add(nametmp);
        mIdlist.add(idtmp);
        mUIdlist.add(uidtmp);
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView mNameView;
        Button accept,cancel;
        public ViewHolder(View v){
            super(v);

            mNameView = (TextView) v.findViewById(R.id.friend_name);
            accept = (Button) v.findViewById(R.id.button_accept_request);
            cancel =(Button) v.findViewById(R.id.button_accept_cancel);

        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.friendlist_request_recyclerview_item, parent, false);
        // set the view's size, margins, paddings and layout parameters

        ViewHolder vh = new ViewHolder(v);

        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder,final int position) {
        Log.v("eeeeee",((Integer)position).toString()+"    "+ mNameList.get(position));
        holder.mNameView.setText(mNameList.get(position));
        holder.accept.setClickable(true);
        holder.accept.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mIdlist.remove(position);
                        mNameList.remove(position);
                        sendaccept tmp=new sendaccept();
                        tmp.execute(mUIdlist.get(position).toString(),"1");
                        //mUIdlist.get(position);
                        notifyDataSetChanged();
                    }
                }
        );
        holder.cancel.setClickable(true);
        holder.cancel.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mIdlist.remove(position);
                        mNameList.remove(position);
                        sendaccept tmp=new sendaccept();
                        tmp.execute(mUIdlist.get(position).toString(),"0");
                        notifyDataSetChanged();
                    }
                }
        );
    }

    @Override
    public int getItemCount() {
        return mNameList.size();
    }
}
