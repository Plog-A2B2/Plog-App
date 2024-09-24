package com.a2b2.plog;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;
import com.kakao.sdk.user.model.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
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
    private SettingProfileDialog settingProfileDialog;
    private TextView standardLocationTextView;
    private SwitchCompat pushAlarmSwitch;
    boolean isPushAlarmOn = false; // 서버에서 알림 허용 여부 받아와서 저장
    boolean isDialogCheck = true;

    private ImageView more;
    private Place location, standardLocation;
    private Button saveBtn;
    private Handler handler;
    Double latitude, longtitude;
    private UUID uuid = UserManager.getInstance().getUserId();
    private String url;
    private ImageView coinInfoBtn, profileImg;

    private ImageView membership;
    String url2, result2;
    TextView coinNumTextView, nicknameTextView, distanceTextView, timeTextView, trashNumTextView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_page);

        UserManager userManager = UserManager.getInstance();
        settingLocationDialog = new SettingLocationDialog(this);
        settingProfileDialog = new SettingProfileDialog(this, userManager);
        handler = new Handler(Looper.getMainLooper());

        standardLocationTextView = findViewById(R.id.standardLocationTextView);
        pushAlarmSwitch =findViewById(R.id.pushAlarmSwitch);
        saveBtn = findViewById(R.id.saveBtn);
        more = findViewById(R.id.more);
        coinInfoBtn = findViewById(R.id.coinInfoBtn);
        profileImg = findViewById(R.id.profileImg);
        membership = findViewById(R.id.membership);
        coinNumTextView = findViewById(R.id.coinNumTextView);
        nicknameTextView = findViewById(R.id.nicknameTextView);
        distanceTextView = findViewById(R.id.distanceTextView);
        timeTextView = findViewById(R.id.timeTextView);
        trashNumTextView = findViewById(R.id.trashNumTextView);

        membership.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showMembershipDialog();
            }
        });

        coinInfoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CoinInfoDialog dialog = new CoinInfoDialog(MyPageActivity.this);
                dialog.show();
            }
        });

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
        if(!pushAlarmSwitch.isChecked()) {
            standardLocationTextView.setVisibility(View.GONE);
        }


        Log.d("유유아이디 확인", String.valueOf(uuid));
        url = "http://15.164.152.246:8080/profile/"+uuid+"/MyPage";
        Log.d("주소 확인",url);
        new Thread(()->{
            String result = httpGetConnection(url, "");
            Log.d("result", result);

            if (handler != null) {
                handler.post(() -> {
                    try {
                        // JSON 문자열을 JSONObject로 변환
                        JSONObject jsonObject = new JSONObject(result);

                        // "message" 키에 해당하는 값을 추출
                        String message = jsonObject.getString("message");
                        if (jsonObject.has("data")) {
                            // "data" 객체를 추출
                            JSONObject dataObject = jsonObject.getJSONObject("data");

                            // "location"과 "notificationEnabled" 값을 추출
                            String location1 = dataObject.getString("location");
                            isPushAlarmOn = dataObject.getBoolean("notificationEnabled");
                            //isPushAlarmOn = jsonObject.getBoolean("notificationEnabled");
                            //isPushAlarmOn = Boolean.parseBoolean(isPushAlarmOntoString);
                            Log.d("message", message);
                            Log.d("알람설정여부", String.valueOf(isPushAlarmOn));

                            if (isPushAlarmOn == true) {
                                standardLocationTextView.setVisibility(View.VISIBLE);
                                isDialogCheck = false;
                                pushAlarmSwitch.setChecked(true);

//                                settingLocationDialog.show();
                                standardLocationTextView.setText("기준 위치: " + location1);
                            } else {
                                standardLocationTextView.setVisibility(View.GONE);
                                pushAlarmSwitch.setChecked(false);
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
            }
        }).start();

        url2 = "http://15.164.152.246:8080/profile/"+uuid+"/MyPageInformation";
        Log.d("주소 확인",url2);
        new Thread(()->{
            String result2 = httpGetConnection2(url2, "");

            if (handler != null) {
                handler.post(() -> {
                    try {
                        // JSON 객체 생성
                        JSONObject jsonObject = new JSONObject(result2);

                        // "message" 값 가져오기
                        String message = jsonObject.getString("message");
                        System.out.println("Message: " + message);

                        // "data" 객체 가져오기
                        JSONObject dataObject = jsonObject.getJSONObject("data");

                        // 각 데이터 필드 파싱
                        String userNickname = dataObject.getString("userNickname");
                        int totalCoin = dataObject.getInt("totalCoin");
                        int badge = dataObject.getInt("badge");
                        double totalDistance = dataObject.getDouble("totalDistance");
                        int totalTime = dataObject.getInt("totalTime");
                        int totalTrash = dataObject.getInt("totalTrash");

                        // 결과 출력
                        System.out.println("User Nickname: " + userNickname);
                        System.out.println("Total Coin: " + totalCoin);
                        System.out.println("Badge: " + badge);
                        System.out.println("Total Distance: " + totalDistance);
                        System.out.println("Total Time: " + totalTime);
                        System.out.println("Total Trash: " + totalTrash);

                        coinNumTextView.setText(totalCoin + "코인");
                        UserManager.getInstance().setBadgeId(badge);
                        profileImg.setImageResource(BadgeManager.getDrawableForBadgeId(userManager.getBadgeId()));
                        UserManager.getInstance().setUserNickname(userNickname);
                        nicknameTextView.setText(userNickname + " 님");

                        distanceTextView.setText(totalDistance + " KM");
                        int hours = totalTime / 3600;        // 시간 계산 (1시간 = 3600초)
                        timeTextView.setText(hours + " H");
                        trashNumTextView.setText(totalTrash + " 개");

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    seeNetworkResult(result2);
                });
            }
        }).start();

//        profileImg.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                settingProfileDialog.show();
//            }
//        });

        profileImg.setOnClickListener(view -> {
            settingProfileDialog.setOnItemClickListener(dialogInterface -> {
                updateProfileImage(userManager.getBadgeId());
            });
            settingProfileDialog.show();
        });

//        if (isPushAlarmOn == true && isDialogCheck == true) {
//            standardLocationTextView.setVisibility(View.VISIBLE);
//            settingLocationDialog.show();
//        } else {
//            standardLocationTextView.setVisibility(View.GONE);
//        }

        Log.d("다이얼로그 상태 확인", String.valueOf(isDialogCheck));
        // 스위치 상태 변경 리스너 설정
        pushAlarmSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            isPushAlarmOn = isChecked;

            if (isPushAlarmOn == true && isDialogCheck == true) {
                standardLocationTextView.setVisibility(View.VISIBLE);
                settingLocationDialog.show();
                saveBtn.setVisibility(View.VISIBLE);

            } else if (isPushAlarmOn == true && isDialogCheck == false) {
                standardLocationTextView.setVisibility(View.VISIBLE);
                isDialogCheck = true;
            } else {
                standardLocationTextView.setVisibility(View.GONE);
                saveBtn.setVisibility(View.GONE);
                url = "http://15.164.152.246:8080/api/fcm/"+uuid+"/notice";
                String data = "{\"latitude\" : \"0\",\"longitude\" : \"0\",\"notificationEnabled\" : \""+isPushAlarmOn+"\",\"location\" : \"장소없음\"}";
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

                }).start();

            }
        });

        settingLocationDialog.setOnItemClickListener(new SettingLocationDialog.OnItemClickListener() {
            @Override
            public void onItemClick(Place document) {
                if (document != null) {
                    location = document;

                    new Thread(() -> {
                        try {
                            // 네트워크 작업 수행

                            // UI 업데이트를 메인 스레드에서 실행
                            runOnUiThread(() -> {
                                // 결과 처리

                                standardLocation = new Place(location.getPlaceName(), location.getAddress(), 0.0, 0.0);
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

                // 서버에 알림 허용여부 업데이트된 내용 저장(on/off - isPushAlarmOn, 기준위치 - standardLocation)
                UUID uuid = UserManager.getInstance().getUserId();
                Log.d("유유아이디 확인", String.valueOf(uuid));
                String url = "http://15.164.152.246:8080/api/fcm/"+uuid+"/notice";
                Log.d("주소 확인",url);
                new Thread(()->{
                    if(standardLocation.getAddress() != null) {
                        Log.d("if문 안에들어왓다", standardLocation.getAddress());

                        getGeoDataByAddress(standardLocation.getAddress());
                    }

                    String data = "{\"latitude\" : \""+standardLocation.getLatitude()+"\",\"longitude\" : \""+standardLocation.getLongitude()+"\",\"notificationEnabled\" : \""+isPushAlarmOn+"\",\"location\" : \""+location.getPlaceName()+"\"}";
                    Log.d("데이터 확인", data);
                    String result = httpPostBodyConnection(url, data);

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

                }).start();
                saveBtn.setVisibility(View.GONE);
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
                overridePendingTransition(0, 0);

                finish();
            }
        });
    }

    public void showMembershipDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        if (UserManager.getInstance().getIsMembership()) {
            builder.setTitle("멤버십 해지")
                    .setMessage("멤버십을 해지하시겠습니까?")
                    .setPositiveButton("예", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // "예" 버튼 클릭 시의 행동
                            //멤버십 해지
                            UUID uuid = UserManager.getInstance().getUserId();

                            String url = "http://15.164.152.246:8080/profile/" + uuid + "/membershipcancel";
                            new Thread(() -> {
                                String result = httpPostBodyConnection(url, "");
                                // 처리 결과 확인
                                handler = new Handler(Looper.getMainLooper());
                                if (handler != null) {
                                    handler.post(() -> {
                                        Log.d("result", result);
                                        Toast.makeText(MyPageActivity.this, "멤버십이 해지되었습니다.", Toast.LENGTH_LONG).show();
                                        UserManager.getInstance().setMembership(false);
                                    });

                                }
                            }).start();
                        }
                    })
                    .setNegativeButton("아니요", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // "아니요" 버튼 클릭 시의 행동
                            dialog.dismiss();
                        }
                    })
                    .setCancelable(true); // 다이얼로그 외부 클릭으로 취소 가능
            AlertDialog dialog = builder.create();
            dialog.show();
        } else {
            builder.setTitle("멤버십 가입")
                    .setMessage("멤버십에 가입하시겠습니까?")
                    .setPositiveButton("예", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // "예" 버튼 클릭 시의 행동
                            onMembershipConfirmed();
                        }
                    })
                    .setNegativeButton("아니요", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // "아니요" 버튼 클릭 시의 행동
                            dialog.dismiss();
                        }
                    })
                    .setCancelable(true); // 다이얼로그 외부 클릭으로 취소 가능
            AlertDialog dialog = builder.create();
            dialog.show();
        }
    }

    // "예" 버튼 클릭 시 호출되는 메서드
    private void onMembershipConfirmed() {
        // 멤버십 가입 로직

        UUID uuid = UserManager.getInstance().getUserId();

        String url = "http://15.164.152.246:8080/profile/" + uuid + "/membership";
        new Thread(() -> {
            String result = httpPostBodyConnection(url, "");
            // 처리 결과 확인
            handler = new Handler(Looper.getMainLooper());
            if (handler != null) {
                handler.post(() -> {
                    Log.d("result", result);
                    Toast.makeText(this, "멤버십에 가입되었습니다.", Toast.LENGTH_LONG).show();
                    UserManager.getInstance().setMembership(true);
                });
            }
        }).start();
    }

    public void updateProfileImage(int badgeId) {
        Log.d("updateProfileImage", "updateProfileImage executed");
        Log.d("updateProfileImage", String.valueOf(badgeId));
        profileImg.setImageResource(BadgeManager.getDrawableForBadgeId(badgeId));

        String url;
        UUID uuid = UserManager.getInstance().getUserId();

        url = "http://15.164.152.246:8080/mybadge/" + uuid + "/" + badgeId;
        // JSON 문자열을 구성하기 위한 StringBuilder 사용
        String data = "";

        // jsonData를 서버에 전송
        Log.d("data", data);
        new Thread(() -> {
            String result = httpPatchBodyConnection(url, data);
            if (handler == null) {
                handler = new Handler(Looper.getMainLooper());
            }
            handler.post(() -> handleResponseAndShowToast(this, result));
        }).start();
    }
    // JSON 응답을 파싱하고, message 값을 Toast로 띄우는 메서드
    public static void handleResponseAndShowToast(Context context, String jsonResponse) {
        try {
            // JSON 응답을 파싱
            JSONObject responseJson = new JSONObject(jsonResponse);

            // message 값 추출
            String message = responseJson.getString("message");

            // 추출한 메시지를 Toast로 표시
            Toast.makeText(context, message, Toast.LENGTH_LONG).show();

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(context, "오류가 발생했습니다.", Toast.LENGTH_LONG).show();
        }
    }
    public String httpPatchBodyConnection(String UrlData, String ParamData) {
        String responseData = "";
        BufferedReader br = null;

        try {
            URL url = new URL(UrlData);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("PATCH");
            conn.setRequestProperty("Content-Type", "application/json; utf-8");
            conn.setRequestProperty("Accept", "application/json");
            conn.setDoOutput(true);

            try (OutputStream os = conn.getOutputStream()) {
                byte[] request_data = ParamData.getBytes("utf-8");
                os.write(request_data);
            }

            conn.connect();

            br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
            StringBuilder sb = new StringBuilder();
            while ((responseData = br.readLine()) != null) {
                sb.append(responseData);
            }

            return sb.toString();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (br != null) {
                    br.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return responseData;
    }

    private void getGeoDataByAddress(String completeAddress) {
        try {
            String API_KEY = "AIzaSyCD4wiVWqJJAq1ipj5VdS4CXVG7ulEswkE";
            String surl = "https://maps.googleapis.com/maps/api/geocode/json?address=" + URLEncoder.encode(completeAddress, "UTF-8") + "&key=" + API_KEY;
            URL url = new URL(surl);
            InputStream is = url.openConnection().getInputStream();

            BufferedReader streamReader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            StringBuilder responseStrBuilder = new StringBuilder();
            String inputStr;
            while ((inputStr = streamReader.readLine()) != null) {
                responseStrBuilder.append(inputStr);
            }

            // JSON 응답 파싱
            JSONObject jo = new JSONObject(responseStrBuilder.toString());

            // API 응답 상태 확인
            String status = jo.getString("status");
            if (!"OK".equals(status)) {
                System.err.println("Geocoding API request failed. Status: " + status);
                return;
            }

            JSONArray results = jo.getJSONArray("results");
            if (results.length() > 0) {
                JSONObject jsonObject = results.getJSONObject(0);
                Double lat = jsonObject.getJSONObject("geometry").getJSONObject("location").getDouble("lat");
                Double lng = jsonObject.getJSONObject("geometry").getJSONObject("location").getDouble("lng");

                // 위도와 경도 설정
                latitude = lat;
                longtitude = lng;

                standardLocation.setLatitude(latitude);
                standardLocation.setLongitude(longtitude);

                System.out.println("LAT: " + lat);
                System.out.println("LNG: " + lng);
            } else {
                System.err.println("No results found for the given address.");
            }
        } catch (FileNotFoundException e) {
            System.err.println("Geocoding API URL not found. Check the API key or URL.");
            e.printStackTrace();
        } catch (IOException e) {
            System.err.println("I/O error while connecting to the Geocoding API.");
            e.printStackTrace();
        } catch (JSONException e) {
            System.err.println("Error parsing JSON response from Geocoding API.");
            e.printStackTrace();
        } catch (Exception e) {
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
    public String httpGetConnection(String UrlData, String s) {
        String totalUrl = UrlData.trim();

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
            url = new URL(totalUrl);
            conn = (HttpURLConnection) url.openConnection();

            //http 요청에 필요한 타입 정의 실시
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json; utf-8");

            //http 요청 실시
            conn.connect();
            System.out.println("http 요청 방식 : " + "GET");
            System.out.println("http 요청 주소 : " + totalUrl);
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
    public String httpGetConnection2(String UrlData, String s) {
        String totalUrl = UrlData.trim();

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
            url = new URL(totalUrl);
            conn = (HttpURLConnection) url.openConnection();

            //http 요청에 필요한 타입 정의 실시
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json; utf-8");

            //http 요청 실시
            conn.connect();
            System.out.println("http 요청 방식 : " + "GET");
            System.out.println("http 요청 주소 : " + totalUrl);
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
}