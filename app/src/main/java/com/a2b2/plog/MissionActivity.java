package com.a2b2.plog;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

public class MissionActivity extends AppCompatActivity {

    private RecyclerView questRecyclerView;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private List<QuestItem> questList;
    private ImageView rank, community, home, mypage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mission);

        questList = new ArrayList<>();
        // 여기에 퀘스트 아이템을 추가합니다.
        questList.add(new QuestItem("플로깅 3KM 하기", 3));
        questList.add(new QuestItem("플라스틱 10개 줍기", 2));
        questList.add(new QuestItem("그룹 플로깅 1회 하기", 5));
        questList.add(new QuestItem("그룹 플로깅 1회 하기", 5));
        questList.add(new QuestItem("그룹 플로깅 1회 하기", 5));
        questList.add(new QuestItem("그룹 플로깅 1회 하기", 5));

        questRecyclerView = findViewById(R.id.questRecyclerView);
        questRecyclerView.setHasFixedSize(true);
        layoutManager = new GridLayoutManager(this, 3, GridLayoutManager.HORIZONTAL, false);
        questRecyclerView.setLayoutManager(layoutManager);
        adapter = new QuestAdapter(questList);
        questRecyclerView.setAdapter(adapter);

        // PagerSnapHelper를 사용하여 페이지 넘기기 기능을 추가합니다.
        PagerSnapHelper snapHelper = new PagerSnapHelper();
        snapHelper.attachToRecyclerView(questRecyclerView);

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
}