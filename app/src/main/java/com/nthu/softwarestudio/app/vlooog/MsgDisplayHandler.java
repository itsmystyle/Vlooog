package com.nthu.softwarestudio.app.vlooog;

import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by doda on 2016/6/22.
 */
public class MsgDisplayHandler {
    String chattmpjson;
    MsgListAdaptor mReAdaptor;
    RecyclerView mTotalRecyclerView;
    Integer mUserId;
    JSONObject data;
    public MsgDisplayHandler(RecyclerView mtotmp, Integer idtmp){
        mReAdaptor = new MsgListAdaptor(idtmp);
        mTotalRecyclerView = mtotmp;
        mUserId = idtmp;
    }

    public void updatevoew(String Chattmp){
        chattmpjson = Chattmp;
        try{
            data = new JSONObject(Chattmp);
        }catch (JSONException e){e.printStackTrace();}
        SenderShell tester=new SenderShell();
        tester.execute();
    }

    public class SenderShell extends AsyncTask<String, Void, String> {
        public SenderShell(){
        }

        @Override
        protected String doInBackground(String... params) {
            try{
                mReAdaptor.addMsg(data.getInt("from"),data.getString("msg"));
            }catch (JSONException e){e.printStackTrace();}




            return "success";
        }

    }
}
