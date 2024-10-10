package com.a2b2.plog;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import com.google.android.gms.wearable.MessageClient;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Wearable;

import org.json.JSONException;
import org.json.JSONObject;

public class SplashActivity extends AppCompatActivity {
    private boolean state = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        Wearable.getMessageClient(this).addListener(new MessageClient.OnMessageReceivedListener() {
            @Override
            public void onMessageReceived(MessageEvent messageEvent) {
                if (messageEvent.getPath().equals("/path/to/startPlogging")) {
                    String jsonString = new String(messageEvent.getData());
                    try {
                        JSONObject jsonObject = new JSONObject(jsonString);
                        state= jsonObject.getBoolean("key1");
                        Log.d("WatchApp", "Message received: " + state);
                        if(state){
                            Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                            startActivity(intent);

                            finish();
                        }
                    } catch (JSONException e) {
                        Log.e("WatchApp", "Failed to parse JSON", e);
                    }
                }
            }
        });
    }
}