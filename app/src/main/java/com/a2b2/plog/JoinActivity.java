package com.a2b2.plog;



import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.UUID;

public class JoinActivity extends AppCompatActivity {

    TextView idDuCheckTxt,nickDuCheckTxt,pwDuCheckTxt,fin,emailAuthTxt;
    EditText joinNick,joinId, joinEmail, joinPw, joinPwCheck;
    Button idDuCheckBtn,nickDu,join,emailAuthBtn;
    private Spinner spinner;
    private String email, inputText,nickname,pw,id,emailresult;

    private Handler handler;
    private UUID emailUUid, userUUid;
    private String url;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join);

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



        nickDu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nickDuCheckTxt.setVisibility(View.VISIBLE);
            }
        });

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.emailArray, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                // 아이템이 선택되었을 때의 동작을 여기에 정의합니다.
                TextView checkEmail = findViewById(R.id.emailCheck);
                checkEmail.setText(joinEmail.getText().toString()+"@"+parentView.getItemAtPosition(position).toString());
                email = joinEmail.getText().toString()+"@"+parentView.getItemAtPosition(position).toString();
               // email = checkEmail.toString();

            }
            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // 아무것도 선택되지 않았을 때의 동작을 여기에 정의합니다.
            }
        });
        emailAuthBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(emailAuthBtn.getText().equals("인 증 하 기")){
                    url = "서버 주소";
                    String data = "{ \"nickname\" : \""+nickname+"\",\"id\" : \""+id+"\",\"password\" : \""+pw+"\",\"email\" : \""+email+"\" }";

                    new Thread(() -> {
                    String result = httpPostBodyConnection(url, data);
                    // 처리 결과 확인
                    handler.post(() -> seeNetworkResult(result));
                    emailUUid = UUID.fromString(result);
                }).start();
                    emailAuthBtn.setText("인 증 완 료");
                } else {

                    url = "";
                    String data = "{\"emailUUID\" : \""+emailUUid+"\"}";
                    new Thread(() -> {
                        emailresult = httpPostBodyConnection(url, data);
                        // 처리 결과 확인
                        handler.post(() -> seeNetworkResult(emailresult));
                    }).start();
                    emailAuthTxt.setText(emailresult);

                }

            }
        });
        idDuCheckBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                idDuCheckTxt.setText("사용 가능한 ID입니다.");
                idDuCheckTxt.setVisibility(View.VISIBLE);
            }
        });
        if (joinPw.getText().toString().equals(joinPwCheck.getText().toString())){
            pwDuCheckTxt.setVisibility(View.VISIBLE);
            join.setEnabled(true);
        }
        else {
            pwDuCheckTxt.setText("비밀번호가 일치하지 않습니다.");
            pwDuCheckTxt.setVisibility(View.VISIBLE);
            //join.setEnabled(false);
        }

        //joinInfo = joinNick.getText().toString() + joinId.getText().toString() + joinEmail.getText().toString() + joinPw.getText().toString();

        join.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fin = findViewById(R.id.fin);
                fin.setText(joinNick.getText().toString() + joinId.getText().toString() + email.toString() + joinPw.getText().toString());

