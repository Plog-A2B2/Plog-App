package com.a2b2.plog;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.gms.ads.AdRequest;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MissionActivity extends AppCompatActivity {

    private RecyclerView questRecyclerView;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private List<QuestItem> questList;
    private ImageView rank, community, home, mypage;

    private RecyclerView badgeRecyclerView;
    private BadgeAdapter badgeAdapter;
    private RecyclerView.LayoutManager badgeLayoutManager;
    private List<BadgeItem> badgeList, myBadgeItemList, finalBadgeList;

    private ImageView renewBtn;
    private RewardedAd rewardedAd;
    String url, result;
    String url2, result2;
    String url3, result3;
    Handler handler;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mission);

        // 구글 애드몹 초기화
        MobileAds.initialize(this);

        questList = new ArrayList<>();
//        // 여기에 퀘스트 아이템을 추가합니다.
//        questList.add(new QuestItem("플로깅 3KM 하기", 3));
//        questList.add(new QuestItem("플라스틱 10개 줍기", 2));
//        questList.add(new QuestItem("그룹 플로깅 1회 하기", 5));
//        questList.add(new QuestItem("그룹 플로깅 1회 하기", 5));
//        questList.add(new QuestItem("그룹 플로깅 1회 하기", 5));
//        questList.add(new QuestItem("그룹 플로깅 1회 하기", 5));

        questRecyclerView = findViewById(R.id.questRecyclerView);
        questRecyclerView.setHasFixedSize(true);
        layoutManager = new GridLayoutManager(this, 3, GridLayoutManager.HORIZONTAL, false);
        questRecyclerView.setLayoutManager(layoutManager);

        UUID uuid = UserManager.getInstance().getUserId();
        handler = new Handler();

        url3 = "http://15.164.152.246:8080/mission/" + uuid + "/dailyquest";

        new Thread(() -> {
            result3 = httpGetConnection(url);
            handler.post(() -> {
                if (result3 != null && !result3.isEmpty()) {
                    questList = parseMissions(result3);

                    adapter = new QuestAdapter(questList, this);
                    questRecyclerView.setAdapter(adapter);
                }
            });
        }).start();

        renewBtn = findViewById(R.id.renewBtn);

        // PagerSnapHelper를 사용하여 페이지 넘기기 기능을 추가합니다.
        PagerSnapHelper snapHelper = new PagerSnapHelper();
        snapHelper.attachToRecyclerView(questRecyclerView);


        // 배지 RecyclerView 설정
        badgeList = new ArrayList<>();
        myBadgeItemList = new ArrayList<>();
        finalBadgeList = new ArrayList<>();
