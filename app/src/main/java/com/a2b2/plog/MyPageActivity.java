package com.a2b2.plog;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
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
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;


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
    private Handler handler;
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
                UUID uuid = UserManager.getInstance().getUserId();
                String url = "http://15.164.152.246:8080/post/"+uuid+"/notice";
                String data = "{\"latitude\" : \"0\",\"longitude\" : \"0\",\"notificationEnabled\" : \""+isPushAlarmOn+"\"}";
                Log.d("알람설정", String.valueOf(isPushAlarmOn));
                new Thread(()->{
                    String result = httpPostBodyConnection(url, data);
                    try {
                        // JSON 문자열을 JSONObject로 변환
                        JSONObject jsonObject = new JSONObject(result);

                        // "message" 키에 해당하는 값을 추출
                        String message = jsonObject.getString("message");

                        // 값 출력
                        System.out.println("Message: " + message);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                });
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

                // 서버에 알림 허용여부 업데이트된 내용 저장(on/off - isPushAlarmOn, 기준위치 - standardLocation)
                UUID uuid = UserManager.getInstance().getUserId();
                String url = "http://15.164.152.246:8080/post/"+uuid+"/notice";
                String data = "{\"latitude\" : \""+latitude+"\",\"longitude\" : \""+longtitude+"\",\"notificationEnabled\" : \""+isPushAlarmOn+"\",\"location\" : \""+location.getPlaceName()+"\"}";
                new Thread(()->{
                    String result = httpPostBodyConnection(url, data);
                    handler = new Handler(Looper.getMainLooper());
                    if (handler != null) {
                        handler.post(() -> {
                            try {
                                // JSON 문자열을 JSONObject로 변환
                                JSONObject jsonObject = new JSONObject(result);

                                // "message" 키에 해당하는 값을 추출
                                String message = jsonObject.getString("message");

                                // 값 출력
                                System.out.println("Message: " + message);

                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            seeNetworkResult(result);
                        });
                    }

                });
                saveBtn.setVisibility(View.GONE);

                //fcm
                Intent intent = getIntent();
                if(intent != null) {//푸시알림을 선택해서 실행한것이 아닌경우 예외처리
                    String notificationData = intent.getStringExtra("test");
                    if(notificationData != null)
                        Log.d("FCM_TEST", notificationData);
                }


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
    public String httpPostBodyConnection(String UrlData, String ParamData) {
        // 이전과 동일한 네트워크 연결 코드를 그대로 사용합니다.
        // 백그라운드 스레드에서 실행되기 때문에 메인 스레드에서는 문제가 없습니다.

        String totalUrl = "";
        totalUrl = UrlData.trim().toString();

        //http 통신을 하기위한 객체 선언 실시
        URL url = null;
        HttpURLConnection conn = null;

        //http 통신 요청 후 응답 받은 데이터를 담기 위한 변수
        String responseData = "";
        BufferedReader br = null;
        StringBuffer sb = null;

        //메소드 호출 결과값을 반환하기 위한 변수
        String returnData = "";


        try {
            //파라미터로 들어온 url을 사용해 connection 실시
            url = null;
            url = new URL(totalUrl);
            conn = null;
            conn = (HttpURLConnection) url.openConnection();

            //http 요청에 필요한 타입 정의 실시
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json; utf-8"); //post body json으로 던지기 위함
            conn.setRequestProperty("Accept", "application/json");
            conn.setDoOutput(true); //OutputStream을 사용해서 post body 데이터 전송
            try (OutputStream os = conn.getOutputStream()) {
                byte request_data[] = ParamData.getBytes("utf-8");
                Log.d("TAGGG",request_data.toString());
                os.write(request_data);
                //os.close();
            } catch (Exception e) {
                Log.d("TAG3","여기다");
                e.printStackTrace();
            }

            //http 요청 실시
            conn.connect();
            System.out.println("http 요청 방식 : " + "POST BODY JSON");
            System.out.println("http 요청 타입 : " + "application/json");
            System.out.println("http 요청 주소 : " + UrlData);
            System.out.println("http 요청 데이터 : " + ParamData);
            System.out.println("");

            //http 요청 후 응답 받은 데이터를 버퍼에 쌓는다
            br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
            sb = new StringBuffer();
            while ((responseData = br.readLine()) != null) {
                sb.append(responseData); //StringBuffer에 응답받은 데이터 순차적으로 저장 실시
            }

            //메소드 호출 완료 시 반환하는 변수에 버퍼 데이터 삽입 실시
            returnData = sb.toString();
            Log.d("TAG2", returnData);
            //http 요청 응답 코드 확인 실시
            String responseCode = String.valueOf(conn.getResponseCode());
            System.out.println("http 응답 코드 : " + responseCode);
            System.out.println("http 응답 데이터 : " + returnData);

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            //http 요청 및 응답 완료 후 BufferedReader를 닫아줍니다
            try {
                if (br != null) {
                    br.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return returnData; // 네트워크 요청 결과를 반환
    }
    public void seeNetworkResult(String result) {
        // 네트워크 작업 완료 후
        Log.d(result, "result");

//        Log.d("uuid",userUUIDStr);
    }
}