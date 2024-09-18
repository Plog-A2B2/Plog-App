package com.a2b2.plog;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.google.android.gms.location.LocationRequest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.wearable.DataClient;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;
import android.Manifest;
import android.os.Bundle;
import android.os.Looper;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.wearable.DataClient;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataClient.OnDataChangedListener;
import com.google.android.gms.wearable.Wearable;
import com.google.android.gms.tasks.Task;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.wearable.DataClient;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataItem;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.MessageClient;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;

import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;


public class MainActivity extends AppCompatActivity implements DataClient.OnDataChangedListener{
    private static final int REQUEST_CODE = 1;
    private static final int TRASH_COUNT_REQUEST = 1;
    private TextView  km,trashtotal;
    private ConstraintLayout background;
    private static final String CAPABILITY_1_NAME = "capability_1";
    private int total = 0;
    private TrashcountItem trashcountItem;
    private String trashtype;
    private int cnt;
    private Boolean isRunning = true;
    private Chronometer chronometer;
    private long pauseOffset;
    private static final int REQUEST_LOCATION_PERMISSION = 1;
    private FusedLocationProviderClient fusedLocationClient;
    private LocationRequest locationRequest;
    private LocationCallback locationCallback;
    private double lastLatitude = 0.0;
    private double lastLongitude = 0.0;
    private double totalDistance = 0.0;
    private ImageView running;
    private DataClient dataClient;
    // 권한 요청 메소드

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        running = findViewById(R.id.running);
        dataClient = Wearable.getDataClient(this);
        running.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendDataToPhone("tlqkf");
            }
        });

        chronometer = findViewById(R.id.chronometer);
        chronometer.setFormat(" %s");
        background = findViewById(R.id.trashCount);
        km = findViewById(R.id.km);
        trashtotal = findViewById(R.id.trash);
        ImageView trashEdit = findViewById(R.id.trashEdit);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        locationRequest = LocationRequest.create();
        locationRequest.setInterval(10000); // 10 seconds
        locationRequest.setFastestInterval(5000); // 5 seconds
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                for (android.location.Location location : locationResult.getLocations()) {
                    double latitude = location.getLatitude();
                    double longitude = location.getLongitude();
                    if (lastLatitude != 0.0 && lastLongitude != 0.0) {
                        totalDistance += calculateDistance(lastLatitude, lastLongitude, latitude, longitude);
                        km.setText(String.format("%.2f KM", totalDistance / 1000.0));
                    }
                    lastLatitude = latitude;
                    lastLongitude = longitude;
                }
            }
        };

        requestLocationPermission();

        trashcountItem = new TrashcountItem(trashtype,cnt);

        if(isRunning){
            chronometer.setBase(SystemClock.elapsedRealtime() - pauseOffset);
            chronometer.start();
            isRunning = true;

           // km.setText("0.00 KM");
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

                            fusedLocationClient.removeLocationUpdates(locationCallback);
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

    }
    private void sendDataToPhone(String jsonString) {
        Log.d("sendDataToPhone","클릭됨");
        // JSON 데이터를 "/json_data" 경로로 전송
        PutDataMapRequest dataMap = PutDataMapRequest.create("/json_data");
        dataMap.getDataMap().putString("json_key", jsonString);
        PutDataRequest request = dataMap.asPutDataRequest();

        // DataClient 사용하여 데이터 전송
        DataClient dataClient = Wearable.getDataClient(this);
        dataClient.putDataItem(request);
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

        startLocationUpdates();
    }

    //km
    private void requestLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_LOCATION_PERMISSION);
        } else {
            startLocationUpdates();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_LOCATION_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // 권한이 허용됨
                startLocationUpdates();
            } else {
                // 권한이 거부됨
                // 사용자에게 권한이 필요하다는 메시지를 표시할 수 있습니다.
                Toast.makeText(MainActivity.this,"위치 권한을 허용해주세요",Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void startLocationUpdates() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
        }
    }
    private double calculateDistance(double startLat, double startLng, double endLat, double endLng) {
        float[] results = new float[1];
        android.location.Location.distanceBetween(startLat, startLng, endLat, endLng, results);
        return results[0];
    }
    @Override
    protected void onPause() {
        super.onPause();
    }
//    private void sendDataToPhone(String jsonData) {
//        PutDataRequest putDataRequest = PutDataRequest.create("/data_path");
//        // JSON 문자열을 바이트 배열로 변환합니다.
//        byte[] jsonDataBytes = jsonData.getBytes(StandardCharsets.UTF_8);
//        // 바이트 배열을 PutDataRequest에 추가합니다.
//        putDataRequest.putByteArray("json_data", jsonDataBytes);
//
//        dataClient.putDataItem(putDataRequest)
//                .addOnSuccessListener(dataItem -> {
//                    // 데이터 전송 성공
//                })
//                .addOnFailureListener(exception -> {
//                    // 데이터 전송 실패
//                });
//    }

}