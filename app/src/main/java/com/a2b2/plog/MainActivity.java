package com.a2b2.plog;

import static android.content.ContentValues.TAG;

import static androidx.core.app.PendingIntentCompat.getActivity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.telephony.ims.ImsMmTelManager;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.wearable.CapabilityClient;
import com.google.android.gms.wearable.CapabilityInfo;
import com.google.android.gms.wearable.DataClient;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataItem;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;
import com.google.firebase.FirebaseApp;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.kakao.sdk.user.model.User;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;



public class MainActivity extends AppCompatActivity implements CapabilityClient.OnCapabilityChangedListener, DataClient.OnDataChangedListener {

    Button loginBtn, idFindBtn, pwFindBtn, joinBtn;
    ImageView kakaoLogin;
    EditText id, pw;
    private String userNickname;
    private UUID userUUID;

    private Handler handler;
    private SharedPreferencesHelper prefsHelper;
    TextView tv1;
    private static final String CAPABILITY_1_NAME = "capability_1";
    private ImageView logoImg;
    private String getToken;
    boolean isMembership;

    Button loginBtn, idFindBtn, pwFindBtn, joinBtn;
    ImageView kakaoLogin;
    EditText id, pw;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        // Firebase 초기화
        FirebaseApp.initializeApp(this);
        FirebaseMessaging.getInstance().getToken().addOnSuccessListener(new OnSuccessListener<String>() {
            @Override
            public void onSuccess(String token) {
               // tb.setToken(token);
                Log.d("FCM Log", "Refreshed token: "+token);
                getToken = token;

            }
        });

//        logoImg = findViewById(R.id.logoImg);
//        logoImg.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(MainActivity.this, HomeActivity.class);
//                startActivity(intent);
//                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
//                finish();
//            }
//        });

        handler = new Handler();


        prefsHelper = new SharedPreferencesHelper(this);
        prefsHelper.saveRoute(null);

        try {
            PackageInfo info = getPackageManager().getPackageInfo(getPackageName(), PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                String keyHash = Base64.encodeToString(md.digest(), Base64.DEFAULT);
                Log.d("KeyHash:", keyHash);
            }
        } catch (PackageManager.NameNotFoundException | NoSuchAlgorithmException e) {
            e.printStackTrace();
        }


        loginBtn = findViewById(R.id.loginBtn);
        joinBtn = findViewById(R.id.joinBtn);


        id = findViewById(R.id.idTxt);
        pw = findViewById(R.id.pwTxt);


        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String url = "http://15.164.152.246:8080/user/signin";
                String data = "{\"userAccount\" : \""+id.getText()+"\",\"userPw\" : \""+pw.getText()+"\",\"deviceToken\" : \""+getToken+"\"}";
                new Thread(() -> {
                    String result = httpPostBodyConnection(url, data);
// JSON 파싱
                    Gson gson = new Gson();
                    JsonObject jsonObject = JsonParser.parseString(result).getAsJsonObject();

// "data" 객체를 먼저 추출
                    JsonObject dataObject = jsonObject.getAsJsonObject("data");
                    JsonElement message = jsonObject.get("message");
                    Log.d("message", message.getAsString());

                    if(message.getAsString().equals("로그인 실패")){
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                TextView loginFail = findViewById(R.id.loginFail);
                                loginFail.setVisibility(View.VISIBLE);
                                Animation shake = AnimationUtils.loadAnimation(MainActivity.this, R.anim.shake_fast);
                                loginFail.startAnimation(shake);
                            }
                        });
                    } else{
//                        runOnUiThread(new Runnable() {
//                            @Override
//                            public void run() {
//                                // "data" 객체에서 "userNickname"과 "userUUID" 추출
//                                userNickname = dataObject.get("userNickname").getAsString();
//                                String userUUIDStr = dataObject.get("userUUID").getAsString();
//                                userUUID = UUID.fromString(userUUIDStr);
//                                Log.d("userNickname", userNickname);
//                                Log.d("userUUIDStr", String.valueOf(userUUID));
//
//                                Intent intent = new Intent(MainActivity.this, HomeActivity.class);
//                                startActivity(intent);
//                                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
//                                finish();
//                            }
//                        });
                        // "data" 객체에서 "userNickname"과 "userUUID" 추출
                        userNickname = dataObject.get("userNickname").getAsString();
                        String userUUIDStr = dataObject.get("userUUID").getAsString();
                        isMembership = dataObject.get("userMembership").getAsBoolean();
                        userUUID = UUID.fromString(userUUIDStr);
                        seeNetworkResult(result);

                        UserManager.getInstance().setMembership(isMembership);

                        Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                        startActivity(intent);
                        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                        finish();
                    }

                }).start();
