package com.a2b2.plog;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

//커뮤니티 게시글 작성 액티비티
public class CommunityPostActivity extends AppCompatActivity {

    private ImageView backBtn;
    private EditText title, ploggingPlace, meetingDate, meetingPlace, text;
    private Button enterBtn;
    private String f_title, f_ploggingPlace, f_meetingDate, f_meetingPlace, f_text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_community_post);

        backBtn = findViewById(R.id.backBtn);
        title = findViewById(R.id.title);
        ploggingPlace = findViewById(R.id.ploggingPlace);
        meetingDate = findViewById(R.id.meetingDate);
        meetingPlace = findViewById(R.id.meetingPlace);
        text = findViewById(R.id.text);
        enterBtn = findViewById(R.id.enterBtn);

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(CommunityPostActivity.this);
                builder.setMessage("게시글 작성을 종료하시겠습니까?")
                        .setPositiveButton("네", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Intent intent = new Intent(CommunityPostActivity.this, CommunityActivity.class);
                                startActivity(intent);
                                overridePendingTransition(0, 0);
                                dialog.dismiss();
                                finish();
                            }
                        })
                        .setNegativeButton("아니요", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // "아니요" 버튼 클릭 시 다이얼로그 닫기
                                dialog.dismiss();
                            }
                        });
                // 다이얼로그 보여주기
                AlertDialog alert = builder.create();
                alert.show();

            }
        });
        enterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });


    }
//    @SuppressLint("MissingSuperCall")
//    @Override
//    public void onBackPressed() {
//        new AlertDialog.Builder(this)
//                .setMessage("게시글 작성을 종료하시겠습니까?")
//                .setCancelable(false)
//                .setPositiveButton("네", new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int id) {
//                        dialog.dismiss();
//                        finish();
//                        // 다이얼로그가 닫힌 후 액티비티 종료
//
//                    }
//                })
//                .setNegativeButton("아니오", new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int id) {
//                        // 다이얼로그를 닫기만 하고 아무 작업도 하지 않음
//                        dialog.dismiss();
//                    }
//                })
//                .show();
//    }

}