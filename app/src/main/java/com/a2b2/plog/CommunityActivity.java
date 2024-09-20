package com.a2b2.plog;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

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

public class CommunityActivity extends AppCompatActivity {

    private ImageView rank, home, mission, mypage, editBtn;
    private RecyclerView communityRecycler;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    //private List<CommunityItem> communityList;
    private List<CommunityList> communitylists;

    private String nickname, title, date;
    private TextView logo;
    private Handler handler;

    //커뮤니티 테스트 해야 함


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_community);
        logo = findViewById(R.id.title);

        handler = new Handler(Looper.getMainLooper());

        //UUID uuid = UUID.fromString("8D841B8A-C15A-4657-95AC-AB28ED6F0190");
        UUID uuid = UserManager.getInstance().getUserId();
        Log.d("uuid", String.valueOf(uuid));
        // GET 요청을 위한 ParamData 제거
        String url = "http://15.164.152.246:8080/post/"+uuid+"/all";  // 예: http://example.com

        new Thread(() -> {
            String result = httpGetConnection(url, "");
            // 처리 결과 확인
            handler = new Handler(Looper.getMainLooper());
            if (handler != null) {
                handler.post(() -> {
                    if(result != null && !result.isEmpty()) {

                        if (communitylists == null) {
                            communitylists = new ArrayList<>();
                        } else{
                            communitylists.clear();
                        }
                        communitylists.addAll(parseCommunityList(result));
                        // tripPlans 초기화 및 데이터 파싱

//                                tripPlans = parseTripPlan(result);
//                                Log.d("TripPlansSize", "Size of tripPlans after parsing: " + tripPlans.size());

                        // 데이터 확인 로그
                        Log.d("TripPlansSize", "Size of tripPlans after parsing: " + communitylists.size());
                        for (CommunityList clist : communitylists) {
                            Log.d("TripPlan", "포스트아이디 : "+String.valueOf(clist.getPostId())+"제목 : "+clist.getTitle()+"시간 : "+clist.getTime()+"닉네임 :"+clist.getUserNickname());
                        }
                        // UI 갱신
                        updateUI();
                    } else {
                        Log.e("Error", "Result is null or empty");
                    }
                    seeNetworkResult(result);
                });
            }
        }).start();


        // 싱글톤 인스턴스 가져오기

        logo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //UUID uuid = UUID.fromString("8D841B8A-C15A-4657-95AC-AB28ED6F0190");
                UUID uuid = UserManager.getInstance().getUserId();
                Log.d("uuid", String.valueOf(uuid));
                // GET 요청을 위한 ParamData 제거
                String url = "http://15.164.152.246:8080/post/"+uuid+"/all";  // 예: http://example.com

                new Thread(() -> {
                    String result = httpGetConnection(url, "");
                    // 처리 결과 확인
                    handler = new Handler(Looper.getMainLooper());
                    if (handler != null) {
                        handler.post(() -> {
                            if(result != null && !result.isEmpty()) {

                                if (communitylists == null) {
                                    communitylists = new ArrayList<>();
                                } else{
                                    communitylists.clear();
                                }
                                communitylists.addAll(parseCommunityList(result));
                                // tripPlans 초기화 및 데이터 파싱

//                                tripPlans = parseTripPlan(result);
//                                Log.d("TripPlansSize", "Size of tripPlans after parsing: " + tripPlans.size());

                                // 데이터 확인 로그
                                Log.d("TripPlansSize", "Size of tripPlans after parsing: " + communitylists.size());
                                for (CommunityList clist : communitylists) {
                                    Log.d("TripPlan", "포스트아이디 : "+String.valueOf(clist.getPostId())+"제목 : "+clist.getTitle()+"시간 : "+clist.getTime()+"닉네임 :"+clist.getUserNickname());
                                }
                                // UI 갱신
                                updateUI();
                            } else {
                                Log.e("Error", "Result is null or empty");
                            }
                            seeNetworkResult(result);
                        });
                    }
                }).start();
            }
        });

        communitylists = new ArrayList<>();
