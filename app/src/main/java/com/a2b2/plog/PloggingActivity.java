package com.a2b2.plog;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

public class PloggingActivity extends AppCompatActivity {

    private ImageView backBtn;
    private RecyclerView recyclerView;
    private PloggerAdapter ploggerAdapter;
    private List<RealtimePloggerItem> ploggingItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plogging);

        recyclerView = findViewById(R.id.ploggerRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        // 이전 액티비티에서 전달된 데이터 받기
        Intent intent = getIntent();
        ArrayList<RealtimePloggerItem> ploggingItems = (ArrayList<RealtimePloggerItem>) intent.getSerializableExtra("ploggingItems");


        ploggerAdapter = new PloggerAdapter(ploggingItems);
        recyclerView.setAdapter(ploggerAdapter);

        backBtn = findViewById(R.id.backBtn);

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PloggingActivity.this, HomeActivity.class);
                startActivity(intent);
                overridePendingTransition(0, 0);
                finish();
            }
        });
    }
}