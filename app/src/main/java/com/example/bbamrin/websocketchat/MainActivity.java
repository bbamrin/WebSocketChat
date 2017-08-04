package com.example.bbamrin.websocketchat;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Set;
import java.util.concurrent.ExecutorService;

import butterknife.ButterKnife;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;

public class MainActivity extends AppCompatActivity {

    private Request request;
    private SocketIO socketIO;
    private OkHttpClient client;
    private WebSocket connectedSocket;
    private ExecutorService ex;
    private boolean IS_CONNECTED = false;






    BroadcastReceiver socketDaemon = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()){
                case "sendMessage":

                    System.out.println(intent);
                    sendMessage(intent);
                    break;
                case "closeConnection":
                    closeConnection();
                    break;
            }
        }
    };




    public void onClick(View view) {

        switch (view.getId()){

            case R.id.chatRoom:
                //may provide a checking if connection was established and etc
                if (IS_CONNECTED){
                    Intent intent = new Intent(this,ChatActivity.class);
                    startActivity(intent);
                } else Toast.makeText(this,"Please, connect to the server",Toast.LENGTH_SHORT).show();

                break;
            case R.id.connect:
                startConnection();
                break;
            case R.id.closeConnection:
                closeConnection();
                break;
        }


    }






    private class SocketIO extends WebSocketListener {
        private static final int NORMAL_CLOSURE_STATUS = 1000;


        @Override
        public void onOpen(WebSocket webSocket, Response response) {
            //webSocket.send("some user is here");
            //connectedSocket = webSocket;
            IS_CONNECTED = true;
        }

        @Override
        public void onMessage(WebSocket webSocket, String text) {
            HashMap<String,String> m = new HashMap<>();
            int index = text.indexOf("@#$%");
            String name,msg;
            name = text.substring(0,index);
            msg = text.substring(index+4);
            m.put(name,msg);
            System.out.println(m + " m");
            Intent intent = new Intent().setAction("toPost");
            intent.putExtra("receivedMessage",m);
            sendBroadcast(intent);
            //output("Receiving : " + text);
        }

        @Override
        public void onMessage(WebSocket webSocket, ByteString bytes) {
            //output("Receiving bytes : " + bytes.hex());
        }

        @Override
        public void onClosing(WebSocket webSocket, int code, String reason) {
            webSocket.close(NORMAL_CLOSURE_STATUS, null);
            IS_CONNECTED = false;
            //output("Closing : " + code + " / " + reason);
        }

        @Override
        public void onFailure(WebSocket webSocket, Throwable t, Response response) {
            //output("Error : " + t.getMessage());
            IS_CONNECTED = false;
        }
    }



    private void startConnection(){
        if (!IS_CONNECTED){
            Request request = new
                    Request.Builder().url("ws://chat-bbamrin.c9users.io/").build();
            socketIO = new SocketIO();
            connectedSocket = client.newWebSocket(request,socketIO);
            ex = client.dispatcher().executorService();
        } else Toast.makeText(this,"You are already connected",Toast.LENGTH_SHORT).show();
    }


    private void closeConnection(){
        if(IS_CONNECTED){
            ex.shutdown();
            IS_CONNECTED = false;
        }  else Toast.makeText(this,"Please, connect to the server",Toast.LENGTH_SHORT).show();
    }


    private void sendMessage(Intent intent){

        System.out.println(intent + "");
        HashMap<String,String> m = (HashMap<String,String> )intent.getSerializableExtra("data");
        Set<String> s = m.keySet();
        for (String name : s){
            String msg = name + "@#$%" + m.get(name);
            connectedSocket.send(msg);
        }



    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lay);

        ButterKnife.bind(this);

        client = new OkHttpClient();

        IntentFilter filter = new IntentFilter();
        filter.addAction("sendMessage");
        filter.addAction("closeConnection");
        registerReceiver(socketDaemon,filter);


    }


    @Override
    protected void onDestroy(){
        super.onDestroy();
        unregisterReceiver(socketDaemon);
    }



}
