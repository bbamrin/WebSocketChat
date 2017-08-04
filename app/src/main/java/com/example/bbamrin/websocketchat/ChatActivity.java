package com.example.bbamrin.websocketchat;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.OkHttpClient;

/**
 * Created by bbamrin on 02.08.17.
 */

public class ChatActivity extends AppCompatActivity {

    private SimpleAdapter adapter;
    private ArrayList<String> msgTextList;
    @BindView((R.id.textOfMsg))EditText textOfMsg;
    @BindView(R.id.msgList) ListView msgList;
    final String NAME_TEXT = "name";
    final String MESSAGE_TEXT = "msg";
    private String[] from = {NAME_TEXT,MESSAGE_TEXT};
    private ArrayList<Map<String,String>> data;
    private int[] to = {R.id.name,R.id.message};

    private BroadcastReceiver onMsg = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()){
                case "toPost":
                    HashMap<String,String> map = (HashMap<String,String>) intent.getSerializableExtra("receivedMessage");
                    for (String name : map.keySet()){
                        putMessage(name,map.get(name));
                    }

                    break;

            }
        }
    };

    //private Map<String,String> usersMessages;

   // @BindView(R.id.sendMessage)Button sendMessage;
   // @BindView(R.id.closeConnectionSecond)Button closeConnection;

    @OnClick({R.id.sendMessage,R.id.closeConnectionSecond})
    public void onViewClick(View view){
        switch (view.getId()){

            case R.id.sendMessage:
                String name = android.os.Build.MODEL;
                String msg = textOfMsg.getText().toString();
                sendMessage(name,msg);
                break;
            case R.id.closeConnectionSecond:
                Intent intent = new Intent().setAction("closeConnection");
                sendBroadcast(intent);
                break;

        }
    }



    public void putMessage(String name,String msg){

        Map<String,String> m = new HashMap<String,String>();



        m.put(NAME_TEXT,name);
        m.put(MESSAGE_TEXT,msg);
        data.add(m);
        adapter.notifyDataSetChanged();
    }


    public void sendMessage(String name, String msg){
        HashMap<String,String> dataToSend = new HashMap<>();
        dataToSend.put(name,msg);

        Intent intent = new Intent().setAction("sendMessage");
        intent.putExtra("data",dataToSend);
        sendBroadcast(intent);

        //putMessage(name,msg);

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat_room);
        data = new ArrayList<Map<String, String>>();
        //client = new OkHttpClient();
        ButterKnife.bind(this);
        adapter = new SimpleAdapter(this,data,R.layout.item,from,to);
        msgList.setAdapter(adapter);
        IntentFilter filter = new IntentFilter("toPost");
        registerReceiver(onMsg,filter);

    }


    @Override
    protected void onDestroy(){
        super.onDestroy();
        unregisterReceiver(onMsg);
    }



}