//                Intent intent = new Intent(MainActivity.this, HomeActivity.class);
//                startActivity(intent);
//                overridePendingTransition(0, 0);
//                finish();

            }
        });
        joinBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, JoinActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                finish();

            }
        });

        //워치 연결 확인 코드
        tv1 = findViewById(R.id.tv1);
        idFindBtn = findViewById(R.id.idFindBtn);
        idFindBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showNodes(CAPABILITY_1_NAME);
            }
        });
        PutDataMapRequest putDataMapReq = PutDataMapRequest.create("/path/to/data");
        putDataMapReq.getDataMap().putString("key", "value");
        PutDataRequest putDataReq = putDataMapReq.asPutDataRequest();
        Task<DataItem> putDataTask = Wearable.getDataClient(this).putDataItem(putDataReq);
        putDataTask.addOnSuccessListener(new OnSuccessListener<DataItem>() {
            @Override
            public void onSuccess(DataItem dataItem) {
                Log.d("MobileApp", "Data item set: " + dataItem);
            }
        });

        pwFindBtn = findViewById(R.id.pwFindBtn);
        pwFindBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isPloggingStart = true;
                sendJsonData(isPloggingStart);
            }
        });

    }
    @Override
    public void onDataChanged(DataEventBuffer dataEvents) {
        for (DataEvent event : dataEvents) {
            if (event.getType() == DataEvent.TYPE_CHANGED) {
                DataItem item = event.getDataItem();
                if (item.getUri().getPath().equals("/json_data")) {
                    DataMap dataMap = DataMapItem.fromDataItem(item).getDataMap();
                    String jsonString = dataMap.getString("json_key");
                    tv1.setText(jsonString);
                    // 받은 JSON 데이터를 처리
                }
            }
        }
    }

    private void stopPlogging() {
        try {
            // JSON 객체 생성
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("stop", false);
            //key1은 스플래시에서 메인으로 넘어갈 때 key2는 플로깅 종료했을 시 스톱워치 종료하게

            // JSON을 문자열로 변환
            String jsonString = jsonObject.toString();

            // Node ID 가져오기 (단말과 워치 간의 연결된 노드)
            Task<List<Node>> nodeListTask = Wearable.getNodeClient(this).getConnectedNodes();
            nodeListTask.addOnSuccessListener(nodes -> {
                for (Node node : nodes) {
                    // MessageClient를 통해 데이터 전송
                    Wearable.getMessageClient(this)
                            .sendMessage(node.getId(), "/path/to/stopPlogging", jsonString.getBytes())
                            .addOnSuccessListener(aVoid -> Log.d("MobileApp", "Message sent successfully"))
                            .addOnFailureListener(e -> Log.e("MobileApp", "Failed to send message", e));
                }
            });
        } catch (Exception e) {
            Log.e("MobileApp", "Failed to create JSON data", e);
        }
    }

    //앱 -> 워치 json 형식 값 전달 확인
    private void sendJsonData(boolean isPloggingStart) {
        try {
            // JSON 객체 생성
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("key1", isPloggingStart);

            // JSON을 문자열로 변환
            String jsonString = jsonObject.toString();

            // Node ID 가져오기 (단말과 워치 간의 연결된 노드)
            Task<List<Node>> nodeListTask = Wearable.getNodeClient(this).getConnectedNodes();
            nodeListTask.addOnSuccessListener(nodes -> {
                for (Node node : nodes) {
                    // MessageClient를 통해 데이터 전송
                    Wearable.getMessageClient(this)
                            .sendMessage(node.getId(), "/path/to/startPlogging", jsonString.getBytes())
                            .addOnSuccessListener(aVoid -> Log.d("MobileApp", "Message sent successfully"))
                            .addOnFailureListener(e -> Log.e("MobileApp", "Failed to send message", e));
                }
            });
        } catch (Exception e) {
            Log.e("MobileApp", "Failed to create JSON data", e);
        }
    }
    @Override
    public void onCapabilityChanged(@NonNull CapabilityInfo capabilityInfo) {
        Log.d(TAG,capabilityInfo.toString());
    }

    private void showNodes(final String... capabilityNames) {

        Task<Map<String, CapabilityInfo>> capabilitiesTask =
                Wearable.getCapabilityClient(this)
                        .getAllCapabilities(CapabilityClient.FILTER_REACHABLE);

        capabilitiesTask.addOnSuccessListener(
                new OnSuccessListener<Map<String, CapabilityInfo>>() {
                    @Override
                    public void onSuccess(Map<String, CapabilityInfo> capabilityInfoMap) {
                        Set<Node> nodes = new HashSet<>();

                        if (capabilityInfoMap.isEmpty()) {
                            showDiscoveredNodes(nodes);
                            return;
                        }
                        for (String capabilityName : capabilityNames) {
                            CapabilityInfo capabilityInfo = capabilityInfoMap.get(capabilityName);
                            if (capabilityInfo != null) {
                                nodes.addAll(capabilityInfo.getNodes());
                            }
                        }
                        showDiscoveredNodes(nodes);
                    }
                });
    }

    private void showDiscoveredNodes(Set<Node> nodes) {
        List<String> nodesList = new ArrayList<>();
        for (Node node : nodes) {
            nodesList.add(node.getDisplayName());
        }
        Log.d(
                TAG,
                "Connected Nodes: "
                        + (nodesList.isEmpty()
                        ? "No connected device was found for the given capabilities"
                        : TextUtils.join(",", nodesList)));
        String msg;
        if (!nodesList.isEmpty()) {
            msg = getString(R.string.connected_nodes, TextUtils.join(", ", nodesList));
        } else {
            msg = getString(R.string.no_device);
        }
        tv1.setText(msg);
    }


    @Override
    public void onResume() {
        super.onResume();
        Wearable.getCapabilityClient(this)
                .addListener(this, Uri.parse("wear://"), CapabilityClient.FILTER_REACHABLE);

        Wearable.getDataClient(this).addListener(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        Wearable.getCapabilityClient(this).removeListener(this);
        Wearable.getDataClient(this).removeListener(this);
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
        Log.d(result, "network");
        UserManager userManager = UserManager.getInstance();
        userManager.setUserNickname(userNickname);
        userManager.setUserId(userUUID);
        userManager.setMembership(isMembership);
        Log.d("닉네임",userNickname);

        Log.d("logintest", userNickname+" "+userUUID);

        Log.d("UserManager.getUserId", String.valueOf(UserManager.getInstance().getUserId()));
        Log.d("UserManager.getUserNickname", UserManager.getInstance().getUserNickname());
//        Log.d("uuid",userUUIDStr);

    }
}