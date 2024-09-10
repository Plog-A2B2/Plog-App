package com.a2b2.plog;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;


public class MyPageActivity extends AppCompatActivity {

    private ImageView rank, community, mission, home;
    private int n=0;
    private android.widget.Button  myCommunityBtn;

    private SettingLocationDialog settingLocationDialog;
    private TextView standardLocationTextView;
    private SwitchCompat pushAlarmSwitch;
    boolean isPushAlarmOn = false; // 서버에서 알림 허용 여부 받아와서 저장

    private ImageView more;
    private Place location, standardLocation;
    private Button saveBtn;

    Double latitude, longtitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_page);

        settingLocationDialog = new SettingLocationDialog(this);

        standardLocationTextView = findViewById(R.id.standardLocationTextView);
        pushAlarmSwitch =findViewById(R.id.pushAlarmSwitch);
        saveBtn = findViewById(R.id.saveBtn);
        more = findViewById(R.id.more);

        more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MyPageActivity.this, MyPloggingActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                finish();
            }
        });

        pushAlarmSwitch.setChecked(false);
        saveBtn.setVisibility(View.GONE);

        if (isPushAlarmOn == true) {
            standardLocationTextView.setVisibility(View.VISIBLE);
            settingLocationDialog.show();
        } else {
            standardLocationTextView.setVisibility(View.GONE);
        }

        // 스위치 상태 변경 리스너 설정
        pushAlarmSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            isPushAlarmOn = isChecked;
            if (isPushAlarmOn == true) {
                standardLocationTextView.setVisibility(View.VISIBLE);
                settingLocationDialog.show();
            } else {
                standardLocationTextView.setVisibility(View.GONE);
            }
            saveBtn.setVisibility(View.VISIBLE);
        });

        settingLocationDialog.setOnItemClickListener(new SettingLocationDialog.OnItemClickListener() {
            @Override
            public void onItemClick(Place document) {
                if (document != null) {
                    location = document;

                    new Thread(() -> {
                        try {
                            // 네트워크 작업 수행
                            getGeoDataByAddress("서울특별시 송파구 송파대로 570");

                            // UI 업데이트를 메인 스레드에서 실행
                            runOnUiThread(() -> {
                                // 결과 처리

                                standardLocation = new Place(location.getPlaceName(), location.getAddress(), latitude, longtitude);
                                standardLocationTextView.setText("기준 위치: " + document.getPlaceName());
                                Log.d("geoDataByAddress", String.valueOf(latitude) + ", " + longtitude);
                            });
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }).start();




                }
            }
        });

        standardLocationTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isPushAlarmOn == true) {
                    standardLocationTextView.setVisibility(View.VISIBLE);
                    settingLocationDialog.show();
                } else {
                    standardLocationTextView.setVisibility(View.GONE);
                }
                saveBtn.setVisibility(View.VISIBLE);
            }
        });

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("setting", "saved : " + isPushAlarmOn);
                // 서버에 알림 허용여부 업데이트된 내용 저장(on/off - isPushAlarmOn, 기준위치 - standardLocation)
                saveBtn.setVisibility(View.GONE);

                //fcm
                Intent intent = getIntent();
                if(intent != null) {//푸시알림을 선택해서 실행한것이 아닌경우 예외처리
                    String notificationData = intent.getStringExtra("test");
                    if(notificationData != null)
                        Log.d("FCM_TEST", notificationData);
                }

                FirebaseMessaging.getInstance().getToken()
                        .addOnCompleteListener(new OnCompleteListener<String>() {
                            @Override
                            public void onComplete(@NonNull Task<String> task) {
                                if (!task.isSuccessful()) {
                                    System.out.println("Fetching FCM registration token failed");
                                    return;
                                }

                                // Get new FCM registration token
                                String token = task.getResult();
                                Log.d("fcm in mypage", token);
                                //통신할 때 알람설정여부,위도,경도, 토큰값 보내기 (post)

                                // Log and toast
//                        System.out.println(token);
//                        Toast.makeText(LoginActivity.this, "Your device registration token is" + token
//                                , Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });

        rank = findViewById(R.id.rank);
        community = findViewById(R.id.community);
        home = findViewById(R.id.home);
        mission = findViewById(R.id.mission);

        rank.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MyPageActivity.this, RankActivity.class);
                startActivity(intent);
                overridePendingTransition(0, 0);
                finish();
            }
        });

        community.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MyPageActivity.this, CommunityActivity.class);
                startActivity(intent);
                overridePendingTransition(0, 0);
                finish();
            }
        });
        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MyPageActivity.this, HomeActivity.class);
                startActivity(intent);
                overridePendingTransition(0, 0);
                finish();
            }
        });
        mission.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MyPageActivity.this, MissionActivity.class);
                startActivity(intent);
                overridePendingTransition(0, 0);
                finish();
            }
        });
        myCommunityBtn = findViewById(R.id.myCommunityTaskBtn);
        myCommunityBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MyPageActivity.this, MyCommunityActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void getGeoDataByAddress(String completeAddress) {
        try {
            String API_KEY = "AIzaSyC6FB54gjhzW2wfkKqD8vo5OybyxW55k8M";
            String surl = "https://maps.googleapis.com/maps/api/geocode/json?address=" + URLEncoder.encode(completeAddress, "UTF-8") + "&key=" + API_KEY;
            URL url = new URL(surl);
            InputStream is = url.openConnection().getInputStream();

            BufferedReader streamReader = new BufferedReader(new InputStreamReader(is, "UTF-8"));

            StringBuilder responseStrBuilder = new StringBuilder();
            String inputStr;
            while ((inputStr = streamReader.readLine()) != null) {
                responseStrBuilder.append(inputStr);
            }

            JSONObject jo = new JSONObject(responseStrBuilder.toString());
            JSONArray results = jo.getJSONArray("results");
            String region = null;
            String province = null;
            String zip = null;
            if (results.length() > 0) {
                JSONObject jsonObject;
                jsonObject = results.getJSONObject(0);
                Double lat = jsonObject.getJSONObject("geometry").getJSONObject("location").getDouble("lat");
                Double lng = jsonObject.getJSONObject("geometry").getJSONObject("location").getDouble("lng");
//                ret.put(lat);
//                ret.put(lng);
                latitude = lat;
                longtitude = lng;

                System.out.println("LAT:\t\t" + lat);
                System.out.println("LNG:\t\t" + lng);
                JSONArray ja = jsonObject.getJSONArray("address_components");

            }
        } catch(Exception e){
            e.printStackTrace();
        }
    }
}