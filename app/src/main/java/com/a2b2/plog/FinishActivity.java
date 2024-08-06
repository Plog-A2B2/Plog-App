package com.a2b2.plog;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class FinishActivity extends AppCompatActivity {
    HashMap<String, Integer> trashCountMap = new HashMap<>();

    private ImageView downloadBtn, nextBtn, routeCreateBtn;
    private TextView todayDateTextView, totalTrashAmountTextView;
    private int totalTrashAmount = 0;
    private TextView tvFinalTime, tvFinalDistance;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_finish);

        Intent intent = getIntent();
        trashCountMap = (HashMap<String, Integer>) intent.getSerializableExtra("trashCountMap");

        for (String trashType : trashCountMap.keySet()) {
            int count = trashCountMap.get(trashType);
            Log.d("PloggingActivity", trashType + ": " + count);
        }
        tvFinalTime = findViewById(R.id.timeTextView);
        tvFinalDistance = findViewById(R.id.distanceTextView);

        String time = getIntent().getStringExtra("time");
        String distance = getIntent().getStringExtra("distance");

        tvFinalTime.setText(time);
        tvFinalDistance.setText(distance);

        todayDateTextView = findViewById(R.id.todayDateTextView);
        totalTrashAmountTextView = findViewById(R.id.totalTrashAmount);
        // 현재 날짜를 yyyy.MM.dd(E) 형식으로 설정
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd(E)", Locale.getDefault());
        String currentDate = dateFormat.format(new Date());
        todayDateTextView.setText(currentDate);


        downloadBtn = findViewById(R.id.downloadBtn);
        nextBtn = findViewById(R.id.nextBtn);
        routeCreateBtn = findViewById(R.id.routeBtn);

        LinearLayout trashContainer1 = findViewById(R.id.trashContainer1);
        LinearLayout trashContainer2 = findViewById(R.id.trashContainer2);
        String[] trashTypes1 = {"일반쓰레기", "플라스틱", "종이류"};
        String[] trashTypes2 = {"캔/고철류", "병류", "비닐류"};
        LayoutInflater inflater = LayoutInflater.from(this);

        for (String trashType : trashTypes1) {
            View itemView = inflater.inflate(R.layout.item_trash_finish, trashContainer1, false);
            TextView tvTrashType = itemView.findViewById(R.id.trashType);
            tvTrashType.setText(trashType);

            TextView tvTrashAmount = itemView.findViewById(R.id.trashAmount);

            int count = trashCountMap.get(trashType);
            Log.d("finishActivity", trashType + ": " + count);
            tvTrashAmount.setText(count + "개");
            totalTrashAmount += count;

            trashContainer1.addView(itemView);
        }
        for (String trashType : trashTypes2) {
            View itemView = inflater.inflate(R.layout.item_trash_finish, trashContainer2, false);
            TextView tvTrashType = itemView.findViewById(R.id.trashType);
            tvTrashType.setText(trashType);

            TextView tvTrashAmount = itemView.findViewById(R.id.trashAmount);

            int count = trashCountMap.get(trashType);
            Log.d("finishActivity", trashType + ": " + count);
            tvTrashAmount.setText(count + "개");
            totalTrashAmount += count;

            trashContainer2.addView(itemView);
        }
        totalTrashAmountTextView.setText("Total : " + totalTrashAmount + "개");

//        // trashCountMap을 사용하여 필요한 작업을 수행
//        if (trashCountMap != null) {
//            for (String key : trashCountMap.keySet()) {
//                Integer count = trashCountMap.get(key);
//                // count를 사용하여 필요한 작업 수행
//            }
//        }

        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 분리수거 확인 화면으로 이동
                Intent intent = new Intent(FinishActivity.this, RecyclingInfoActivity.class);
                startActivity(intent);
                overridePendingTransition(0, 0);
                finish();
            }
        });


    }
}