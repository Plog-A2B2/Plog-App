package com.a2b2.plog;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.RenderNode;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.View;
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

public class RankActivity extends AppCompatActivity {
    private RecyclerView rankRecyclerView;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private List<RankItem> rankList;
    private ImageView community, home, mission, mypage,
            rank_first_frame, rank_sec_frame, rank_third_frame, first_userimg,sec_userimg,third_userimg, helpBtn;
    private TextView first_username,sec_username,third_username,first_score,sec_score,third_score, helpTxt, MYrank, Mynickname, Myscore;
    private ConstraintLayout background;
    private Handler handler;
    private ConstraintLayout rank_third_all;
    private ConstraintLayout rank_sec_all;
    private ConstraintLayout rank_first_all;
    private TextView rankclear;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rank);

        rank_third_all = findViewById(R.id.rank_third_all);
        rank_sec_all = findViewById(R.id.rank_sec_all);
        rank_first_all = findViewById(R.id.rank_first_all);
        rankclear = findViewById(R.id.rankcleartxt);
        background = findViewById(R.id.rank_background);

        mission = findViewById(R.id.mission);
        community = findViewById(R.id.community);
        home = findViewById(R.id.home);
        mypage = findViewById(R.id.mypage);
        rank_first_frame = findViewById(R.id.rank_first_frame);
        rank_sec_frame = findViewById(R.id.rank_sec_frame);
        rank_third_frame = findViewById(R.id.rank_third_frame);
        first_userimg =findViewById(R.id.rank_first_userimg);
        sec_userimg =findViewById(R.id.rank_sec_userimg);
        third_userimg =findViewById(R.id.rank_third_userimg);
        first_username =findViewById(R.id.rank_first_username);
        sec_username =findViewById(R.id.rank_sec_username);
        third_username =findViewById(R.id.rank_third_username);
        first_score = findViewById(R.id.rank_first_score);
        sec_score = findViewById(R.id.rank_sec_score);
        third_score = findViewById(R.id.rank_third_score);

        rank_first_frame.bringToFront();
        rank_sec_frame.bringToFront();
        rank_third_frame.bringToFront();

        MYrank = findViewById(R.id.MYrank);
        Mynickname = findViewById(R.id.mynickname);
        Myscore = findViewById(R.id.myscore);

        rankList = new ArrayList<>();
//        rankList.add(new RankItem(4, "지구지킴이",936));
//        rankList.add(new RankItem(5, "지구지킴이",873));
//        rankList.add(new RankItem(6, "지구지킴이",450));
//        rankList.add(new RankItem(7, "지구지킴이",450));
//        rankList.add(new RankItem(7, "지구지킴이",450));
//        rankList.add(new RankItem(7, "지구지킴이",450));
//        rankList.add(new RankItem(7, "지구지킴이",450));
//        rankList.add(new RankItem(7, "지구지킴이",450));

        rankRecyclerView = findViewById(R.id.rankRecyclerView);
        rankRecyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        rankRecyclerView.setLayoutManager(layoutManager);
        adapter = new RankAdapter(rankList);
        rankRecyclerView.setAdapter(adapter);

        helpBtn = findViewById(R.id.helpBtn);
        helpTxt = findViewById(R.id.helpTxt);
        handler = new Handler(Looper.getMainLooper());

        UUID uuid = UserManager.getInstance().getUserId();
        Log.d("uuid", String.valueOf(uuid));
        //주소 삽입해야함⭐⭐⭐⭐
        String url = "http://15.164.152.246:8080/rank/"+uuid+"/inquiry";
        new Thread(() -> {
            String result = httpGetConnection(url, "");
            if (handler != null) {
                handler.post(() -> {
                    if(result != null && !result.isEmpty()) {

                        if (rankList == null) {
                            rankList = new ArrayList<>();
                        } else{
                            rankList.clear();
                        }
                        rankList.addAll(parseCommunityList(result));
                        adapter.notifyDataSetChanged();
                        // tripPlans 초기화 및 데이터 파싱

//                                tripPlans = parseTripPlan(result);
//                                Log.d("TripPlansSize", "Size of tripPlans after parsing: " + tripPlans.size());

                        // 데이터 확인 로그
                        Log.d("rankListSize", "Size of tripPlans after parsing: " + rankList.size());
                        for (RankItem rItem : rankList) {
                            Log.d("TripPlan", "배지 : "+String.valueOf(rItem.getBadge())+"순위 : "+rItem.getRank()+" 유저 닉네임 : "+rItem.getUsername()+" 점수 :"+rItem.getScore());
                        }
                        // UI 갱신
                        //updateUI();
                    } else {
                        Log.e("Error", "Result is null or empty");
                    }
                    seeNetworkResult(result);
                });
            }
        }).start();



