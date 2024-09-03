package com.a2b2.plog;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class SettingLocationDialog extends Dialog {

    private RecyclerView recyclerView;
    private EditText searchEditText;
    private LocationAdapter adapter;
    private List<Place> documentList;
    private Activity activity;
    private Handler handler = new Handler(Looper.getMainLooper());


    public SettingLocationDialog(@NonNull Activity activity) {
        super(activity);
        this.activity = activity;
        documentList = new ArrayList<>();
    }

    public interface OnItemClickListener {
        void onItemClick(Place document);
    }

    private OnItemClickListener itemClickListener;

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.itemClickListener = listener;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.location_setting_popup);

        searchEditText = findViewById(R.id.searchEditText);
        recyclerView = findViewById(R.id.recyclerView);

        adapter = new LocationAdapter(documentList, document -> {
            // 장소 선택 시 처리할 로직
            if (itemClickListener != null) {
                itemClickListener.onItemClick(document); // 클릭한 장소 정보 전달
            }
            dismiss();
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {
                    searchLocation(s.toString());
                } else {
                    documentList.clear();
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void searchLocation(String query) {
        OkHttpClient client = new OkHttpClient();
        String url = "https://dapi.kakao.com/v2/local/search/keyword.json?query=" + query;
        Request request = new Request.Builder()
                .url(url)
                .header("Authorization", "KakaoAK " + "856b60d15352dfaae39da72e011fc9c3")
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("FindDepartureDialog", "Network request failed", e); // 로그 추가
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    try {
                        JSONObject jsonResponse = new JSONObject(response.body().string());
                        JSONArray documents = jsonResponse.getJSONArray("documents");

                        documentList.clear();
                        for (int i = 0; i < documents.length(); i++) {
                            JSONObject document = documents.getJSONObject(i);
                            String placeName = document.getString("place_name");
                            String addressName = document.getString("address_name");
                            documentList.add(new Place(placeName, addressName));
                        }
                        handler.post(() -> adapter.notifyDataSetChanged());
                    } catch (Exception e) {
                        Log.e("FindDepartureDialog", "Parsing response failed", e); // 로그 추가
                    }
                } else {
                    Log.e("FindDepartureDialog", "Response not successful: " + response.code()); // 로그 추가
                }
            }
        });
    }
}
