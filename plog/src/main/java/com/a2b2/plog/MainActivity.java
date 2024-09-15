package com.a2b2.plog;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
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
import com.google.android.gms.wearable.MessageClient;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Wearable;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class MainActivity extends AppCompatActivity implements DataClient.OnDataChangedListener {
    private static final int REQUEST_CODE = 1;
    private static final int TRASH_COUNT_REQUEST = 1;

    private TextView time, km,trashtotal;
    private ConstraintLayout background;
    private static final String CAPABILITY_1_NAME = "capability_1";
    private int total = 0;
    private TrashcountItem trashcountItem;
    private String trashtype;
    private int cnt;
    private Thread timeThread = null;
    private Boolean isRunning = true;
    private Boolean isPloggingChecked = true;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        background = findViewById(R.id.trashCount);

        time = findViewById(R.id.time);
        km = findViewById(R.id.km);
        trashtotal = findViewById(R.id.trash);
        ImageView trashEdit = findViewById(R.id.trashEdit);

        trashcountItem = new TrashcountItem(trashtype,cnt);

//        if(adapter.getTotalCount() !=0){
//            total = adapter.getTotalCount();
//            Log.d("받아라 좀", String.valueOf(total));
//
//            trashtotal.setText(String.valueOf(total));
//        }



        // SharedPreferences 초기화
        SharedPreferences sharedPreferences = getSharedPreferences("trashData", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear(); // 초기화
        editor.apply();

        // MessageClient 리스너 등록
        Wearable.getMessageClient(this).addListener(new MessageClient.OnMessageReceivedListener() {
            @Override
            public void onMessageReceived(MessageEvent messageEvent) {
                if (messageEvent.getPath().equals("/path/to/data")) {
                    String jsonString = new String(messageEvent.getData());
                    try {
                        JSONObject jsonObject = new JSONObject(jsonString);
                        String value = jsonObject.getString("key1");
                        isPloggingChecked = jsonObject.getBoolean("key2");
                        Log.d("isPloggingChecked", String.valueOf(isPloggingChecked));
                        Log.d("WatchApp", "Message received: " + value);
                        km.setText(value);

                    } catch (JSONException e) {
                        Log.e("WatchApp", "Failed to parse JSON", e);
                    }
                }
            }
        });

        findViewById(R.id.trashEdit).setOnClickListener(v -> {
            Intent intent1 = new Intent(MainActivity.this, TrashCountActivity.class);
            startActivityForResult(intent1, REQUEST_CODE);
        });
        findViewById(R.id.trashCount).setOnClickListener(v -> {
            Intent intent2 = new Intent(MainActivity.this, TrashCountActivity.class);
            startActivityForResult(intent2, REQUEST_CODE);
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
    //액티비티 전환 시 값 받아오는 거
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == TRASH_COUNT_REQUEST && resultCode == RESULT_OK) {
            // TrashCountActivity로부터 값을 받아옴
            total = data.getIntExtra("totalCount", 0);
            Log.d("total", String.valueOf(total));
            trashtotal.setText(String.valueOf(total)); // 값 설정
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
        // SharedPreferences에서 total 값을 가져옴
        SharedPreferences sharedPreferences = getSharedPreferences("trashData", MODE_PRIVATE);
        total = sharedPreferences.getInt("total", 0);
        trashtotal.setText(String.valueOf(total)); // TextView 업데이트
    }
}