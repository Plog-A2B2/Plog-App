package com.a2b2.plog;


import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.UUID;

//커뮤니티 게시글 상세조회 액티비티
public class CommunityPostShowActivity extends AppCompatActivity {

    private ImageView backBtn,badge;
    private TextView nickname, date, postTitle, ploggingPlace, meetingDate, meetingPlace, posting, deleteBtn;
    private Button likeBtn,joinBtn;
    private boolean isLikeCheck =false;
    private boolean isJoinCheck =false;
    private Handler handler;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_community_post_show);

        backBtn = findViewById(R.id.backBtn);
        badge = findViewById(R.id.badge);
        nickname = findViewById(R.id.nickname);
        date = findViewById(R.id.date);
        postTitle = findViewById(R.id.title);
        ploggingPlace = findViewById(R.id.ploggingPlace);
        meetingDate = findViewById(R.id.meetingDate);
        meetingPlace = findViewById(R.id.meetingPlace);
        posting = findViewById(R.id.posting);
        deleteBtn = findViewById(R.id.deleteBtn);
        likeBtn = findViewById(R.id.likeBtn);
        joinBtn = findViewById(R.id.joinBtn);

        Button testBtn = findViewById(R.id.testBtn);
        Log.d("라이크 기본 상태", String.valueOf(isLikeCheck));
        testBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isLikeCheck = true;
                Log.d("라이크 상태 확인", String.valueOf(isLikeCheck));
                updateLikeButton();
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        updateLikeButton();  // UI 업데이트는 메인 스레드에서 실행
//                    }
//                });
            }
        });
        Button test2 = findViewById(R.id.testBtn2);
        test2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isLikeCheck = false;
                Log.d("false 떠야 함", String.valueOf(isLikeCheck));
                updateLikeButton();
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        updateLikeButton();  // UI 업데이트는 메인 스레드에서 실행
//                    }
//                });
            }
        });
        int postId =getIntent().getIntExtra("postId", 0);;

        UUID uuid = UserManager.getInstance().getUserId();
        Log.d("uuid", String.valueOf(uuid));
        Log.d("postId", String.valueOf(postId));
        // GET 요청을 위한 ParamData 제거
        String url = "http://15.164.152.246:8080/post/"+uuid+"/"+postId+"/post_detail";
        new Thread(() -> {
            String result = httpGetConnection(url, "");
            // 처리 결과 확인
            handler = new Handler(Looper.getMainLooper());
            if (handler != null) {
                handler.post(() -> {
                    if(result != null && !result.isEmpty()) {
                        parseCommunityDetail(result);
                    } else {
                        Log.e("Error", "Result is null or empty");
                    }
                    seeNetworkResult(result);
                });
            }
        }).start();


        likeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isLikeCheck = !isLikeCheck;
                updateLikeButton();
                String likeState = likeBtn.getText().toString();
                Log.d("likeState", likeState);

                if(likeState.equals("찜하기")){
                    int postId =getIntent().getIntExtra("postId", 0);;

                    UUID uuid = UserManager.getInstance().getUserId();
                    Log.d("uuid", String.valueOf(uuid));
                    Log.d("postId", String.valueOf(postId));
                    // GET 요청을 위한 ParamData 제거
                    String url = "http://15.164.152.246:8080/post/"+uuid+"/"+postId+"/unlike";
                    new Thread(() -> {
                        // DELETE 요청을 수행
                        String result = httpDeleteConnection(url, "");
                        // 처리 결과 확인
                        handler = new Handler(Looper.getMainLooper());
                        if (handler != null) {
                            handler.post(() -> {
                                seeNetworkResult(result);
                            });
                        }
                    }).start();
                } else{
                    Toast.makeText(getApplicationContext(),"찜 완료!", Toast.LENGTH_SHORT).show();
                    int postId =getIntent().getIntExtra("postId", 0);;

                    UUID uuid = UserManager.getInstance().getUserId();
                    Log.d("uuid", String.valueOf(uuid));
                    Log.d("postId", String.valueOf(postId));
                    // GET 요청을 위한 ParamData 제거
                    String url = "http://15.164.152.246:8080/post/"+uuid+"/"+postId+"/like";
                    new Thread(() -> {
                        // DELETE 요청을 수행
                        String result = httpPostBodyConnection(url, "");
                        // 처리 결과 확인
                        handler = new Handler(Looper.getMainLooper());
                        if (handler != null) {
                            handler.post(() -> {
                                seeNetworkResult(result);
                            });
                        }
                    }).start();
                }
                //서버랑 통신
            }
        });
        joinBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isJoinCheck = !isJoinCheck;
                updateJoinButton();
                String joinState = joinBtn.getText().toString();

                if(joinState.equals("참여하기")){
                    int postId =getIntent().getIntExtra("postId", 0);;

                    UUID uuid = UserManager.getInstance().getUserId();
                    Log.d("uuid", String.valueOf(uuid));
                    Log.d("postId", String.valueOf(postId));
                    // GET 요청을 위한 ParamData 제거
                    String url = "http://15.164.152.246:8080/post/"+uuid+"/"+postId+"/canceljoin";
                    new Thread(() -> {
                        // DELETE 요청을 수행
                        String result = httpDeleteConnection(url, "");
                        // 처리 결과 확인
                        handler = new Handler(Looper.getMainLooper());
                        if (handler != null) {
                            handler.post(() -> {
                                seeNetworkResult(result);
                            });
                        }
                    }).start();
                } else{
                    Toast.makeText(getApplicationContext(),"참여완료!", Toast.LENGTH_SHORT).show();
                    int postId =getIntent().getIntExtra("postId", 0);;

                    UUID uuid = UserManager.getInstance().getUserId();
                    Log.d("uuid", String.valueOf(uuid));
                    Log.d("postId", String.valueOf(postId));
                    // GET 요청을 위한 ParamData 제거
                    String url = "http://15.164.152.246:8080/post/"+uuid+"/"+postId+"/join";
                    new Thread(() -> {
                        // DELETE 요청을 수행
                        String result = httpPostBodyConnection(url, "");
                        // 처리 결과 확인
                        handler = new Handler(Looper.getMainLooper());
                        if (handler != null) {
                            handler.post(() -> {
                                seeNetworkResult(result);
                            });
                        }
                    }).start();
                }
                //서버랑 통신
            }
        });


        //삭제하는 버튼 클릭 시 지우는 통신 코드 추가하기

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
                                int postId =getIntent().getIntExtra("postId", 0);;

                                UUID uuid = UserManager.getInstance().getUserId();
                                Log.d("uuid", String.valueOf(uuid));
                                Log.d("postId", String.valueOf(postId));
                                // GET 요청을 위한 ParamData 제거
                                String url = "http://15.164.152.246:8080/post/"+uuid+"/"+postId+"/deletepost";
                                new Thread(() -> {
                                    // DELETE 요청을 수행
                                    String result = httpDeleteConnection(url, "");
                                    // 처리 결과 확인
                                    handler = new Handler(Looper.getMainLooper());
                                    if (handler != null) {
                                        handler.post(() -> {
                                            seeNetworkResult(result);
                                        });
                                    }
                                }).start();

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
    }
    private void parseCommunityDetail(String json) {
        try {
            // 전체 JSON 데이터는 JSONObject로 파싱
            JSONObject jsonObject = new JSONObject(json);

            // JSONObject에서 "data" 필드를 JSONObject로 추출
            JSONObject postObject = jsonObject.getJSONObject("data");

            Log.d("PostObject", postObject.toString()); // postObject의 내용을 로그로 출력

            // 데이터 추출
            String userNickname = postObject.optString("userNickname", "");
            String time = postObject.optString("time", "");
            String title = postObject.optString("title", "");
            String plogPlace = postObject.optString("plogPlace", "");
            String meetPlace = postObject.optString("meetPlace", "");
            String content = postObject.optString("content", "");
            String schedule = postObject.optString("schedule", "");
            boolean joined = postObject.optBoolean("joined", false);
            boolean liked = postObject.optBoolean("liked", false);

            // UI 업데이트
            runOnUiThread(() -> {
                nickname.setText(userNickname);
                date.setText(time);
                postTitle.setText(title);
                ploggingPlace.setText(plogPlace);
                meetingDate.setText(schedule);
                meetingPlace.setText(meetPlace);
                posting.setText(content);
                isLikeCheck = liked;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        joinBtn.setText(joined ? "참여완료" : "참여하기");
                        joinBtn.setBackgroundResource(joined ? R.drawable.joined : R.drawable.rounded_blue_square);

                        likeBtn.setText(liked ? "찜완료" : "찜하기");
                        likeBtn.setBackgroundResource(liked ? R.drawable.liked : R.drawable.rounded_puple_square);
                    }
                });

//                joinBtn.setText(joined ? "참여완료" : "참여하기");
//                joinBtn.setBackgroundResource(joined ? R.drawable.joined : R.drawable.rounded_blue_square);
//
//                likeBtn.setText(liked ? "찜완료" : "찜하기");
//                likeBtn.setBackgroundResource(liked ? R.drawable.liked : R.drawable.rounded_puple_square);
            });

        } catch (JSONException e) {
            Log.e("JSONError", "JSONException: " + e.getMessage());
        }
    }

    public String httpGetConnection(String UrlData, String s) {
        String totalUrl = UrlData.trim();

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
            url = new URL(totalUrl);
            conn = (HttpURLConnection) url.openConnection();

            //http 요청에 필요한 타입 정의 실시
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json; utf-8");

            //http 요청 실시
            conn.connect();
            System.out.println("http 요청 방식 : " + "GET");
            System.out.println("http 요청 주소 : " + totalUrl);
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
        if (isLikeCheck) {
            likeBtn.setText("찜완료");
            likeBtn.setBackgroundResource(R.drawable.liked);
        } else {
            likeBtn.setText("찜하기");
            likeBtn.setBackgroundResource(R.drawable.rounded_puple_square);
        }
        // 네트워크 작업 완료 후
        Log.d(result, "network");
    }
    private void updateLikeButton() {
        Log.d("라이크체크", String.valueOf(isLikeCheck));
        if (isLikeCheck) {
            likeBtn.setText("찜완료");
            likeBtn.setBackgroundResource(R.drawable.liked);
        } else {
            likeBtn.setText("찜하기");
            likeBtn.setBackgroundResource(R.drawable.rounded_puple_square);
        }
    }
    private void updateJoinButton() {
        Log.d("조인체크", String.valueOf(isJoinCheck));
        if (isJoinCheck) {
            joinBtn.setText("참여완료");
            joinBtn.setBackgroundResource(R.drawable.joined);
        } else {
            joinBtn.setText("참여하기");
            joinBtn.setBackgroundResource(R.drawable.rounded_blue_square);
        }
    }
    public String httpDeleteConnection(String UrlData, String s) {
        String totalUrl = UrlData.trim();

        // HTTP 통신 객체 선언
        URL url = null;
        HttpURLConnection conn = null;

        // 응답 받은 데이터를 담을 변수
        String responseData = "";
        BufferedReader br = null;
        StringBuffer sb = null;

        // 메소드 호출 결과값을 반환할 변수
        String returnData = "";

        try {
            // URL 객체 생성
            url = new URL(totalUrl);
            conn = (HttpURLConnection) url.openConnection();

            // 요청 메소드를 DELETE로 설정
            conn.setRequestMethod("DELETE");
            conn.setRequestProperty("Accept", "application/json; utf-8");

            // HTTP 요청 실시
            conn.connect();
            System.out.println("HTTP 요청 방식 : DELETE");
            System.out.println("HTTP 요청 주소 : " + totalUrl);
            System.out.println("");

            // 응답 받은 데이터를 버퍼에 쌓기
            br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
            sb = new StringBuffer();
            while ((responseData = br.readLine()) != null) {
                sb.append(responseData);
            }

            // 버퍼의 데이터를 반환 변수에 저장
            returnData = sb.toString();
            Log.d("TAG2", returnData);

            // HTTP 응답 코드 확인
            int responseCode = conn.getResponseCode();
            System.out.println("HTTP 응답 코드 : " + responseCode);
            System.out.println("HTTP 응답 데이터 : " + returnData);

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            // BufferedReader를 닫기
            try {
                if (br != null) {
                    br.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return returnData; // 네트워크 요청 결과 반환
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