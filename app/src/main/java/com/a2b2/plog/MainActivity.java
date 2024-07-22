package com.a2b2.plog;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity {

    Button loginBtn, idFindBtn, pwFindBtn, joinBtn;
    ImageView kakaoLogin;
    EditText id, pw;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        loginBtn = findViewById(R.id.loginBtn);
        idFindBtn = findViewById(R.id.idFindBtn);
        pwFindBtn = findViewById(R.id.pwFindBtn);
        joinBtn = findViewById(R.id.joinBtn);
        kakaoLogin =findViewById(R.id.kakaoLogin);

        id = findViewById(R.id.idTxt);
        pw = findViewById(R.id.pwTxt);


        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                startActivity(intent);
                overridePendingTransition(0, 0);
                finish();

            }
        });
    }
}