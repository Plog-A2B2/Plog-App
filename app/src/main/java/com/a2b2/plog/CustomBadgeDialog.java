package com.a2b2.plog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Objects;
import java.util.UUID;

public class CustomBadgeDialog extends Dialog {

    private Button buyBtn, closeBtn;
    private Context mContext;
    private BadgeItem badgeItem;
    Handler handler;
    TextView unlockCondition;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.badge_unlock_condition);

        // 다이얼로그의 배경을 투명으로 만든다.
        Objects.requireNonNull(getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        handler = new Handler();
        // 커스텀 다이얼로그의 각 위젯들을 정의한다.
        buyBtn = findViewById(R.id.buyBtn);
        closeBtn = findViewById(R.id.closeBtn);
        unlockCondition = findViewById(R.id.unlockConditionText);

        unlockCondition.setText(badgeItem.getUnlockCondition());
        if (badgeItem.isMine()) {
            buyBtn.setText("구매 완료");
            buyBtn.setEnabled(false);
        } else {
            buyBtn.setText(badgeItem.getCost() + "코인으로 구매");
            buyBtn.setEnabled(true);
        }

        // 버튼 리스너 설정
        buyBtn.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                UUID uuid = UserManager.getInstance().getUserId();

                String url = "http://15.164.152.246:8080/badge/" + uuid + "/" + badgeItem.getBadgeId() + "/purchase";
                new Thread(() -> {
                    String result = httpPostBodyConnection(url, "");
                    // 처리 결과 확인
                    handler = new Handler(Looper.getMainLooper());
                    if (handler != null) {
                        handler.post(() -> {
                            try {
                                // JSON 응답을 파싱
                                JSONObject jsonObject = new JSONObject(result);

                                // "data" 필드에서 문자열 추출
                                String message = jsonObject.getString("data");

                                // 추출한 메시지를 토스트로 표시
                                Toast.makeText(mContext, message, Toast.LENGTH_LONG).show();
                                badgeItem.setMine(true);
                            } catch (JSONException e) {
                                e.printStackTrace();
                                // 파싱 중 에러 발생 시 에러 메시지를 토스트로 표시
                                Toast.makeText(mContext, "응답 처리 중 오류가 발생했습니다.", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }).start();

                dismiss();
            }
        });
        closeBtn.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {

                dismiss();
            }
        });

    }

    public CustomBadgeDialog(Context mContext, BadgeItem badgeItem) {
        super(mContext);
        this.mContext = mContext;
        this.badgeItem = badgeItem;
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


}