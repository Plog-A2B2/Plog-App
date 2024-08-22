package com.a2b2.plog;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

public class CommunityActivity extends AppCompatActivity {

    private ImageView rank, home, mission, mypage, editBtn;
    private RecyclerView communityRecycler;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private List<CommunityItem> communityList;

    private String nickname, title, date;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_community);

        communityList = new ArrayList<>();
        communityList.add(new CommunityItem(R.drawable.tiger,"지구지킴이","2024.08.18(일)","8월 30일에 같이 플로깅 하실 분 있나요?"));
        communityList.add(new CommunityItem(R.drawable.tiger,"지구지킴이","2024.08.18(일)","8월 30일에 같이 플로깅 하실 분 있나요?"));
        communityList.add(new CommunityItem(R.drawable.tiger,"지구지킴이","2024.08.18(일)","8월 30일에 같이 플로깅 하실 분 있나요?"));
        communityList.add(new CommunityItem(R.drawable.tiger,"지구지킴이","2024.08.18(일)","8월 30일에 같이 플로깅 하실 분 있나요?"));
        communityList.add(new CommunityItem(R.drawable.tiger,"지구지킴이","2024.08.18(일)","8월 30일에 같이 플로깅 하실 분 있나요?"));
        communityList.add(new CommunityItem(R.drawable.tiger,"지구지킴이","2024.08.18(일)","8월 30일에 같이 플로깅 하실 분 있나요?"));

        communityRecycler = findViewById(R.id.communityRecycler);
        communityRecycler.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        communityRecycler.setLayoutManager(layoutManager);
        adapter = new CommunityAdapter(communityList, this);
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
    }
}