//        String content = helpTxt.getText().toString(); //텍스트 가져옴.
//        SpannableString spannableString = new SpannableString(content); //객체 생성
//        String word ="달린 거리(m) x 주운 쓰레기 개수";
//        int start = content.indexOf(word);
//        int end = start + word.length();
//
//        spannableString.setSpan(new ForegroundColorSpan(Color.parseColor("#2b5d5b")), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//        spannableString.setSpan(new StyleSpan(Typeface.BOLD), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//        spannableString.setSpan(new RelativeSizeSpan(1.3f), start, end, SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE);

        helpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                helpTxt.bringToFront();
                helpTxt.setVisibility(View.VISIBLE);
            }
        });
        background.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                helpTxt.setVisibility(View.INVISIBLE);
            }
        });
        mission.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RankActivity.this, MissionActivity.class);
                startActivity(intent);
                overridePendingTransition(0, 0);
                finish();
            }
        });
        community.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RankActivity.this, CommunityActivity.class);
                startActivity(intent);
                overridePendingTransition(0, 0);
                finish();
            }
        });
        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RankActivity.this, HomeActivity.class);
                startActivity(intent);
                overridePendingTransition(0, 0);
                finish();
            }
        });
        mypage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RankActivity.this, MyPageActivity.class);
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
    public ArrayList<RankItem> parseCommunityList(String json) {
        ArrayList<RankItem> rankItems = new ArrayList<>();

        try {

            // 전체 JSON 데이터는 JSONObject로 파싱
            JSONObject jsonObject = new JSONObject(json);

            JSONObject dataObject = jsonObject.getJSONObject("data");

            // JSONObject에서 "data" 필드를 JSONArray로 추출
            JSONArray jsonArray = dataObject.getJSONArray("ranking");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject postObject = jsonArray.getJSONObject(i);

                int badge = 0;
                int rank = -1;
                String userName = null;
                int score = -1;

                if (postObject.has("badge")) {
                    badge = postObject.getInt("badge");
                    Log.d("badge", String.valueOf(badge));
                }
                if (postObject.has("rank")) {
                    rank = postObject.getInt("rank");
                    Log.d("rank", String.valueOf(rank));
                }
                if (postObject.has("userNickname")) {
                    userName = postObject.getString("userNickname");
                    Log.d("userNickname", userName);
                }
                if (postObject.has("score")) {
                    score = postObject.getInt("score");
                    Log.d("score", String.valueOf(score));
                }
                if (badge != 0 && userName != null && score != -1) {
                    if(rank ==0){

                        rankclear.setVisibility(View.VISIBLE);
                        rank_third_all.setVisibility(View.INVISIBLE);
                        rank_sec_all.setVisibility(View.INVISIBLE);
                        rank_first_all.setVisibility(View.INVISIBLE);
                    } else {
                        rankclear.setVisibility(View.INVISIBLE);
                        if (rank == 1) {
                            // userName 값을 final 변수에 복사
                            final String finalUserName = userName;
                            final int finalScore = score;
                            final int finalBadge = badge;
                            runOnUiThread(() -> {
                                first_userimg.setImageResource(BadgeManager.getDrawableForBadgeId(finalBadge));
                                first_username.setText(finalUserName);
                                first_score.setText(String.valueOf(finalScore));
                            });
                        } else if (rank == 2) {
                            final String finalUserName = userName;
                            final int finalScore = score;
                            final int finalBadge = badge;
                            runOnUiThread(() -> {
                                sec_userimg.setImageResource(BadgeManager.getDrawableForBadgeId(finalBadge));
                                sec_username.setText(finalUserName);
                                sec_score.setText(String.valueOf(finalScore));
                            });
                        } else if (rank == 3) {
                            final String finalUserName = userName;
                            final int finalScore = score;
                            final int finalBadge = badge;
                            runOnUiThread(() -> {
                                third_userimg.setImageResource(BadgeManager.getDrawableForBadgeId(finalBadge));
                                third_username.setText(finalUserName);
                                third_score.setText(String.valueOf(finalScore));
                            });
                        } else if (rank >= 4){
                            RankItem rankitem = new RankItem(badge, rank, userName, score);
                            rankItems.add(rankitem);
                        } else{
                            Log.e("JSONError", "Missing key in JSON object: " + postObject.toString());
                        }
                    }

                }
            }

            JSONObject mydataObject = dataObject.getJSONObject("mydata");
            int myrank = mydataObject.optInt(("myRank"),0);
            String mynickname = mydataObject.optString("myUsername", "");
            int myscore = mydataObject.optInt(("myScore"),0);
            runOnUiThread(() -> {
                MYrank.setText(String.valueOf(myrank) + "등");
                Mynickname.setText(mynickname);
                Myscore.setText(String.valueOf(myscore) + "점");
            });

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return rankItems;
    }
}