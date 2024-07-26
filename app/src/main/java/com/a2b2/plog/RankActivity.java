package com.a2b2.plog;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.RenderNode;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class RankActivity extends AppCompatActivity {
    private RecyclerView rankRecyclerView;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private List<RankItem> rankList;


    private ImageView community, home, mission, mypage,
            rank_first_frame, rank_sec_frame, rank_third_frame, first_userimg,sec_userimg,third_userimg;
    private TextView first_username,sec_username,third_username,first_score,sec_score,third_score;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rank);

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

        rankList = new ArrayList<>();
        rankList.add(new RankItem(4, "지구지킴이",936));
        rankList.add(new RankItem(5, "지구지킴이",873));
        rankList.add(new RankItem(6, "지구지킴이",450));
        rankList.add(new RankItem(7, "지구지킴이",450));

        rankRecyclerView = findViewById(R.id.rankRecyclerView);
        rankRecyclerView.setHasFixedSize(true);
        rankRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        rankRecyclerView.setLayoutManager(layoutManager);
        adapter = new RankAdapter(rankList);
        rankRecyclerView.setAdapter(adapter);



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
}