//        badgeList.add(new BadgeItem(R.drawable.tiger, "normal"));
//        badgeList.add(new BadgeItem(R.drawable.elephant, "normal"));
//        badgeList.add(new BadgeItem(R.drawable.lion, "normal"));
//        badgeList.add(new BadgeItem(R.drawable.polarbear, "normal"));
//        badgeList.add(new BadgeItem(R.drawable.sturgeon, "normal"));
//        badgeList.add(new BadgeItem(R.drawable.red_wolf, "normal"));
//        badgeList.add(new BadgeItem(R.drawable.stork, "normal"));
//        badgeList.add(new BadgeItem(R.drawable.humpback_whale, "normal"));
//        badgeList.add(new BadgeItem(R.drawable.small_clawed_otter, "normal"));
        for (int i=1; i<BadgeManager.getBadgeMapSize() + 1; i++) {
            int drawableResId = BadgeManager.getDrawableForBadgeId(i);
            badgeList.add(new BadgeItem(i, drawableResId));
        }
        badgeRecyclerView = findViewById(R.id.badgeRecyclerView);

        url = "http://15.164.152.246:8080/mybadge/" + uuid;

        new Thread(() -> {
            result = httpGetConnection(url);
            handler.post(() -> {
                if (result != null && !result.isEmpty()) {
                    myBadgeItemList = parseBadgeAll(result);

                    // 배지 목록을 보여줄 때, 소유한 배지는 정상 이미지, 소유하지 않은 배지는 잠긴 이미지로 설정
                    for (BadgeItem badge : badgeList) {
                        if (!myBadgeItemList.contains(badge)) {
                            badge.setBadgeImage(BadgeManager.getLockedDrawableForBadgeId(badge.getBadgeImage(), this));  // 소유한 배지 이미지
                        }
                        finalBadgeList.add(badge);
                    }
                    // 어댑터 설정
                    badgeRecyclerView.setHasFixedSize(true);
                    badgeLayoutManager = new GridLayoutManager(this, 3);
                    badgeRecyclerView.setLayoutManager(badgeLayoutManager);
                    badgeAdapter = new BadgeAdapter(finalBadgeList);
                    badgeRecyclerView.setAdapter(badgeAdapter);
                }
            });
        }).start();

        badgeAdapter.setOnItemClickListener(new BadgeAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                BadgeItem clickedBadge = badgeList.get(position);
                showUnlockCondition(clickedBadge);
            }
        });



        renewBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!UserManager.getInstance().getIsMembership()) {
                    // 광고 시청(구글 애드몹 테스트 광고)
                    // 보상형 광고 로드
                    loadRewardedAd();

                    if (rewardedAd != null) {
                        rewardedAd.show(MissionActivity.this, rewardItem -> {
                            // 보상 지급 코드 처리
                            int rewardAmount = rewardItem.getAmount();
                            String rewardType = rewardItem.getType();
                            // 보상 처리 로직을 추가

                            Log.d("adtest", "reward get!");

                        });
                    }
                }
                UUID uuid = UserManager.getInstance().getUserId();

                String url = "http://15.164.152.246:8080/mission/" + uuid + "/reroll";
                new Thread(() -> {
                    String result = httpPostBodyConnection(url, "");
                    // 처리 결과 확인
                    handler = new Handler(Looper.getMainLooper());
                    if (handler != null) {
                        handler.post(() -> {
                            questList = parseMissions(result);

                            adapter = new QuestAdapter(questList, MissionActivity.this);
                            questRecyclerView.setAdapter(adapter);
                        });
                    }
                }).start();

            }
        });

        rank = findViewById(R.id.rank);
        community = findViewById(R.id.community);
        home = findViewById(R.id.home);
        mypage = findViewById(R.id.mypage);

        rank.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MissionActivity.this, RankActivity.class);
                startActivity(intent);
                overridePendingTransition(0, 0);
                finish();
            }
        });

        community.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MissionActivity.this, CommunityActivity.class);
                startActivity(intent);
                overridePendingTransition(0, 0);
                finish();
            }
        });
        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MissionActivity.this, HomeActivity.class);
                startActivity(intent);
                overridePendingTransition(0, 0);
                finish();
            }
        });
        mypage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MissionActivity.this, MyPageActivity.class);
                startActivity(intent);
                overridePendingTransition(0, 0);
                finish();
            }
        });

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
    // JSON 응답을 파싱하여 미션 리스트를 반환하는 메서드
    public static List<QuestItem> parseMissions(String jsonResponse) {
        List<QuestItem> missionList = new ArrayList<>();

        try {
            // JSON 응답 파싱
            JSONObject responseJson = new JSONObject(jsonResponse);
            JSONArray dataArray = responseJson.getJSONArray("data");

            // 각 미션 정보를 파싱하여 리스트에 추가
            for (int i = 0; i < dataArray.length(); i++) {
                JSONObject missionObject = dataArray.getJSONObject(i);

                int missionId = missionObject.getInt("missionId");
                int missionCoin = missionObject.getInt("missionCoin");
                String missionDescription = missionObject.getString("mission");
                boolean isFinish = false;
                if (missionObject.has("isFinish")) {
                    isFinish = missionObject.getBoolean("isFinish");
                }

                // Mission 객체 생성 후 리스트에 추가
                QuestItem mission = new QuestItem(missionId, missionDescription, missionCoin);
                mission.setFinish(isFinish);
                missionList.add(mission);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return missionList;
    }
    private void showUnlockCondition(BadgeItem badgeItem) {
        // Inflate the custom layout
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.badge_unlock_condition, null);
        TextView unlockConditionText = dialogView.findViewById(R.id.unlockConditionText);

        Handler handler = new Handler();

        url2 = "http://15.164.152.246:8080/badge/" + BadgeManager.getBadgeIdForLockedDrawable(badgeItem.getBadgeImage(), this) + "/conditions";

        new Thread(() -> {
            result2 = httpGetConnection(url2);
            handler.post(() -> {
                if (result2 != null && !result2.isEmpty()) {
                    parseBadgeUnlockCondition(result2, badgeItem);

                    unlockConditionText.setText(badgeItem.getUnlockCondition());
                    // Create and show the dialog
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setView(dialogView)
                            .setPositiveButton("확인", null);
                    CustomBadgeDialog dlg = new CustomBadgeDialog(MissionActivity.this, badgeItem);
                    dlg.show();
                }
            });
        }).start();


    }

    public static void parseBadgeUnlockCondition(String jsonResponse, BadgeItem badge) {
        try {
            // JSON 응답을 JSONObject로 변환
            JSONObject jsonObject = new JSONObject(jsonResponse);
            JSONObject dataObject = jsonObject.getJSONObject("data");

            // 필요한 데이터 추출
            int badgeId = dataObject.getInt("badgeId");
            String badgeGoal = dataObject.getString("badgeGoal");
            int cost = dataObject.getInt("cost");

            // BadgeUnlockCondition 객체로 반환
            badge.setCost(cost);
            badge.setUnlockCondition(badgeGoal);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 보상형 광고 로드 메소드
    private void loadRewardedAd() {
        AdRequest adRequest = new AdRequest.Builder().build();
        RewardedAd.load(MissionActivity.this, "ca-app-pub-3940256099942544/5224354917", adRequest, new RewardedAdLoadCallback() {
            @Override
            public void onAdLoaded(RewardedAd ad) {
                rewardedAd = ad;
            }

            @Override
            public void onAdFailedToLoad(LoadAdError adError) {
                // 광고 로드 실패 처리
                rewardedAd = null;
            }
        });
    }

    public static List<BadgeItem> parseBadgeAll(String jsonResponse) {
        List<Integer> ownedBadgeIds = new ArrayList<>();
        List<BadgeItem> myBadgeItemList = new ArrayList<>();
        try {
            // JSON 응답을 JSONObject로 변환
            JSONObject jsonObject = new JSONObject(jsonResponse);

            // 'data' 배열에서 배지 ID를 추출
            JSONArray dataArray = jsonObject.getJSONArray("data");

            // 각 배지 ID를 리스트에 추가
            for (int i = 0; i < dataArray.length(); i++) {
                int badgeId = dataArray.getInt(i);
                ownedBadgeIds.add(badgeId);
            }

        } catch (Exception e) {
            e.printStackTrace();  // 에러 발생 시 로그 출력
        }

        for(int i=1; i<ownedBadgeIds.size()+1; i++) {
            int drawableResId = BadgeManager.getDrawableForBadgeId(i);
            myBadgeItemList.add(new BadgeItem(i, drawableResId));
        }

        return myBadgeItemList;  // 소유한 배지 리스트 반환
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
}