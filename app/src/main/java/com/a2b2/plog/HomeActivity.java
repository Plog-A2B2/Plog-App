package com.a2b2.plog;

import static androidx.core.content.ContentProviderCompat.requireContext;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

public class HomeActivity extends AppCompatActivity {


    private RecyclerView recyclerView;
    private PloggerAdapter ploggerAdapter;
    private List<RealtimePloggerItem> ploggingItems;
    private ImageView rank, community, mission, mypage;
    private SwitchCompat trashcanVisibleSwitch;
    boolean trashcanVisible = true;
    private Button setRoute;
    private EditText trashGoalEditText;
    int trashGoal = 0;
    private Button plggingStartBut;
    private SharedPreferencesHelper prefsHelper;
    Handler handler;
    private ImageView logo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        prefsHelper = new SharedPreferencesHelper(this);

        recyclerView = findViewById(R.id.ploggerRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        logo = findViewById(R.id.logoImg);
        logo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handler = new Handler();
                String url = "http://15.164.152.246:8080/profile/active";
                UUID uuid = UUID.fromString("57F7C28F-67A6-4091-B837-8C3168653B81");

                new Thread(() -> {
                    String result = httpGetConnection(url, "");
                    ploggingItems = parseCommunityList(result);
                    // 처리 결과 확인
                    handler.post(() -> seeNetworkResult(result));
                }).start();
            }
        });

        handler = new Handler();


        String url = "http://15.164.152.246:8080/profile/active";
        UUID uuid = UUID.fromString("57F7C28F-67A6-4091-B837-8C3168653B81");

        new Thread(() -> {
            String result = httpGetConnection(url, "");
            ploggingItems = parseCommunityList(result);
            // 처리 결과 확인
            handler.post(() -> {
                seeNetworkResult(result);
                ploggerAdapter = new PloggerAdapter(ploggingItems);
                recyclerView.setAdapter(ploggerAdapter);
            });
        }).start();


//        ploggingItems = new ArrayList<>();
//        // 샘플 데이터 추가
//        ploggingItems.add(new RealtimePloggerItem(R.drawable.badgetest, "nickname"));
//        ploggingItems.add(new RealtimePloggerItem(R.drawable.badgetest, "nickname"));
//        ploggingItems.add(new RealtimePloggerItem(R.drawable.badgetest, "nickname"));
//        ploggingItems.add(new RealtimePloggerItem(R.drawable.badgetest, "nickname"));
//        ploggingItems.add(new RealtimePloggerItem(R.drawable.badgetest, "nickname"));
//        ploggingItems.add(new RealtimePloggerItem(R.drawable.badgetest, "nickname"));




        trashcanVisibleSwitch = findViewById(R.id.trashcanVisibleSwitch);
        setRoute = findViewById(R.id.routeBtn);

        if (prefsHelper.getRoute() != null) {
            setRoute.setText(prefsHelper.getRoute().getOrigin());
        }

        trashcanVisibleSwitch.setChecked(true); //디폴트값: 쓰레기통 위치 보이기

        //쓰레기통 위치 보이게 하기로 하면 서버로 쓰레기통 위치 달라고 요청해야함 유유아이디, 현위치 위도 경도, 문의하는 내용

        // 스위치 상태 변경 리스너 설정
        trashcanVisibleSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            trashcanVisible = isChecked;
            //Log.d("trashcanVisible", "trashcan : " + trashcanVisible);
        });

        setRoute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, RouteListActivity.class);
                startActivity(intent);
                overridePendingTransition(0, 0);
                finish();
            }
        });

        plggingStartBut = findViewById(R.id.startBtn);

        plggingStartBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(HomeActivity.this, PloggingActivity.class);
                intent.putExtra("ploggingItems", new ArrayList<>(ploggingItems));
                intent.putExtra("trashcanVisible", trashcanVisible);

                startActivity(intent);
                overridePendingTransition(0, 0);
                finish();
            }
        });

        rank = findViewById(R.id.rank);
        community = findViewById(R.id.community);
        mission = findViewById(R.id.mission);
        mypage = findViewById(R.id.mypage);

        rank.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, RankActivity.class);
                startActivity(intent);
                overridePendingTransition(0, 0);
                finish();
            }
        });

        community.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, CommunityActivity.class);
                startActivity(intent);
                overridePendingTransition(0, 0);
                finish();
            }
        });
        mission.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, MissionActivity.class);
                startActivity(intent);
                overridePendingTransition(0, 0);
                finish();
            }
        });
        mypage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, MyPageActivity.class);
                startActivity(intent);
                overridePendingTransition(0, 0);
                finish();
            }
        });
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
    public void seeNetworkResult(String result) {
        // 네트워크 작업 완료 후
        Log.d(result, "network");
    }
    public ArrayList<RealtimePloggerItem> parseCommunityList(String json) {
        ArrayList<RealtimePloggerItem> ploggerList = new ArrayList<>();

        try {
            // JSON 객체를 파싱하여 "data" 배열을 가져옵니다.
            JsonObject jsonObject = JsonParser.parseString(json).getAsJsonObject();
            JsonArray jsonArray = jsonObject.getAsJsonArray("data");

            // "data" 배열을 순회하며 각 객체를 파싱합니다.
            for (int i = 0; i < jsonArray.size(); i++) {
                JsonObject itemObject = jsonArray.get(i).getAsJsonObject();

                String userNickname = null;
                int badgeId = 0;

                // JSON 객체에서 필요한 값을 추출합니다.
                if (itemObject.has("userNickname")) {
                    userNickname = itemObject.get("userNickname").getAsString();
                    Log.d("userNickname", userNickname);
                }

                if (itemObject.has("badgeId")) {
                    badgeId = itemObject.get("badgeId").getAsInt();
                    Log.d("badgeId", String.valueOf(badgeId));
                }
                BadgeManager badgeManager = new BadgeManager();
                // 모든 필드가 존재할 경우 리스트에 추가합니다.
                if (userNickname != null) {
                    RealtimePloggerItem ploggerItem = new RealtimePloggerItem(badgeManager.getDrawableForBadgeId(badgeId), userNickname);
                    ploggerList.add(ploggerItem);
                } else {
                    Log.e("JSONError", "Missing key in JSON object: " + itemObject.toString());
                }
            }
        } catch (JsonParseException e) {
            e.printStackTrace();
        }
        return ploggerList;
    }

}