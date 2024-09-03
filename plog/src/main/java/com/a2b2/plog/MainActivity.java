package com.a2b2.plog;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.wearable.DataClient;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataItem;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.Wearable;

import org.json.JSONObject;

public class MainActivity extends AppCompatActivity implements DataClient.OnDataChangedListener {
    private static final int REQUEST_CODE = 1;
    private TextView time, km,trashtotal;
    private ConstraintLayout background;
    private static final String CAPABILITY_1_NAME = "capability_1";
    private int total = 0;
    private TrashcountItem trashcountItem;
    private String trashtype;
    private int cnt;
    private Thread timeThread = null;
    private Boolean isRunning = true;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        background = findViewById(R.id.trashCount);

        time = findViewById(R.id.time);
        km = findViewById(R.id.km);
        trashtotal = findViewById(R.id.trash);
        ImageView trashEdit = findViewById(R.id.trashEdit);

        trashtotal.setText(total + "/0");

        trashcountItem = new TrashcountItem(trashtype,cnt);
        ImageView running = findViewById(R.id.running);
        running.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SplashActivity.class);
                startActivity(intent);
                finish();
            }
        });
        timeThread = new Thread(new timeThread());
        timeThread.start();


        Wearable.getDataClient(this).addListener(this);

        Wearable.getDataClient(this).addListener(new DataClient.OnDataChangedListener() {
            @Override
            public void onDataChanged(DataEventBuffer dataEvents) {
                for (DataEvent event : dataEvents) {
                    if (event.getType() == DataEvent.TYPE_CHANGED) {
                        DataItem item = event.getDataItem();
                        if (item.getUri().getPath().compareTo("/path/to/data") == 0) {
                            DataMap dataMap = DataMapItem.fromDataItem(item).getDataMap();
                            String value = dataMap.getString("key");
                            Log.d("WatchApp", "Data item received: " + value);
                            time.setText(value);
                        }
                    }
                }
            }
        });

        findViewById(R.id.trashEdit).setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, TrashCountActivity.class);
            startActivityForResult(intent, REQUEST_CODE);
        });
        findViewById(R.id.trashCount).setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, TrashCountActivity.class);
            startActivityForResult(intent, REQUEST_CODE);
        });

//        trashEdit.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(getApplicationContext(), TrashCountActivity.class);
//                startActivity(intent);
//            }
//        });
//        background.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(getApplicationContext(), TrashCountActivity.class);
//                startActivity(intent);
//            }
//        });

    }
    @Override
    public void onDataChanged(DataEventBuffer dataEvents) {
        for (DataEvent event : dataEvents) {
            if (event.getType() == DataEvent.TYPE_CHANGED) {
                DataItem item = event.getDataItem();
                if (item.getUri().getPath().equals("/trash_data")) {
                    DataMap dataMap = DataMapItem.fromDataItem(item).getDataMap();
                    String jsonString = dataMap.getString("json_data");

                    try {
                        JSONObject jsonObject = new JSONObject(jsonString);
                        int count = jsonObject.getInt("count");

                        // TrashCountItem의 cnt 변수에 수신한 count 값을 설정
                        trashcountItem.getCnt();

                        Log.d("WatchApp", "Received count: " + count);
                    } catch (Exception e) {
                        Log.e("WatchApp", "Failed to parse JSON data", e);
                    }
                }
            }
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
            if (data != null && data.hasExtra("total")) {
                total = data.getIntExtra("total", 0);
                trashtotal.setText(total + "/0");
            }
        }
    }
    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            int mSec = msg.arg1 % 100;
            int sec = (msg.arg1 / 100) % 60;
            int min = (msg.arg1 / 100) / 60;
            int hour = (msg.arg1 / 100) / 360;
            //1000이 1초 1000*60 은 1분 1000*60*10은 10분 1000*60*60은 한시간

//            @SuppressLint("DefaultLocale") String result = String.format("%02d:%02d:%02d:%02d", hour, min, sec, mSec);
//            if (result.equals("00:01:15:00")) {
//                Toast.makeText(MainActivity.this, "1분 15초가 지났습니다.", Toast.LENGTH_SHORT).show();
//            }
//            time.setText(result);
        }
    };
    public class timeThread implements Runnable {
        @Override
        public void run() {
            int i = 0;

            while (true) {
                while (isRunning) { //일시정지를 누르면 멈춤
                    Message msg = new Message();
                    msg.arg1 = i++;
                    handler.sendMessage(msg);

                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        runOnUiThread(new Runnable(){
                            @Override
                            public void run() {
                                time.setText("");
                                time.setText("00:00:00:00");
                            }
                        });
                        return; // 인터럽트 받을 경우 return
                    }
                }
            }
        }
    }
}