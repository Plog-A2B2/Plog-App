package com.a2b2.plog;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.UUID;

public class JoinActivity extends AppCompatActivity {

    TextView idDuCheckTxt, nickDuCheckTxt, pwDuCheckTxt, fin, emailAuthTxt;
    EditText joinNick, joinId, joinEmail, joinPw, joinPwCheck;
    Button idDuCheckBtn, nickDu, join, emailAuthBtn;
    private Spinner spinner;
    private String email, account, emailresult;
    private Handler handler;
    private UUID emailUUid;
    private String url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join);
        handler = new Handler();

        joinEmail = findViewById(R.id.joinEmail);
        spinner = findViewById(R.id.spinner);
        emailAuthTxt = findViewById(R.id.emailAuth);
        emailAuthBtn = findViewById(R.id.emailAuthBtn);
        joinNick = findViewById(R.id.joinNick);
        nickDu = findViewById(R.id.nickDu);
        joinId = findViewById(R.id.joinId);
        nickDuCheckTxt = findViewById(R.id.nickDuCheckTxt);
        joinPw = findViewById(R.id.joinPw);
        joinPwCheck = findViewById(R.id.joinPwCheck);
        pwDuCheckTxt = findViewById(R.id.pwDuCheckTxt);
        idDuCheckTxt = findViewById(R.id.idDuCheckTxt);
        idDuCheckBtn = findViewById(R.id.idDuCheckBtn);
        join = findViewById(R.id.join);
        fin = findViewById(R.id.fin);
        ImageView back = findViewById(R.id.backBtn);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(JoinActivity.this, MainActivity.class);
                startActivity(intent);
                overridePendingTransition(2, 0);
                finish();
            }
        });

        // 닉네임 중복 확인 버튼 클릭 이벤트
        nickDu.setOnClickListener(v -> nickDuCheckTxt.setVisibility(View.VISIBLE));

        // 이메일 스피너 설정
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.emailArray, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                email = joinEmail.getText().toString() + "@" + parentView.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // 아무것도 선택되지 않았을 때의 동작
            }
        });

        // 이메일 인증 버튼 클릭 이벤트
        emailAuthBtn.setOnClickListener(v -> {
            if (emailAuthBtn.getText().equals("인 증 하 기")) {
                url = "http://15.164.152.246:8080/user/signup";

                String data = "{ \"account\" : \"" + joinId.getText().toString() + "\","
                        + "\"password\" : \"" + joinPw.getText().toString() + "\","
                        + "\"nickname\" : \"" + joinNick.getText().toString() + "\","
                        + "\"email\" : \"" + email + "\" }";

                Log.d("emailAuth", email);
                Log.d("emailAuth", joinId.getText().toString());

                new Thread(() -> {
                    try {
                        String result = httpPostBodyConnection(url, data);
                        handler.post(() -> handleSignUpResponse(result));
                        Log.d("emailAuth", "try");
                    } catch (Exception e) {
                        Log.e("SignUpError", "Error during sign-up: " + e.getMessage());
                        handler.post(() -> emailAuthTxt.setText("Sign-up failed: " + e.getMessage()));
                    }
                }).start();


                emailAuthBtn.setText("인 증 완 료");
                emailAuthBtn.setBackgroundColor(getResources().getColor(R.color.joinBack));
            } else {
                url = "http://15.164.152.246:8080/user/confirm-email?uuid="+emailUUid;  // 실제 인증 URL로 변경 필요
                String data = "{\"account\" : \"" + account + "\"}";

                new Thread(() -> {
                    emailresult = httpPostBodyConnection(url, data);
                    handler.post(() -> emailAuthTxt.setText(emailresult));
                }).start();
            }
        });

        // ID 중복 확인 버튼 클릭 이벤트
        idDuCheckBtn.setOnClickListener(v -> {
            idDuCheckTxt.setText("사용 가능한 ID입니다.");
            idDuCheckTxt.setVisibility(View.VISIBLE);
        });
        // 비밀번호 입력 감지 리스너 추가
        TextWatcher pwTextWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String pw = joinPw.getText().toString();
                String pwCheck = joinPwCheck.getText().toString();

                if (pw.isEmpty() || pwCheck.isEmpty()) {
                    pwDuCheckTxt.setVisibility(View.GONE); // 둘 다 비어있으면 숨김
                    join.setEnabled(false);
                } else {
                    pwDuCheckTxt.setVisibility(View.VISIBLE); // 입력하면 보이게 함
                    if (pw.equals(pwCheck)) {
                        pwDuCheckTxt.setVisibility(View.VISIBLE);
                        join.setEnabled(true);
                    } else {
                        pwDuCheckTxt.setText("비밀번호가 일치하지 않습니다.");
                        join.setEnabled(false);
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        };
        //EditText에 TextWatcher 등록
        joinPw.addTextChangedListener(pwTextWatcher);
        joinPwCheck.addTextChangedListener(pwTextWatcher);

        // 회원가입 버튼 클릭 이벤트
        join.setOnClickListener(v -> {
            fin.setText(joinNick.getText().toString() + joinId.getText().toString() + email + joinPw.getText().toString());
            Intent intent = new Intent(JoinActivity.this, MainActivity.class);
            startActivity(intent);
            overridePendingTransition(3, 0);
            finish();
        });
    }

    private void handleSignUpResponse(String result) {
        try {
            Log.d("handleSignUpResponse", "handleSignUpResponse executed");
            JsonObject jsonObject = JsonParser.parseString(result).getAsJsonObject();

            // "message" 추출
            String message = jsonObject.get("message").getAsString();

            // "data" 객체 추출
            JsonObject dataObject = jsonObject.getAsJsonObject("data");

            // "account"와 "uuid" 추출
            account = dataObject.get("account").getAsString();
            String userUUIDStr = dataObject.get("uuid").getAsString();
            emailUUid = UUID.fromString(userUUIDStr);

            emailAuthTxt.setText("회원가입 성공");
            Log.d("JoinActivity", "회원가입 성공: " + message);
        } catch (Exception e) {
            Log.e("JoinActivity", "JSON 파싱 오류 또는 네트워크 오류: " + e.getMessage());
        }
    }


    public String httpPostBodyConnection(String UrlData, String ParamData) {
        String responseData = "";
        BufferedReader br = null;

        try {
            URL url = new URL(UrlData);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json; utf-8");
            conn.setRequestProperty("Accept", "application/json");
            conn.setDoOutput(true);

            try (OutputStream os = conn.getOutputStream()) {
                byte[] request_data = ParamData.getBytes("utf-8");
                os.write(request_data);
            }

            conn.connect();

            br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
            StringBuilder sb = new StringBuilder();
            while ((responseData = br.readLine()) != null) {
                sb.append(responseData);
            }

            return sb.toString();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (br != null) {
                    br.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return responseData;
    }

    public void seeNetworkResult(String result) {
        Log.d("NetworkResult", result);
    }
}
