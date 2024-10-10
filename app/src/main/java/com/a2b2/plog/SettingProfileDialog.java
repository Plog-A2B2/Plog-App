package com.a2b2.plog;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class SettingProfileDialog extends Dialog {

    private RecyclerView recyclerView;
    private ProfileBadgeAdapter adapter;
    private Activity activity;
    private Handler handler = new Handler(Looper.getMainLooper());
    private UserManager userManager;
    private List<BadgeItem> badgeList = new ArrayList<>(); // 배지 목록을 저장할 변수
    String url;
    String result = "";
    GridLayoutManager badgeLayoutManager = new GridLayoutManager(getContext(), 3);
    public SettingProfileDialog(@NonNull Activity activity, UserManager userManager) {
        super(activity);
        this.activity = activity;
        this.userManager = userManager;
    }

    public interface OnItemClickListener {
        void onItemClick(Integer badge);
    }

    private OnItemClickListener itemClickListener;

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.itemClickListener = listener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_setting_popup);

        recyclerView = findViewById(R.id.badgeRecyclerView);

        recyclerView.setHasFixedSize(true);


        // 배지 목록을 서버에서 받아오는 메서드 호출
        fetchBadgeList();



    }

    private void fetchBadgeList() {
        // 서버에서 해금된 배지 목록을 받아오는 코드
        UUID uuid = UserManager.getInstance().getUserId();
        handler = new Handler();

        url = "http://15.164.152.246:8080/mybadge/" + uuid;

        new Thread(() -> {
            result = httpGetConnection(url);
            Log.d("fetchBadgeList", result);
            handler.post(() -> {
                if (result != null && !result.isEmpty()) {
                    badgeList = parseBadgeAll(result);

                    adapter = new ProfileBadgeAdapter(badgeList);
                    adapter.setOnItemClickListener(new ProfileBadgeAdapter.OnItemClickListener() {
                        @Override
                        public void onItemClick(int position) {
                            BadgeItem clickedBadge = badgeList.get(position);

                            // UserManager에 배지 아이디 저장
                            userManager.setBadgeId(clickedBadge.getBadgeId());
                            // 프로필 이미지 업데이트
//            updateProfileImage();
                            if(itemClickListener != null) {
                                itemClickListener.onItemClick(clickedBadge.getBadgeId());
                            }
                            dismiss();
                        }
                    });

                    recyclerView.setLayoutManager(badgeLayoutManager);
                    recyclerView.setAdapter(adapter);
                }
            });
        }).start();

//        for (int i=1; i<BadgeManager.getBadgeMapSize() + 1; i++) {
//            int drawableResId = BadgeManager.getDrawableForBadgeId(i);
//            badgeList.add(new BadgeItem(i, drawableResId));
//        }

    }

    public static List<BadgeItem> parseBadgeAll(String jsonResponse) {
        List<Integer> ownedBadgeIds = new ArrayList<>();
        List<BadgeItem> myBadgeItemList = new ArrayList<>();

        try {
            // JSON 응답을 JSONObject로 변환
            JSONObject jsonObject = new JSONObject(jsonResponse);

            // 'data' 배열을 가져옴
            JSONArray dataArray = jsonObject.getJSONArray("data");

            // 각 배지 ID를 리스트에 추가
            for (int i = 0; i < dataArray.length(); i++) {
                // JSON 배열에서 int 값 추출
                int badgeId = dataArray.getInt(i);  // getJSONObject가 아니라 getInt 사용
                ownedBadgeIds.add(badgeId);  // 배지 ID는 1부터 시작
            }

        } catch (JSONException e) {
            e.printStackTrace();  // 에러 발생 시 로그 출력
        }

        // 소유한 배지 ID에 대해 BadgeItem 리스트 생성
        for (int badgeId : ownedBadgeIds) {
            // 배지 ID가 1부터 시작하는 것을 가정하고 그대로 처리
            int drawableResId = BadgeManager.getDrawableForBadgeId(badgeId);
            myBadgeItemList.add(new BadgeItem(badgeId, drawableResId));
        }

        return myBadgeItemList;  // 소유한 배지 리스트 반환
    }

    public String httpGetConnection(String UrlData) {
        String responseData = "";
        BufferedReader br = null;

        try {
            URL url = new URL(UrlData);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Content-Type", "application/json; utf-8");
            conn.setRequestProperty("Accept", "application/json");

            br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
            StringBuilder sb = new StringBuilder();
            while ((responseData = br.readLine()) != null) {
                sb.append(responseData);
            }

            return sb.toString();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } finally {
            try {
                if (br != null) {
                    br.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void updateProfileImage() {
        // 프로필 이미지 업데이트 로직을 여기에 구현
        // 예를 들어, 액티비티의 프로필 이미지 뷰를 업데이트하는 코드
        // ((YourActivity)activity).updateProfileImage();
    }
}
