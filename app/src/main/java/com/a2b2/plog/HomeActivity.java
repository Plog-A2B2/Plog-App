package com.a2b2.plog;

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

        trashcanVisibleSwitch = findViewById(R.id.trashcanVisibleSwitch);
        setRoute = findViewById(R.id.routeBut);
        trashGoalEditText = findViewById(R.id.trashGoalEditText);

        trashcanVisibleSwitch.setChecked(true); //디폴트값: 쓰레기통 위치 보이기

        // 스위치 상태 변경 리스너 설정
        trashcanVisibleSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            trashcanVisible = isChecked;
            Log.d("trashcanVisible", "trashcan : " + trashcanVisible);
        });

        setRoute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, RouteActivity.class);
                startActivity(intent);
                overridePendingTransition(0, 0);
                finish();
            }
        });

        plggingStartBut = findViewById(R.id.startBtn);

        plggingStartBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setTrashGoal();
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


    void setTrashGoal() {
        String numberInput = trashGoalEditText.getText().toString();

        if (!numberInput.isEmpty()){
            trashGoal = Integer.parseInt(numberInput);
        }
    }
}