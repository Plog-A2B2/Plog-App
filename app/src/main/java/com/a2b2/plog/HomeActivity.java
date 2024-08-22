package com.a2b2.plog;

import static androidx.core.content.ContentProviderCompat.requireContext;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class HomeActivity extends AppCompatActivity {


    private RecyclerView recyclerView;
    private PloggerAdapter ploggerAdapter;
    private List<RealtimePloggerItem> ploggingItems;
    private ImageView rank, community, mission, mypage;
    private SwitchCompat trashcanVisibleSwitch;
    boolean trashcanVisible = true;
    private Button setRoute;
    private EditText trashGoalEditText;
    int trashGoal = 0;
    private Button plggingStartBut;
    private SharedPreferencesHelper prefsHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        prefsHelper = new SharedPreferencesHelper(this);

        recyclerView = findViewById(R.id.ploggerRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        ploggingItems = new ArrayList<>();
        // 샘플 데이터 추가
        ploggingItems.add(new RealtimePloggerItem(R.drawable.badgetest, "nickname"));
        ploggingItems.add(new RealtimePloggerItem(R.drawable.badgetest, "nickname"));
        ploggingItems.add(new RealtimePloggerItem(R.drawable.badgetest, "nickname"));
        ploggingItems.add(new RealtimePloggerItem(R.drawable.badgetest, "nickname"));
        ploggingItems.add(new RealtimePloggerItem(R.drawable.badgetest, "nickname"));
        ploggingItems.add(new RealtimePloggerItem(R.drawable.badgetest, "nickname"));


        ploggerAdapter = new PloggerAdapter(ploggingItems);
        recyclerView.setAdapter(ploggerAdapter);

        trashcanVisibleSwitch = findViewById(R.id.trashcanVisibleSwitch);
        setRoute = findViewById(R.id.routeBtn);

        if (prefsHelper.getRoute() != null) {
            setRoute.setText(prefsHelper.getRoute().getOrigin());
        }

        trashcanVisibleSwitch.setChecked(true); //디폴트값: 쓰레기통 위치 보이기

        //쓰레기통 위치 보이게 하기로 하면 서버로 쓰레기통 위치 달라고 요청해야함 유유아이디, 현위치 위도 경도, 문의하는 내용

        // 스위치 상태 변경 리스너 설정
        trashcanVisibleSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            trashcanVisible = isChecked;
            //Log.d("trashcanVisible", "trashcan : " + trashcanVisible);
        });

        setRoute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, RouteListActivity.class);
                startActivity(intent);
                overridePendingTransition(0, 0);
                finish();
            }
        });

        plggingStartBut = findViewById(R.id.startBtn);

        plggingStartBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(HomeActivity.this, PloggingActivity.class);
                intent.putExtra("ploggingItems", new ArrayList<>(ploggingItems));
                intent.putExtra("trashcanVisible", trashcanVisible);

                startActivity(intent);
                overridePendingTransition(0, 0);
                finish();
            }
        });

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