package com.a2b2.plog;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MyPloggingActivity extends AppCompatActivity implements MyPloggingAdapter.OnItemClickListener {

    private RecyclerView recyclerView;
    private MyPloggingAdapter adapter;
    private List<MyPloggingItem> myPloggingItemList;
    String url, result;

    private ImageView rank, community, mission, mypage, home, back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_plogging);
        back = findViewById(R.id.backBtn);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MyPloggingActivity.this, MyPageActivity.class);
                startActivity(intent);
                overridePendingTransition(0, 0);
                finish();
            }
        });

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        myPloggingItemList = new ArrayList<>();

        Handler handler = new Handler();

        UUID uuid = UserManager.getInstance().getUserId();
        url = "http://15.164.152.246:8080/activitys/" + uuid + "/ploggings";

        new Thread(() -> {
            result = httpGetConnection(url);
            handler.post(() -> {
                if (result != null && !result.isEmpty()) {
                    myPloggingItemList = parseRouteAll(result);

                    // 어댑터 설정
                    adapter = new MyPloggingAdapter(myPloggingItemList, this, this);
                    recyclerView.setAdapter(adapter);
                }
            });
        }).start();

        // 하단 네비게이션 메뉴 설정
        setupNavigationMenu();
    }

    public ArrayList<MyPloggingItem> parseRouteAll(String json) {
        ArrayList<MyPloggingItem> MyPloggingItems = new ArrayList<>();

        try {
            JSONObject jsonObject = new JSONObject(json);
            JSONArray dataArray = jsonObject.getJSONArray("data");

            for (int i = 0; i < dataArray.length(); i++) {
                JSONObject dataObject = dataArray.getJSONObject(i);

                // JSON 데이터 추출
                int activityId = dataObject.getInt("activityId");
                JSONArray dateArray = dataObject.getJSONArray("ploggingDate");
                String ploggingDate = dateArray.getInt(0) + "." + dateArray.getInt(1) + "." + dateArray.getInt(2);
                int ploggingTime = dataObject.getInt("ploggingTime");
                double distance = dataObject.getDouble("distance");
                int trashSum = dataObject.getInt("trash_sum");

                // MyPloggingItem 객체 생성
                MyPloggingItem item = new MyPloggingItem(activityId, ploggingDate, ploggingTime, distance, trashSum);
                MyPloggingItems.add(item);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return MyPloggingItems;
    }

    public String httpGetConnection(String UrlData) {
        String responseData = "";
        BufferedReader br = null;

        try {
            URL url = new URL(UrlData);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Content-Type", "application/json; utf-8");
            conn.setRequestProperty("Accept", "application/json");

            br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
            StringBuilder sb = new StringBuilder();
            while ((responseData = br.readLine()) != null) {
                sb.append(responseData);
            }

            return sb.toString();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } finally {
            try {
                if (br != null) {
                    br.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void seeNetworkResult(String result) {
        Log.d("NetworkResult", result);
    }

    @Override
    public void onItemClick(MyPloggingItem activity) {
        // 클릭 시 플로깅 경로 상세 정보를 다이얼로그로 보여줄 수 있음
        MyPloggingDialogFragment dialog = MyPloggingDialogFragment.newInstance(activity);
        dialog.show(getSupportFragmentManager(), "MyPloggingDialogFragment");
    }

    // 하단 네비게이션 메뉴 클릭 이벤트 설정
    private void setupNavigationMenu() {
        rank = findViewById(R.id.rank);
        community = findViewById(R.id.community);
        mission = findViewById(R.id.mission);
        mypage = findViewById(R.id.mypage);
        home = findViewById(R.id.home);

        rank.setOnClickListener(v -> navigateTo(RankActivity.class));
        community.setOnClickListener(v -> navigateTo(CommunityActivity.class));
        mission.setOnClickListener(v -> navigateTo(MissionActivity.class));
        mypage.setOnClickListener(v -> navigateTo(MyPageActivity.class));
        home.setOnClickListener(v -> navigateTo(HomeActivity.class));
    }

    // 네비게이션을 통해 액티비티 전환
    private void navigateTo(Class<?> targetActivity) {
        Intent intent = new Intent(MyPloggingActivity.this, targetActivity);
        startActivity(intent);
        overridePendingTransition(0, 0);
        finish();
    }
}
