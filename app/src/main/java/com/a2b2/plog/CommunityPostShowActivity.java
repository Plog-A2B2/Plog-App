package com.a2b2.plog;


import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.UUID;

//커뮤니티 게시글 상세조회 액티비티
public class CommunityPostShowActivity extends AppCompatActivity {

    private ImageView backBtn,badge;
    private TextView nickname, date, postTitle, ploggingPlace, meetingDate, meetingPlace, posting, deleteBtn;
    private Button likeBtn,joinBtn;
    private boolean isLikeCheck =false;
    private boolean isJoinCheck =false;
    private Handler handler;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_community_post_show);

        backBtn = findViewById(R.id.backBtn);
        badge = findViewById(R.id.badge);
        nickname = findViewById(R.id.nickname);
        date = findViewById(R.id.date);
        postTitle = findViewById(R.id.title);
        ploggingPlace = findViewById(R.id.ploggingPlace);
        meetingDate = findViewById(R.id.meetingDate);
        meetingPlace = findViewById(R.id.meetingPlace);
        posting = findViewById(R.id.posting);
        deleteBtn = findViewById(R.id.deleteBtn);
        likeBtn = findViewById(R.id.likeBtn);
        joinBtn = findViewById(R.id.joinBtn);

        int postId =getIntent().getIntExtra("postId", 0);;

        UUID uuid = UserManager.getInstance().getUserId();
        Log.d("uuid", String.valueOf(uuid));
        Log.d("postId", String.valueOf(postId));
        // GET 요청을 위한 ParamData 제거
        String url = "http://15.164.152.246:8080/post/"+uuid+"/"+postId+"/post_detail";
        new Thread(() -> {
            String result = httpGetConnection(url, "");
            // 처리 결과 확인
            handler = new Handler(Looper.getMainLooper());
            if (handler != null) {
                handler.post(() -> {
                    if(result != null && !result.isEmpty()) {
                        parseCommunityDetail(result);

                        //찜버튼이 true/false인지 확인하고 통신하는 코드 추가하기

                    } else {
                        Log.e("Error", "Result is null or empty");
                    }
                    seeNetworkResult(result);
                });
            }
        }).start();

        //삭제하는 버튼 클릭 시 지우는 통신 코드 추가하기

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CommunityPostShowActivity.this, CommunityActivity.class);
                startActivity(intent);
                overridePendingTransition(0, 0);
                finish();
            }
        });
        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 팝업 다이얼로그 생성
                AlertDialog.Builder builder = new AlertDialog.Builder(CommunityPostShowActivity.this);
                builder.setMessage("게시글을 삭제하시겠습니까?")
                        .setPositiveButton("네", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // "네" 버튼 클릭 시 알림창 표시
                                Toast.makeText(CommunityPostShowActivity.this, "삭제완료", Toast.LENGTH_SHORT).show();
                                //게시글 삭제하는 통신

                                // 다이얼로그 닫기
                                dialog.dismiss();
                                finish();
                            }
                        })
                        .setNegativeButton("아니요", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // "아니요" 버튼 클릭 시 다이얼로그 닫기
                                dialog.dismiss();
                            }
                        });
                // 다이얼로그 보여주기
                AlertDialog alert = builder.create();
                alert.show();
            }
        });
        likeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(),"찜 완료!", Toast.LENGTH_SHORT).show();
                //서버랑 통신
            }
        });
        joinBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(),"참가 완료!", Toast.LENGTH_SHORT).show();
                //서버랑 통신
            }
        });
    }
    public ArrayList<CommunityPostShowItem> parseCommunityDetail(String json) {
        ArrayList<CommunityPostShowItem> communitylists = new ArrayList<>();

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
                String content = null;
                String plogPlace = null;
                String meetPlace = null;
                String time = null;
                String schedule = null;
                String userNickname = null;
                boolean joined =false;
                boolean liked = false;

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
                if (postObject.has("content")) {
                    content = postObject.getString("content");
                    Log.d("content", content);
                }
                if (postObject.has("plogPlace")) {
                    plogPlace = postObject.getString("plogPlace");
                    Log.d("title", plogPlace);
                }
                if (postObject.has("meetPlace")) {
                    meetPlace = postObject.getString("meetPlace");
                    Log.d("title", meetPlace);
                }
                if (postObject.has("title")) {
                    title = postObject.getString("title");
                    Log.d("title", title);
                }
                if(postObject.has("joined")){
                    joined = postObject.getBoolean("joined");
                    Log.d("joined", String.valueOf(joined));
                }
                if(postObject.has("liked")){
                    liked = postObject.getBoolean("liked");
                    Log.d("liked", String.valueOf(liked));
                }

                if (postId != 0 && badge != 0 && title != null && time != null && userNickname != null && content !=null &&schedule !=null && plogPlace !=null && meetPlace!=null) {
                    nickname.setText(userNickname);
                    date.setText(time);
                    postTitle.setText(title);
                    ploggingPlace.setText(plogPlace);
                    meetingDate.setText(schedule);
                    meetingPlace.setText(meetPlace);
                    posting.setText(content);
                    if(joined == true){
                        joinBtn.setText("참여완료");
                        joinBtn.setBackgroundColor(getResources().getColor(R.color.joined));
                        //joinBtn.setBackgroundColor(getResources().getColor(R.drawable.joined));
                    } else{
                        joinBtn.setText("참여하기");
                    }
                    if(liked == true){
                        likeBtn.setText("참여완료");
                        likeBtn.setBackgroundColor(getResources().getColor(R.color.liked));
                        //likeBtn.setBackgroundColor(getResources().getColor(R.drawable.liked));
                    }


                    //setText로 바꾸기
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