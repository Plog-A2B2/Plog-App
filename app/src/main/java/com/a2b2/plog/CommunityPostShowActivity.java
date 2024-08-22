package com.a2b2.plog;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

//커뮤니티 게시글 상세조회 액티비티
public class CommunityPostShowActivity extends AppCompatActivity {

    private ImageView backBtn,badge;
    private TextView nickname, date, title, ploggingPlace, meetingDate, meetingPlace, posting, deleteBtn;
    private Button likeBtn,joinBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_community_post_show);

        backBtn = findViewById(R.id.backBtn);
        badge = findViewById(R.id.badge);
        nickname = findViewById(R.id.nickname);
        date = findViewById(R.id.date);
        title = findViewById(R.id.title);
        ploggingPlace = findViewById(R.id.ploggingPlace);
        meetingDate = findViewById(R.id.meetingDate);
        meetingPlace = findViewById(R.id.meetingPlace);
        posting = findViewById(R.id.posting);
        deleteBtn = findViewById(R.id.deleteBtn);
        likeBtn = findViewById(R.id.likeBtn);
        joinBtn = findViewById(R.id.joinBtn);

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CommunityPostShowActivity.this, CommunityActivity.class);
                startActivity(intent);
                overridePendingTransition(0, 0);
                finish();
            }
        });
        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 팝업 다이얼로그 생성
                AlertDialog.Builder builder = new AlertDialog.Builder(CommunityPostShowActivity.this);
                builder.setMessage("게시글을 삭제하시겠습니까?")
                        .setPositiveButton("네", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // "네" 버튼 클릭 시 알림창 표시
                                Toast.makeText(CommunityPostShowActivity.this, "삭제완료", Toast.LENGTH_SHORT).show();
                                //게시글 삭제하는 통신

                                // 다이얼로그 닫기
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

        likeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(),"찜 완료!", Toast.LENGTH_SHORT).show();
                //서버랑 통신
            }
        });
        joinBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(),"참가 완료!", Toast.LENGTH_SHORT).show();
                //서버랑 통신
            }
        });
    }
}