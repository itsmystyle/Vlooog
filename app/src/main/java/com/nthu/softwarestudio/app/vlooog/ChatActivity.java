package com.nthu.softwarestudio.app.vlooog;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.nthu.softwarestudio.app.vlooog.data.AccountHelper;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

public class ChatActivity extends AppCompatActivity {
    public String[] ThisTimeLog;
    private WebSocketClient mWebSocketClient;
    Integer yourfriendId;
    Integer myid;
    RecyclerView mRecyclerView;
    MsgListAdaptor mAdaptor;
    LogHelper logdatabase;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("");
        logdatabase = new LogHelper(this);

        Intent new_msg=getIntent();
        setTitle(new_msg.getStringExtra("yourFriendName"));
        yourfriendId = new_msg.getIntExtra("yourFriendID",0);

        mRecyclerView = (RecyclerView) findViewById(R.id.msg_recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        AccountHelper getuid = new AccountHelper(this);
        myid = getuid.getUserId();
        List<MsgData>tmp=logdatabase.getmsg(myid,yourfriendId);
        mAdaptor = new MsgListAdaptor(getuid.getUserId());
        int j=0;
        for(j=0;j<tmp.size();j++){
            mAdaptor.addMsg(tmp.get(j).from,tmp.get(j).msg);
        }
        mRecyclerView.setAdapter(mAdaptor);
        if(mAdaptor.getItemCount()>11){
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getParent());
            linearLayoutManager.setStackFromEnd(true);
            mRecyclerView.setLayoutManager(linearLayoutManager);
        }
        connectWebSocket();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mWebSocketClient.close();
        Log.v("Destroy test","save log");
    }

    private void connectWebSocket() {
        URI uri;
        try {
            uri = new URI("ws://s103062325.web.2y.idv.tw:2000");
        } catch (URISyntaxException e) {
            e.printStackTrace();
            return;
        }

        mWebSocketClient = new WebSocketClient(uri) {
            @Override
            public void onOpen(ServerHandshake serverHandshake) {
                Log.i("Websocket", "Opened");
                String tmp="{\"action\":\"setname\",\"username\":\""+myid.toString()+"\",\"friendid\":\""+yourfriendId.toString()+"\"}";
                mWebSocketClient.send(tmp);
            }

            @Override
            public void onMessage(String s) {
                final String message = s;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try{
                            Log.v("gget data", message);
                            JSONObject jtmp = new JSONObject(message);
                            mAdaptor.addMsg(jtmp.getInt("username"),jtmp.getString("msg"));
                            mRecyclerView.setAdapter(mAdaptor);
                            //mRecyclerView.scrollTo(mRecyclerView.getScrollX(), 2000);
                            if(mAdaptor.getItemCount()>11){
                                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getParent());
                                linearLayoutManager.setStackFromEnd(true);
                                mRecyclerView.setLayoutManager(linearLayoutManager);
                            }
                            logdatabase.insertLog(yourfriendId,myid,jtmp.getString("msg"));
                        }catch(JSONException e){e.printStackTrace();}
                    }
                });

            }

            @Override
            public void onClose(int i, String s, boolean b) {
                Log.i("Websocket", "Closed " + s);
            }

            @Override
            public void onError(Exception e) {
                Log.i("Websocket", "Error " + e.getMessage());
            }
        };
        mWebSocketClient.connect();
    }

    public void sendMessage(View view) {
        EditText editText = (EditText)findViewById(R.id.send_msg);
        //mWebSocketClient.send(editText.getText().toString());
        if(!editText.getText().toString().equals("")){
            String tmp="{\"action\":\"message\",\"msg\":\""+editText.getText().toString()+"\",\"username\":\""+myid.toString()+"\",\"to\":\""+yourfriendId.toString()+"\",\"from\":\""+myid.toString()+"\"}";
            mWebSocketClient.send(tmp);
            try{
                JSONObject jtmp = new JSONObject(tmp);
                mAdaptor.addMsg(jtmp.getInt("from"),jtmp.getString("msg"));
                mRecyclerView.setAdapter(mAdaptor);
                if(mAdaptor.getItemCount()>11){
                    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getParent());
                    linearLayoutManager.setStackFromEnd(true);
                    mRecyclerView.setLayoutManager(linearLayoutManager);
                }
                logdatabase.insertLog(myid,yourfriendId,editText.getText().toString());
            }catch(JSONException e){e.printStackTrace();}
            editText.setText("");
        }
    }


}
