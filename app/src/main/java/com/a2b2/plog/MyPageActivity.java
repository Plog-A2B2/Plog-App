package com.a2b2.plog;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class MyPageActivity extends AppCompatActivity {

    private ImageView rank, community, mission, home;
    private int n=0;
    private android.widget.Button  myCommunityBtn;

    private SettingLocationDialog settingLocationDialog;
    private TextView standardLocationTextView;
    private SwitchCompat pushAlarmSwitch;
    boolean isPushAlarmOn = false; // 서버에서 알림 허용 여부 받아와서 저장

    private Place location, standardLocation;
    private Button saveBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_page);

        settingLocationDialog = new SettingLocationDialog(this);

        standardLocationTextView = findViewById(R.id.standardLocationTextView);
        pushAlarmSwitch =findViewById(R.id.pushAlarmSwitch);
        saveBtn = findViewById(R.id.saveBtn);

        pushAlarmSwitch.setChecked(false);
        saveBtn.setVisibility(View.GONE);

        if (isPushAlarmOn == true) {
            standardLocationTextView.setVisibility(View.VISIBLE);
            settingLocationDialog.show();
        } else {
            standardLocationTextView.setVisibility(View.GONE);
        }

        // 스위치 상태 변경 리스너 설정
        pushAlarmSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            isPushAlarmOn = isChecked;
            if (isPushAlarmOn == true) {
                standardLocationTextView.setVisibility(View.VISIBLE);
                settingLocationDialog.show();
            } else {
                standardLocationTextView.setVisibility(View.GONE);
            }
            saveBtn.setVisibility(View.VISIBLE);
        });

        settingLocationDialog.setOnItemClickListener(new SettingLocationDialog.OnItemClickListener() {
            @Override
            public void onItemClick(Place document) {
                if (document != null) {
                    location = document;
                    standardLocation = new Place(location.getPlaceName(), location.getAddress(), 0, 0);
                    standardLocationTextView.setText("기준 위치: " + document.getPlaceName());
                }
            }
        });

        standardLocationTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isPushAlarmOn == true) {
                    standardLocationTextView.setVisibility(View.VISIBLE);
                    settingLocationDialog.show();
                } else {
                    standardLocationTextView.setVisibility(View.GONE);
                }
                saveBtn.setVisibility(View.VISIBLE);
            }
        });

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("setting", "saved : " + isPushAlarmOn);

                // 서버에 알림 허용여부 업데이트된 내용 저장(on/off, 기준위치)

                saveBtn.setVisibility(View.GONE);
            }
        });

        rank = findViewById(R.id.rank);
        community = findViewById(R.id.community);
        home = findViewById(R.id.home);
        mission = findViewById(R.id.mission);

        rank.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MyPageActivity.this, RankActivity.class);
                startActivity(intent);
                overridePendingTransition(0, 0);
                finish();
            }
        });

        community.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MyPageActivity.this, CommunityActivity.class);
                startActivity(intent);
                overridePendingTransition(0, 0);
                finish();
            }
        });
        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MyPageActivity.this, HomeActivity.class);
                startActivity(intent);
                overridePendingTransition(0, 0);
                finish();
            }
        });
        mission.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MyPageActivity.this, MissionActivity.class);
                startActivity(intent);
                overridePendingTransition(0, 0);
                finish();
            }
        });
        myCommunityBtn = findViewById(R.id.myCommunityTaskBtn);
        myCommunityBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MyPageActivity.this, MyCommunityActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}