//        communityList.add(new CommunityItem(R.drawable.tiger,"지구지킴이","2024.08.18(일)","8월 30일에 같이 플로깅 하실 분 있나요?"));
//        communityList.add(new CommunityItem(R.drawable.tiger,"지구지킴이","2024.08.18(일)","8월 30일에 같이 플로깅 하실 분 있나요?"));
//        communityList.add(new CommunityItem(R.drawable.tiger,"지구지킴이","2024.08.18(일)","8월 30일에 같이 플로깅 하실 분 있나요?"));
//        communityList.add(new CommunityItem(R.drawable.tiger,"지구지킴이","2024.08.18(일)","8월 30일에 같이 플로깅 하실 분 있나요?"));
//        communityList.add(new CommunityItem(R.drawable.tiger,"지구지킴이","2024.08.18(일)","8월 30일에 같이 플로깅 하실 분 있나요?"));
//        communityList.add(new CommunityItem(R.drawable.tiger,"지구지킴이","2024.08.18(일)","8월 30일에 같이 플로깅 하실 분 있나요?"));

        communityRecycler = findViewById(R.id.communityRecycler);
        communityRecycler.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        communityRecycler.setLayoutManager(layoutManager);
        adapter = new CommunityAdapter(communitylists, this);
        communityRecycler.setAdapter(adapter);

        rank = findViewById(R.id.rank);
        mission = findViewById(R.id.mission);
        home = findViewById(R.id.home);
        mypage = findViewById(R.id.mypage);
        editBtn = findViewById(R.id.editBtn);

        rank.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CommunityActivity.this, RankActivity.class);
                startActivity(intent);
                overridePendingTransition(0, 0);
                finish();
            }
        });
        mission.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CommunityActivity.this, MissionActivity.class);
                startActivity(intent);
                overridePendingTransition(0, 0);
                finish();
            }
        });
        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CommunityActivity.this, HomeActivity.class);
                startActivity(intent);
                overridePendingTransition(0, 0);
                finish();
            }
        });
        mypage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CommunityActivity.this, MyPageActivity.class);
                startActivity(intent);
                overridePendingTransition(0, 0);
                finish();
            }
        });
        editBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CommunityActivity.this, CommunityPostActivity.class);
                startActivity(intent);
                overridePendingTransition(0, 0);
                finish();
            }
        });

        ImageView myCommunity = findViewById(R.id.joinBtn);
        myCommunity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CommunityActivity.this, MyCommunityActivity.class);
                startActivity(intent);
                finish();
            }
        });

    }

    private void updateUI() {
        TextView nothing = findViewById(R.id.nothing);
        if (communitylists.isEmpty()) {
            nothing.setVisibility(View.VISIBLE);
            communityRecycler.setVisibility(View.GONE);
        } else {
            nothing.setVisibility(View.GONE);
            communityRecycler.setVisibility(View.VISIBLE);
            if (adapter != null) {
                adapter.notifyDataSetChanged();
            }
        }
    }
    public ArrayList<CommunityList> parseCommunityList(String json) {
        ArrayList<CommunityList> communitylists = new ArrayList<>();

        try {

            // 전체 JSON 데이터는 JSONObject로 파싱
            JSONObject jsonObject = new JSONObject(json);

            // JSONObject에서 "data" 필드를 JSONArray로 추출
            JSONArray jsonArray = jsonObject.getJSONArray("data");

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject postObject = jsonArray.getJSONObject(i);

                int postId = 0;
                int badge = 1;
                String title = null;
                String time = null;
                String userNickname = null;

                if (postObject.has("postId")) {
                    postId = postObject.getInt("postId");
                    Log.d("postId", String.valueOf(postId));
                }
                if (postObject.has("badge")) {
                    badge = postObject.getInt("badge");
                    Log.d("badge", String.valueOf(badge));
                }
                if (postObject.has("title")) {
                    title = postObject.getString("title");
                    Log.d("title", title);
                }
                if (postObject.has("time")) {
                    time = postObject.getString("time");
                    Log.d("time", time);
                }
                if (postObject.has("userNickname")) {
                    userNickname = postObject.getString("userNickname");
                    Log.d("userNickname", userNickname);
                }

                if (postId != 0 && badge != 0 && title != null && time != null && userNickname != null) {
                    CommunityList communitylist = new CommunityList(postId, BadgeManager.getDrawableForBadgeId(badge), title, time, userNickname);
                    communitylists.add(communitylist);
                } else {
                    Log.e("JSONError", "Missing key in JSON object: " + postObject.toString());
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return communitylists;
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
}