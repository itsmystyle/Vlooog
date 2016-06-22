package com.nthu.softwarestudio.app.vlooog.data;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.nthu.softwarestudio.app.vlooog.FriendListDataHandler;
import com.nthu.softwarestudio.app.vlooog.FriendlistAdaptor;
import com.nthu.softwarestudio.app.vlooog.R;

public class FriendListActivity extends AppCompatActivity {
    RecyclerView mRecyclerView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_list);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        mRecyclerView = (RecyclerView) findViewById(R.id.friend_list_recycler);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        AccountHelper getuid = new AccountHelper(this);
        FriendlistAdaptor mAdapter = new FriendlistAdaptor(this);
        FriendListDataHandler list_sender = new FriendListDataHandler(this,mRecyclerView, mAdapter);
        list_sender.connecthttpexs(getuid.getUserId());
    }



}
