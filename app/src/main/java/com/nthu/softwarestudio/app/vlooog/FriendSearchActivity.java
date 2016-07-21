package com.nthu.softwarestudio.app.vlooog;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

import com.nthu.softwarestudio.app.vlooog.data.AccountHelper;

import static android.widget.Toast.LENGTH_SHORT;

public class FriendSearchActivity extends AppCompatActivity  implements SearchView.OnQueryTextListener,SearchView.OnSuggestionListener{
    SearchView mSearch;
    RecyclerView mRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_search);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        setTitle("");
        mRecyclerView = (RecyclerView) findViewById(R.id.friendlist_request_recyclerview);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        AccountHelper getuid = new AccountHelper(this);
        FriendRequestAdaptor madaptor = new FriendRequestAdaptor(this);
        FriendRequestDataHandler list_sender = new FriendRequestDataHandler(getuid.getUserId(),mRecyclerView,madaptor);
        list_sender.connecthttpexs();
        mSearch = (SearchView) findViewById(R.id.msearchView);
        mSearch.setSubmitButtonEnabled(true);
        mSearch.setOnQueryTextListener(this);
        mSearch.setOnSuggestionListener(this);


    }

    @Override
    public boolean onQueryTextSubmit(final String query) {
        AlertDialog alertDialog = new AlertDialog.Builder(FriendSearchActivity.this).create();
        alertDialog.setTitle("Do you want to add it?");
        alertDialog.setMessage(query);
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        AccountHelper accountHelper = new AccountHelper(getApplicationContext());
                        frsend tmp =new frsend();
                        tmp.execute(Integer.toString(accountHelper.getUserId()), query, accountHelper.getNickName());
                        Toast.makeText(getBaseContext(),((Integer)which).toString(),LENGTH_SHORT).show();
                    }
                });
        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        Toast.makeText(getBaseContext(),"you don't like this "+((Integer)which).toString(),LENGTH_SHORT).show();
                    }
                });
        alertDialog.show();

        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {

        return false;
    }

    @Override
    public boolean onSuggestionClick(int position) {
        return false;
    }

    @Override
    public boolean onSuggestionSelect(int position) {
        return false;
    }
}
