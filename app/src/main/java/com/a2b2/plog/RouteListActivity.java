package com.a2b2.plog;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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


public class RouteListActivity extends AppCompatActivity implements RouteAdapter.OnItemClickListener{

    private RecyclerView recyclerView;
    private RouteAdapter routeAdapter;
    private List<Route> routeList;

    private ImageView rank, community, mission, mypage, home, back;
    String url, result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route_list);

        back = findViewById(R.id.backBtn);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RouteListActivity.this, HomeActivity.class);
                startActivity(intent);
                overridePendingTransition(0, 0);
                finish();
            }
        });

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        routeList = new ArrayList<>();

        Handler handler = new Handler();

        url = "http://15.164.152.246:8080/activitys/all-route";
        String data = "";

        new Thread(() -> {
            result = httpPostBodyConnection(url, data);
            handler.post(() -> { seeNetworkResult(result);
                if(result != null && !result.isEmpty())
                    routeList = parseRouteAll(result);

                routeAdapter = new RouteAdapter(routeList, this, this);
                recyclerView.setAdapter(routeAdapter);
            });
        }).start();

        rank = findViewById(R.id.rank);
        community = findViewById(R.id.community);
        mission = findViewById(R.id.mission);
        mypage = findViewById(R.id.mypage);
        home = findViewById(R.id.home);

        rank.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RouteListActivity.this, RankActivity.class);
                startActivity(intent);
                overridePendingTransition(0, 0);
                finish();
            }
        });

        community.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RouteListActivity.this, CommunityActivity.class);
                startActivity(intent);
                overridePendingTransition(0, 0);
                finish();
            }
        });
        mission.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RouteListActivity.this, MissionActivity.class);
                startActivity(intent);
                overridePendingTransition(0, 0);
                finish();
            }
        });
        mypage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RouteListActivity.this, MyPageActivity.class);
                startActivity(intent);
                overridePendingTransition(0, 0);
                finish();
            }
        });
        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RouteListActivity.this, HomeActivity.class);
                startActivity(intent);
                overridePendingTransition(0, 0);
                finish();
            }
        });
    }
    @Override
    public void onItemClick(Route route) {
        RouteDialogFragment dialog = RouteDialogFragment.newInstance(route);
        dialog.show(getSupportFragmentManager(), "RouteDialog");
    }

    public ArrayList<Route> parseRouteAll(String json) {
        ArrayList<Route> RouteList = new ArrayList<>();

        try {
            // JSON 전체 객체를 먼저 파싱합니다.
            JSONObject jsonObject = new JSONObject(json);

            // "data" 키에 있는 JSON 배열을 추출합니다.
            JSONArray dataArray = jsonObject.getJSONArray("data");

            for (int i = 0; i < dataArray.length(); i++) {
                JSONObject dataObject = dataArray.getJSONObject(i);

                // 필요한 데이터 추출
                int ploggingTime = dataObject.getInt("ploggingTime");
                double distance = dataObject.getDouble("distance");
                String startPlace = dataObject.getString("startPlace");
                String endPlace = dataObject.getString("endPlace");
                int routeId = dataObject.getInt("activityId");

                // 필요한 로그 출력
                Log.d("시작 장소", startPlace);
                Log.d("종료 장소", endPlace);
                Log.d("거리", String.valueOf(distance));
                Log.d("소요 시간", String.valueOf(ploggingTime));
                Log.d("루트 ID", String.valueOf(routeId));

                // Route 객체 생성 및 리스트에 추가
                Route route = new Route(routeId, startPlace,
                        endPlace, distance, ploggingTime);
                RouteList.add(route);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return RouteList;
    }


    public String httpPostBodyConnection(String UrlData, String ParamData) {
        String responseData = "";
        BufferedReader br = null;

        try {
            URL url = new URL(UrlData);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
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

    public void seeNetworkResult(String result) {
        Log.d("NetworkResult", result);
    }
}