//                String url = "http://13.209.33.141:5000";
////                    //10자리 숫자/이메일/이름/프로필 사진 주소
//                inputText = joinNick.getText().toString() + joinId.getText().toString() + email.toString() + joinPw.getText().toString();
//
//                // Log.d("id값", "카카오ID " + user.getId().toString());
//                String data = "{ \"content\" : \""+inputText+"\" }";; //json 형식 데이터
//
//                new Thread(() -> {
//                    String result = httpPostBodyConnection(url, data);
//                    // 처리 결과 확인
//                    handler.post(() -> seeNetworkResult(result));
//                }).start();

                Intent intent = new Intent(JoinActivity.this, MainActivity.class);
                startActivity(intent);
                overridePendingTransition(3, 0);
                finish();
            }
        });

    }
    public String httpPostBodyConnection(String UrlData, String ParamData) {
        // 이전과 동일한 네트워크 연결 코드를 그대로 사용합니다.
        // 백그라운드 스레드에서 실행되기 때문에 메인 스레드에서는 문제가 없습니다.

        String totalUrl = "";
        totalUrl = UrlData.trim().toString();

        //http 통신을 하기위한 객체 선언 실시
        URL url = null;
        HttpURLConnection conn = null;

        //http 통신 요청 후 응답 받은 데이터를 담기 위한 변수
        String responseData = "";
        BufferedReader br = null;
        StringBuffer sb = null;

        //메소드 호출 결과값을 반환하기 위한 변수
        String returnData = "";


        try {
            //파라미터로 들어온 url을 사용해 connection 실시
            url = null;
            url = new URL(totalUrl);
            conn = null;
            conn = (HttpURLConnection) url.openConnection();

            //http 요청에 필요한 타입 정의 실시
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json; utf-8"); //post body json으로 던지기 위함
            conn.setRequestProperty("Accept", "application/json");
            conn.setDoOutput(true); //OutputStream을 사용해서 post body 데이터 전송
            try (OutputStream os = conn.getOutputStream()) {
                byte request_data[] = ParamData.getBytes("utf-8");
                Log.d("TAGGG",request_data.toString());
                os.write(request_data);
                //os.close();
            } catch (Exception e) {
                Log.d("TAG3","여기다");
                e.printStackTrace();
            }

            //http 요청 실시
            conn.connect();
            System.out.println("http 요청 방식 : " + "POST BODY JSON");
            System.out.println("http 요청 타입 : " + "application/json");
            System.out.println("http 요청 주소 : " + UrlData);
            System.out.println("http 요청 데이터 : " + ParamData);
            System.out.println("");

            //http 요청 후 응답 받은 데이터를 버퍼에 쌓는다
            br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
            sb = new StringBuffer();
            while ((responseData = br.readLine()) != null) {
                sb.append(responseData); //StringBuffer에 응답받은 데이터 순차적으로 저장 실시
            }

            //메소드 호출 완료 시 반환하는 변수에 버퍼 데이터 삽입 실시
            returnData = sb.toString();
            Log.d("TAG2", returnData);
            //http 요청 응답 코드 확인 실시
            String responseCode = String.valueOf(conn.getResponseCode());
            System.out.println("http 응답 코드 : " + responseCode);
            System.out.println("http 응답 데이터 : " + returnData);

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            //http 요청 및 응답 완료 후 BufferedReader를 닫아줍니다
            try {
                if (br != null) {
                    br.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return returnData; // 네트워크 요청 결과를 반환
    }
   /* public UUID getUserEmailUUID(String UrlData, String ParamData) {
        // 이전과 동일한 네트워크 연결 코드를 그대로 사용합니다.
        // 백그라운드 스레드에서 실행되기 때문에 메인 스레드에서는 문제가 없습니다.

        String totalUrl = "";
        totalUrl = UrlData.trim().toString();

        //http 통신을 하기위한 객체 선언 실시
        URL url = null;
        HttpURLConnection conn = null;

        //http 통신 요청 후 응답 받은 데이터를 담기 위한 변수
        UUID responseData = null;
        BufferedReader br = null;
        StringBuffer sb = null;

        //메소드 호출 결과값을 반환하기 위한 변수
        UUID returnData;


        try {
            //파라미터로 들어온 url을 사용해 connection 실시
            url = null;
            url = new URL(totalUrl);
            conn = null;
            conn = (HttpURLConnection) url.openConnection();

            //http 요청에 필요한 타입 정의 실시
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json; utf-8"); //post body json으로 던지기 위함
            conn.setRequestProperty("Accept", "application/json");
            conn.setDoOutput(true); //OutputStream을 사용해서 post body 데이터 전송
            try (OutputStream os = conn.getOutputStream()) {
                byte request_data[] = ParamData.getBytes("utf-8");
                Log.d("TAGGG",request_data.toString());
                os.write(request_data);
                //os.close();
            } catch (Exception e) {
                Log.d("TAG3","여기다");
                e.printStackTrace();
            }

            //http 요청 실시
            conn.connect();
            System.out.println("http 요청 방식 : " + "POST BODY JSON");
            System.out.println("http 요청 타입 : " + "application/json");
            System.out.println("http 요청 주소 : " + UrlData);
            System.out.println("http 요청 데이터 : " + ParamData);
            System.out.println("");

            //http 요청 후 응답 받은 데이터를 버퍼에 쌓는다
            br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
            sb = new StringBuffer();
            while ((responseData = br.readLine()) != null) {
                sb.append(responseData); //StringBuffer에 응답받은 데이터 순차적으로 저장 실시
            }

            //메소드 호출 완료 시 반환하는 변수에 버퍼 데이터 삽입 실시
            returnData = sb;
            Log.d("TAG2", returnData.toString());
            //http 요청 응답 코드 확인 실시
            String responseCode = String.valueOf(conn.getResponseCode());
            System.out.println("http 응답 코드 : " + responseCode);
            System.out.println("http 응답 데이터 : " + returnData);

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            //http 요청 및 응답 완료 후 BufferedReader를 닫아줍니다
            try {
                if (br != null) {
                    br.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return returnData; // 네트워크 요청 결과를 반환
    }*/
    public void seeNetworkResult(String result) {
        // 네트워크 작업 완료 후
        Log.d(result, "network");
    }
}