package com.a2b2.plog;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.Chronometer;
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

public class MainActivity extends AppCompatActivity implements SensorEventListener, DataClient.OnDataChangedListener {
    private static final int REQUEST_CODE = 1;
    private static final int TRASH_COUNT_REQUEST = 1;

    private TextView  km,trashtotal;
    private ConstraintLayout background;
    private static final String CAPABILITY_1_NAME = "capability_1";
    private int total = 0;
    private TrashcountItem trashcountItem;
    private String trashtype;
    private int cnt;
    private Thread timeThread = null;
    private Boolean isRunning = true;
    private Chronometer chronometer;
    private long pauseOffset;
    private SensorManager sensorManager;
    private Sensor accelerometer;
    private float totalDistance = 0f;
    private float lastX, lastY, lastZ;
    private boolean isFirstUpdate = true;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        chronometer = findViewById(R.id.chronometer);
        chronometer.setFormat(" %s");
        background = findViewById(R.id.trashCount);
        km = findViewById(R.id.km);
        trashtotal = findViewById(R.id.trash);
        ImageView trashEdit = findViewById(R.id.trashEdit);

        // SensorManager 및 가속도계 설정
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        trashcountItem = new TrashcountItem(trashtype,cnt);
        if(isRunning){
            chronometer.setBase(SystemClock.elapsedRealtime() - pauseOffset);
            chronometer.start();
            isRunning = true;

            isFirstUpdate = true; // 초기값 설정
            totalDistance = 0f;   // 거리 초기화
            km.setText("0.00 KM");
            sensorManager.registerListener((SensorEventListener) MainActivity.this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        }



        // SharedPreferences 초기화
        SharedPreferences sharedPreferences = getSharedPreferences("trashData", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear(); // 초기화
        editor.apply();

        // MessageClient 리스너 등록
        Wearable.getMessageClient(this).addListener(new MessageClient.OnMessageReceivedListener() {
            @Override
            public void onMessageReceived(MessageEvent messageEvent) {
                if (messageEvent.getPath().equals("/path/to/stopPlogging")) {
                    String jsonString = new String(messageEvent.getData());
                    try {
                        JSONObject jsonObject = new JSONObject(jsonString);
                        Boolean result = jsonObject.getBoolean("stop");
                        isRunning = result;
                        if(!isRunning){
                            chronometer.stop();
                            pauseOffset = SystemClock.elapsedRealtime() - chronometer.getBase();
                            sensorManager.unregisterListener(MainActivity.this);
                            Intent intent = new Intent(MainActivity.this,FinishActivity.class);
                            startActivity(intent);
                            finish();
                        }
                        Log.d("isRunning", String.valueOf(isRunning));
                        Log.d("WatchApp", "Message received: " + result);

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
    @Override
    public void onSensorChanged(SensorEvent event) {
        if (isRunning && event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];

            if (!isFirstUpdate) {
                float deltaX = x - lastX;
                float deltaY = y - lastY;
                float deltaZ = z - lastZ;

                // 움직임 변화량 계산 (단순한 방식으로 거리 추정)
                float deltaDistance = (float) Math.sqrt(deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ);

                // 너무 작은 변화는 무시
                if (deltaDistance > 0.1f) {
                    totalDistance += deltaDistance;

                    // Meter를 Kilometer로 변환해서 표시 (1km = 1000m)
                    float distanceInKm = totalDistance / 1000;
                    km.setText(String.format("%.2f KM", distanceInKm));
                }
            }

            lastX = x;
            lastY = y;
            lastZ = z;
            isFirstUpdate = false;
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // 센서 정확도 변경 시 호출되지만 이 예시에서는 사용하지 않음
    }

    @Override
    protected void onPause() {
        super.onPause();
        // 앱이 일시 중지되면 센서 리스너 해제
        sensorManager.unregisterListener(this);
    }
}