package com.a2b2.plog;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.StateListDrawable;
import android.os.Bundle;
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

public class MyCommunityActivity extends AppCompatActivity {

    private Button mytext, finish,like;
    private Button selectedButton;
    private RecyclerView MycommunityRecycler;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private List<CommunityList> MycommunityList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_community);

        MycommunityList = new ArrayList<>();
//        MycommunityList.add(new CommunityItem(R.drawable.tiger,"지구지킴이","2024.08.18(일)","8월 30일에 같이 플로깅 하실 분 있나요?"));
//        MycommunityList.add(new CommunityItem(R.drawable.tiger,"지구지킴이","2024.08.18(일)","8월 30일에 같이 플로깅 하실 분 있나요?"));
//        MycommunityList.add(new CommunityItem(R.drawable.tiger,"지구지킴이","2024.08.18(일)","8월 30일에 같이 플로깅 하실 분 있나요?"));
//        MycommunityList.add(new CommunityItem(R.drawable.tiger,"지구지킴이","2024.08.18(일)","8월 30일에 같이 플로깅 하실 분 있나요?"));
//        MycommunityList.add(new CommunityItem(R.drawable.tiger,"지구지킴이","2024.08.18(일)","8월 30일에 같이 플로깅 하실 분 있나요?"));
//        MycommunityList.add(new CommunityItem(R.drawable.tiger,"지구지킴이","2024.08.18(일)","8월 30일에 같이 플로깅 하실 분 있나요?"));

        MycommunityRecycler = findViewById(R.id.recyclerView);
        MycommunityRecycler.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        MycommunityRecycler.setLayoutManager(layoutManager);
        adapter = new CommunityAdapter(MycommunityList, this);
        MycommunityRecycler.setAdapter(adapter);

        mytext = findViewById(R.id.mytext);
        finish = findViewById(R.id.finish);
        like = findViewById(R.id.like);
        ImageView backBtn = findViewById(R.id.backBtn);

        //resetButtonBackgrounds();
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MyCommunityActivity.this, CommunityActivity.class);
                startActivity(intent);
                finish();
            }
        });

        TextView check = findViewById(R.id.check);

        if (check.getText() == "mytext"){
            setButtonPressedState(mytext,true);
        }
        mytext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mytext.setBackgroundResource(R.drawable.round_white);
                finish.setBackgroundResource(R.drawable.round_gray);
                like.setBackgroundResource(R.drawable.round_gray);
            }
        });
        finish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish.setBackgroundResource(R.drawable.round_white);
                mytext.setBackgroundResource(R.drawable.round_gray);
                like.setBackgroundResource(R.drawable.round_gray);
            }
        });
        like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                like.setBackgroundResource(R.drawable.round_white);
                finish.setBackgroundResource(R.drawable.round_gray);
                mytext.setBackgroundResource(R.drawable.round_gray);
            }
        });

        Drawable roundWhiteDrawable = getResources().getDrawable(R.drawable.round_white);

    }
    // 모든 버튼의 배경을 기본값으로 초기화하는 메서드
    private void resetButtonBackgrounds() {
        setButtonBackground(mytext, "#E5E5EC");
        setButtonBackground(finish, "#E5E5EC");
        setButtonBackground(like, "#E5E5EC");
    }

    // 버튼의 상태를 업데이트하는 메서드
    private void updateButtonState(Button clickedButton) {
        if (selectedButton != null) {
            setButtonBackground(selectedButton, "#E5E5EC"); // 기본 색상으로 설정
        }

        setButtonBackground(clickedButton, "#FFFFFF"); // 흰색으로 설정
        selectedButton = clickedButton; // 현재 클릭된 버튼을 선택된 버튼으로 설정
    }
    // 버튼의 pressed 상태를 설정하는 메서드
    private void setButtonPressedState(Button button, boolean pressed) {
        StateListDrawable stateListDrawable = (StateListDrawable) button.getBackground();
        if (pressed) {
            // Button을 'pressed' 상태로 설정
            stateListDrawable.setState(new int[]{android.R.attr.state_pressed});
        } else {
            // Button을 기본 상태로 설정
            stateListDrawable.setState(new int[]{});
        }
        button.setBackground(stateListDrawable);
    }

    // 버튼의 배경색을 설정하는 메서드
    private void setButtonBackground(Button button, String color) {
        Drawable background = button.getBackground();
        if (background instanceof StateListDrawable) {
            StateListDrawable stateListDrawable = (StateListDrawable) background;
            Drawable current = stateListDrawable.getCurrent();

            if (current instanceof GradientDrawable) {
                GradientDrawable gradientDrawable = (GradientDrawable) current;
                gradientDrawable.setColor(Color.parseColor(color));
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
                    CommunityList communitylist = new CommunityList(postId, R.drawable.tiger, title, time, userNickname);
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