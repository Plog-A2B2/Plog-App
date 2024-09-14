package com.a2b2.plog;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONArray;
import org.json.JSONObject;

public class SettingProfileDialog extends Dialog {

    private RecyclerView recyclerView;
    private ProfileBadgeAdapter adapter;
    private Activity activity;
    private Handler handler = new Handler(Looper.getMainLooper());
    private UserManager userManager;
    private List<BadgeItem> badgeList = new ArrayList<>(); // 배지 목록을 저장할 변수

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

        // 배지 목록을 서버에서 받아오는 메서드 호출
        fetchBadgeList();

        recyclerView.setHasFixedSize(true);
        GridLayoutManager badgeLayoutManager = new GridLayoutManager(getContext(), 3);

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

    private void fetchBadgeList() {
        // 서버에서 해금된 배지 목록을 받아오는 코드
//        OkHttpClient client = new OkHttpClient();
//        String url = "http://example.com/getBadgeList"; // 서버의 배지 목록 API URL
//
//        Request request = new Request.Builder()
//                .url(url)
//                .build();
//
//        client.newCall(request).enqueue(new Callback() {
//            @Override
//            public void onFailure(@NonNull Call call, @NonNull IOException e) {
//                handler.post(() -> {
//                    // 실패 시 처리
//                });
//            }
//
//            @Override
//            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
//                if (response.isSuccessful()) {
//                    String responseBody = response.body().string();
//                    try {
//                        JSONArray jsonArray = new JSONArray(responseBody);
//                        badgeList = new ArrayList<>();
//                        for (int i = 0; i < jsonArray.length(); i++) {
//                            JSONObject jsonObject = jsonArray.getJSONObject(i);
//                            int badgeId = jsonObject.getInt("id");
//                            BadgeItem badge = new BadgeItem(badgeId, BadgeManager.getDrawableForBadgeId(badgeId));
//                            badgeList.add(badge);
//                        }
//                        handler.post(() -> {
//                            adapter.setBadgeList(badgeList);
//                        });
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                }
//            }
//        });

        for (int i=1; i<BadgeManager.getBadgeMapSize() + 1; i++) {
            int drawableResId = BadgeManager.getDrawableForBadgeId(i);
            badgeList.add(new BadgeItem(i, drawableResId));
        }

    }

    private void updateProfileImage() {
        // 프로필 이미지 업데이트 로직을 여기에 구현
        // 예를 들어, 액티비티의 프로필 이미지 뷰를 업데이트하는 코드
        // ((YourActivity)activity).updateProfileImage();
    }
}
