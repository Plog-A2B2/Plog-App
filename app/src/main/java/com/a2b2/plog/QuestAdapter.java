package com.a2b2.plog;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.ads.AdRequest;
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

public class QuestAdapter extends RecyclerView.Adapter<QuestAdapter.QuestViewHolder> {
    private List<QuestItem> questList;
    private Context context;
    Handler handler = new Handler();
    private RewardedAd rewardedAd;

    public static class QuestViewHolder extends RecyclerView.ViewHolder {
        public TextView questTextView;
        public ImageView coinImageView;
        public TextView coinNumTextView;
        public View finishLine;

        public QuestViewHolder(View itemView) {
            super(itemView);
            questTextView = itemView.findViewById(R.id.questTextView);
            coinImageView = itemView.findViewById(R.id.coinImageView);
            coinNumTextView = itemView.findViewById(R.id.coinNumTextView);
            finishLine = itemView.findViewById(R.id.finishLine);
        }
    }

    public QuestAdapter(List<QuestItem> questList, Context context) {
        this.questList = questList;
        this.context = context;
    }

    @Override
    public QuestViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_quest, parent, false);
        QuestViewHolder evh = new QuestViewHolder(v);
        return evh;
    }

    @Override
    public void onBindViewHolder(QuestViewHolder holder, int position) {
        QuestItem currentItem = questList.get(position);
        holder.questTextView.setText(currentItem.getQuestText());
        holder.coinNumTextView.setText("X " + currentItem.getCoinNum());
// 구글 애드몹 초기화
        MobileAds.initialize(context);
        if (currentItem.isFinish()) {
            holder.finishLine.setVisibility(View.VISIBLE);
        } else {
            holder.finishLine.setVisibility(View.GONE);
        }

        // 아이템 클릭 시 다이얼로그 띄우기
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showMissionCompleteDialog(currentItem);
            }
        });
    }

    @Override
    public int getItemCount() {
        return questList.size();
    }

    // 다이얼로그 표시 메서드
    private void showMissionCompleteDialog(QuestItem questItem) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("미션 완료 확인")
                .setMessage("미션을 완료하시겠습니까?")
                .setPositiveButton("예", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // 서버에 미션 완료 요청
                        sendMissionCompleteRequest(questItem);
                    }
                })
                .setNegativeButton("아니요", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                })
                .setNeutralButton("광고 시청 후 미션 바로 완료하기", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // 광고 시청 후 미션 완료하기 로직
                        showAdAndCompleteMission(questItem);
                    }
                });

        AlertDialog alert = builder.create();
        alert.show();

        // 중립 버튼을 빨간색으로 설정
        alert.getButton(AlertDialog.BUTTON_NEUTRAL).setTextColor(Color.RED);
    }
    // 보상형 광고 로드 메소드
    private void loadRewardedAd() {
        AdRequest adRequest = new AdRequest.Builder().build();
        RewardedAd.load(context, "ca-app-pub-3940256099942544/5224354917", adRequest, new RewardedAdLoadCallback() {
            @Override
            public void onAdLoaded(RewardedAd ad) {
                rewardedAd = ad;
            }

            @Override
            public void onAdFailedToLoad(LoadAdError adError) {
                Log.d("AdError", "Failed to load rewarded ad: " + adError.getMessage());
                rewardedAd = null;
            }

        });
    }
    private void showAdAndCompleteMission(QuestItem questItem) {
        if (!UserManager.getInstance().getIsMembership()) {
            // 광고 시청(구글 애드몹 테스트 광고)
            // 보상형 광고 로드
            loadRewardedAd();

            if (rewardedAd != null && context instanceof Activity) {
                Activity activityContext = (Activity) context;  // Context를 Activity로 캐스팅
                rewardedAd.show(activityContext, rewardItem -> {
                    // 보상 지급 코드 처리
                    int rewardAmount = rewardItem.getAmount();
                    String rewardType = rewardItem.getType();
                    // 보상 처리 로직을 추가

                    Log.d("adtest", "reward get!");

                    UUID uuid = UserManager.getInstance().getUserId();

                    String url = "http://15.164.152.246:8080/mission/" + uuid + "/" + questItem.getQuestId() + "/advertisement";
                    new Thread(() -> {
                        String result = httpPostBodyConnection(url, "");
                        // 처리 결과 확인
                        handler = new Handler(Looper.getMainLooper());
                        if (handler != null) {
                            handler.post(() -> {
                                Log.d("result", result);

                                questItem.setFinish(true);
                                notifyDataSetChanged();  // 미션 완료 상태 업데이트
                            });
                        }
                    }).start();
                });
            }

        }

    }

    // 서버에 미션 완료 여부 확인 요청 메서드
    private void sendMissionCompleteRequest(QuestItem questItem) {
        UUID uuid = UserManager.getInstance().getUserId();

        String url = "http://15.164.152.246:8080/mission/" + uuid + "/" + questItem.getQuestId() + "/successful";
        new Thread(() -> {
            String result = httpPostBodyConnection(url, "");
            // 처리 결과 확인
            handler = new Handler(Looper.getMainLooper());
            if (handler != null) {
                handler.post(() -> {
                    String missionCompleted = parseIsMissionComplete(result);
                    Log.d("isMissionCompleted", String.valueOf(missionCompleted));
                    if (missionCompleted.equals("미션 성공")) {
                        questItem.setFinish(true);
                        notifyDataSetChanged();  // 미션 완료 상태 업데이트
                        Toast.makeText(context, "미션을 완료했습니다!", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(context, "아직 미션을 완료하지 않았습니다.", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }).start();
    }
    public static String parseIsMissionComplete(String jsonResponse) {
        String isComplete = "실패";

        try {
            // JSON 문자열을 JSONObject로 변환
            JSONObject jsonObject = new JSONObject(jsonResponse);

            // "message" 값 추출
            isComplete = jsonObject.getString("message");


        } catch (Exception e) {
            e.printStackTrace();
        }

        return isComplete;
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
}
