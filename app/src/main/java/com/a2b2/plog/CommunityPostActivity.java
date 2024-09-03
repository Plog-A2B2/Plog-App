package com.a2b2.plog;



import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.UUID;

//커뮤니티 게시글 작성 액티비티
public class CommunityPostActivity extends AppCompatActivity {

    private ImageView backBtn;
    private EditText title, ploggingPlace, meetingDate, meetingPlace, text;
    private Button enterBtn;
    private String f_title, f_ploggingPlace, f_meetingPlace, f_text;
    private int f_meetingDate;
    private Handler handler;
    private SettingLocationDialog ploggingLocationDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_community_post);

        backBtn = findViewById(R.id.backBtn);
        title = findViewById(R.id.title);
        ploggingPlace = findViewById(R.id.ploggingPlace);
        meetingDate = findViewById(R.id.meetingDate);
//        meetingPlace = findViewById(R.id.meetingPlace);
        text = findViewById(R.id.text);
        enterBtn = findViewById(R.id.enterBtn);

        ploggingLocationDialog = new SettingLocationDialog(this);

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(CommunityPostActivity.this);
                builder.setMessage("게시글 작성을 종료하시겠습니까?")
                        .setPositiveButton("네", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Intent intent = new Intent(CommunityPostActivity.this, CommunityActivity.class);
                                startActivity(intent);
                                finish();
                                dialog.dismiss();

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

                f_title = title.getText().toString();
                f_ploggingPlace = ploggingPlace.getText().toString();
                f_meetingPlace = meetingPlace.getText().toString();
                f_text = text.getText().toString();
                f_meetingDate = Integer.parseInt(meetingDate.getText().toString());
                Log.d(f_title, f_title);
                Log.d(f_ploggingPlace, f_ploggingPlace);
                Log.d(f_meetingPlace, f_meetingPlace);
                Log.d(f_text, f_text);
                Log.d("f_meetingDate", String.valueOf(f_meetingDate));

                UUID uuid = UUID.fromString("8D841B8A-C15A-4657-95AC-AB28ED6F0190");
                String url = "http://15.164.152.246:8080/post/"+uuid+"/createpost";
                String data = "{\"title\" : \""+f_title+"\",\"plogPlace\" : \""+f_ploggingPlace+"\", \"meetPlace\" :  \""+f_ploggingPlace+"\" , \"content\" : \""+f_text+"\", \"schedule\" : \""+f_meetingDate+"\"}";
                new Thread(() -> {
                    String result = httpPostBodyConnection(url, data);
                    // 처리 결과 확인
                    handler.post(() -> {
                        seeNetworkResult(result);
                        try {
                            // JSON 문자열을 JSONObject로 변환
                            JSONObject jsonObject = new JSONObject(result);

                            // "userId" 키에 해당하는 값 추출
                            String message = jsonObject.getString("message");

                            // 추출한 userId 출력
                            System.out.println("받은 값" + message);
                            Toast.makeText(CommunityPostActivity.this,message+"완료",Toast.LENGTH_SHORT).show();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    });
                }).start();
            }
        });

    }
    @SuppressLint("MissingSuperCall")
    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setMessage("게시글 작성을 종료하시겠습니까?")
                .setCancelable(false)
                .setPositiveButton("네", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Intent intent = new Intent(CommunityPostActivity.this, CommunityActivity.class);
                        startActivity(intent);
                        finish();
                        dialog.dismiss();
                        // 다이얼로그가 닫힌 후 액티비티 종료

                    }
                })
                .setNegativeButton("아니오", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // 다이얼로그를 닫기만 하고 아무 작업도 하지 않음
                        dialog.dismiss();
                    }
                })
                .show();
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
    public void seeNetworkResult(String result) {
        // 네트워크 작업 완료 후
        Log.d(result, "network");
    }

}