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

public class HomeActivity extends AppCompatActivity {


    private RecyclerView recyclerView;
    private PloggerAdapter ploggerAdapter;
    private List<RealtimePloggerItem> ploggingItems;
    private ImageView rank, community, mission, mypage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        recyclerView = findViewById(R.id.ploggerRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        ploggingItems = new ArrayList<>();
        // 샘플 데이터 추가
        ploggingItems.add(new RealtimePloggerItem(R.drawable.ic_runner, "5 km", "00:30"));
        ploggingItems.add(new RealtimePloggerItem(R.drawable.ic_runner, "3 km", "01:26"));
        ploggingItems.add(new RealtimePloggerItem(R.drawable.ic_runner, "7 km", "02:07"));
        ploggingItems.add(new RealtimePloggerItem(R.drawable.ic_runner, "7 km", "02:07"));
        ploggingItems.add(new RealtimePloggerItem(R.drawable.ic_runner, "7 km", "02:07"));
        ploggingItems.add(new RealtimePloggerItem(R.drawable.ic_runner, "7 km", "02:07"));
        ploggingItems.add(new RealtimePloggerItem(R.drawable.ic_runner, "7 km", "02:07"));
        ploggingItems.add(new RealtimePloggerItem(R.drawable.ic_runner, "7 km", "02:07"));
        ploggingItems.add(new RealtimePloggerItem(R.drawable.ic_runner, "7 km", "02:07"));
        ploggingItems.add(new RealtimePloggerItem(R.drawable.ic_runner, "7 km", "02:07"));
        ploggingItems.add(new RealtimePloggerItem(R.drawable.ic_runner, "7 km", "02:07"));

        ploggerAdapter = new PloggerAdapter(ploggingItems);
        recyclerView.setAdapter(ploggerAdapter);


        rank = findViewById(R.id.rank);
        community = findViewById(R.id.community);
        mission = findViewById(R.id.mission);
        mypage = findViewById(R.id.mypage);

        rank.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, RankActivity.class);
                startActivity(intent);
                overridePendingTransition(0, 0);
                finish();
            }
        });

        community.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, CommunityActivity.class);
                startActivity(intent);
                overridePendingTransition(0, 0);
                finish();
            }
        });
        mission.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, MissionActivity.class);
                startActivity(intent);
                overridePendingTransition(0, 0);
                finish();
            }
        });
        mypage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, MyPageActivity.class);
                startActivity(intent);
                overridePendingTransition(0, 0);
                finish();
            }
        });
    }
}