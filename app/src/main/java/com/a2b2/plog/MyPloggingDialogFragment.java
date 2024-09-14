package com.a2b2.plog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.bumptech.glide.Glide;
import com.kakao.vectormap.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.UUID;


public class MyPloggingDialogFragment extends DialogFragment {

    HashMap<String, Integer> trashCountMap = new HashMap<>();
    String url;
    Handler handler = new Handler();
    String result;
    MyPloggingItem activity;

    int activityId;

    public static MyPloggingDialogFragment newInstance(MyPloggingItem activity) {
        MyPloggingDialogFragment fragment = new MyPloggingDialogFragment();
        Bundle args = new Bundle();
        args.putSerializable("activity", activity);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            activity = (MyPloggingItem) getArguments().getSerializable("activity");
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_my_plogging, null);

        TextView dateTextView = view.findViewById(R.id.dateTextView);
        TextView timeTextView = view.findViewById(R.id.timeTextView);
        TextView distanceTextView = view.findViewById(R.id.distanceTextView);
        Button buttonClose = view.findViewById(R.id.closeBtn);

        dateTextView.setText(activity.getPloggingDate());

        // 초를 시, 분, 초 단위로 변환
        int timeInSeconds = activity.getTime();
        int hours = timeInSeconds / 3600;
        int minutes = (timeInSeconds % 3600) / 60;
        int seconds = timeInSeconds % 60;

        // 0h 0m 0s 형식으로 변환하여 표시
        String timeFormatted = String.format("%dh %dm %ds", hours, minutes, seconds);

        timeTextView.setText(timeFormatted);
        distanceTextView.setText(activity.getDistance() + "km");

        getTrashData(inflater, view);  // getTrashData에 view와 inflater 전달

        buttonClose.setOnClickListener(v -> dismiss());

        builder.setView(view);
        return builder.create();
    }

    private void getTrashData(LayoutInflater inflater, View view) {
        UUID uuid = UserManager.getInstance().getUserId();
        activityId = activity.getActivityId();

        url = "http://15.164.152.246:8080/trash/" + uuid + "/" + activityId;

        new Thread(() -> {
            String result = httpGetConnection(url);
            handler.post(() -> {
                seeNetworkResult(result);
                if (result != null && !result.isEmpty()) {
                    // Parse the result and update trashCountMap
                    trashCountMap = parseTrashData(result, view);
                }

                setTrashCount(inflater, view);  // 뷰와 인플레이터 전달
            });
        }).start();
    }

    public HashMap<String, Integer> parseTrashData(String json, View view) {
        HashMap<String, Integer> trashDataMap = new HashMap<>();

        try {
            JSONObject jsonObject = new JSONObject(json);
            JSONObject dataObject = jsonObject.getJSONObject("data");

            String photoUrl;
            // 각 쓰레기 유형 및 수량을 HashMap에 저장
            trashDataMap.put("일반쓰레기", dataObject.getInt("garbage"));
            trashDataMap.put("캔/고철류", dataObject.getInt("can"));
            trashDataMap.put("플라스틱", dataObject.getInt("plastic"));
            trashDataMap.put("종이류", dataObject.getInt("paper"));
            trashDataMap.put("비닐류", dataObject.getInt("plastic_bag"));
            trashDataMap.put("유리류", dataObject.getInt("glass"));

            photoUrl = dataObject.getString("photo");
            ImageView ploggingPhoto = view.findViewById(R.id.ploggingPhoto);

            if (!photoUrl.equals("none")) {
                Log.d("photoUrl is", "none X");
                Log.d("photoUrl is", photoUrl);
                ploggingPhoto.setVisibility(View.VISIBLE);
                Glide.with(this)
                        .load(photoUrl)
                        .placeholder(R.drawable.logotest2) // 로딩 중 보여줄 이미지
                        .error(R.drawable.logotest2) // 오류 발생 시 보여줄 이미지
                        .fitCenter()
                        .into(ploggingPhoto);
            } else {
                Log.d("photoUrl is", "none O");
                ploggingPhoto.setVisibility(View.GONE);
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }

        return trashDataMap;
    }

    private void setTrashCount(LayoutInflater inflater, View view) {
        LinearLayout trashContainer1 = view.findViewById(R.id.trashContainer1);
        LinearLayout trashContainer2 = view.findViewById(R.id.trashContainer2);

        String[] trashTypes1 = {"일반쓰레기", "플라스틱", "종이류"};
        String[] trashTypes2 = {"캔/고철류", "유리류", "비닐류"};

        // 첫 번째 쓰레기 타입 처리
        for (String trashType : trashTypes1) {
            View itemView = inflater.inflate(R.layout.item_trash_finish, trashContainer1, false);
            TextView tvTrashType = itemView.findViewById(R.id.trashType);
            tvTrashType.setText(trashType);

            TextView tvTrashAmount = itemView.findViewById(R.id.trashAmount);
            Integer count = trashCountMap.get(trashType);
            if (count == null) count = 0;  // 기본값 설정
            tvTrashAmount.setText(count + "개");

            trashContainer1.addView(itemView);
        }

        // 두 번째 쓰레기 타입 처리
        for (String trashType : trashTypes2) {
            View itemView = inflater.inflate(R.layout.item_trash_finish, trashContainer2, false);
            TextView tvTrashType = itemView.findViewById(R.id.trashType);
            tvTrashType.setText(trashType);

            TextView tvTrashAmount = itemView.findViewById(R.id.trashAmount);
            Integer count = trashCountMap.get(trashType);
            if (count == null) count = 0;  // 기본값 설정
            tvTrashAmount.setText(count + "개");

            trashContainer2.addView(itemView);
        }
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

    public void seeNetworkResult(String result) {
        Log.d("NetworkResult", result);
    }
}
