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
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
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
            rank_first_frame, rank_sec_frame, rank_third_frame, first_userimg,sec_userimg,third_userimg, helpBtn;
    private TextView first_username,sec_username,third_username,first_score,sec_score,third_score, helpTxt, myrankTxt;
    private ConstraintLayout background;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rank);

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
        myrankTxt = findViewById(R.id.myrankTxt);

        rankList = new ArrayList<>();
        rankList.add(new RankItem(4, "지구지킴이",936));
        rankList.add(new RankItem(5, "지구지킴이",873));
        rankList.add(new RankItem(6, "지구지킴이",450));
        rankList.add(new RankItem(7, "지구지킴이",450));
        rankList.add(new RankItem(7, "지구지킴이",450));
        rankList.add(new RankItem(7, "지구지킴이",450));
        rankList.add(new RankItem(7, "지구지킴이",450));
        rankList.add(new RankItem(7, "지구지킴이",450));

        rankRecyclerView = findViewById(R.id.rankRecyclerView);
        rankRecyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        rankRecyclerView.setLayoutManager(layoutManager);
        adapter = new RankAdapter(rankList);
        rankRecyclerView.setAdapter(adapter);

        helpBtn = findViewById(R.id.helpBtn);
        helpTxt = findViewById(R.id.helpTxt);

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
}