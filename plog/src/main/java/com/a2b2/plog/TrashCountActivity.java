package com.a2b2.plog;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.wearable.DataClient;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataItem;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.MessageClient;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Wearable;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class TrashCountActivity extends AppCompatActivity implements DataClient.OnDataChangedListener, TrashcountAdapter.OnTrashTypeUpdateListener {

    private RecyclerView trashcountRecyclerView;
    //private RecyclerView.Adapter adapter;
    private TrashcountAdapter adapter;
    private List<Integer> dataList;
    private String[] trashTypes = {"종이류", "유리류", "일반쓰레기", "플라스틱", "캔/고철류", "비닐류"};
    private String trashType;
    public int total = 0;
    private int count = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trashcount);

        trashcountRecyclerView = findViewById(R.id.trashcountRecyclerView);
        trashcountRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        dataList = new ArrayList<>();
        for (int i = 0; i < trashTypes.length; i++) {
            dataList.add(0);
        }

        // MessageClient를 통해 데이터 수신 리스너 설정
        Wearable.getMessageClient(this).addListener(new MessageClient.OnMessageReceivedListener() {
            @Override
            public void onMessageReceived(MessageEvent messageEvent) {
                if (messageEvent.getPath().equals("/path/to/EachTrashGet")) {
                    String jsonString = new String(messageEvent.getData());
                    try {
                        JSONObject jsonObject = new JSONObject(jsonString);

                        for (int i = 0; i < trashTypes.length; i++) {
                            String trashType = trashTypes[i];
                            if (jsonObject.has(trashType)) {
                                int newCount = jsonObject.getInt(trashType);
                                // 해당 trashType의 index를 찾기
                                int index = -1;
                                for (int j = 0; j < trashTypes.length; j++) {
                                    if (trashTypes[j].equals(trashType)) {
                                        index = j;
                                        break;
                                    }
                                }
                                if (index != -1) {
                                    // dataList와 어댑터의 값을 업데이트
                                    dataList.set(index, newCount);
                                    adapter.notifyItemChanged(index); // 특정 아이템만 갱신
                                    total = adapter.getTotalCount(); // 총합 업데이트
                                    // totalTxt.setText("Total: " + total); // 총합 UI 업데이트 (필요 시)
                                }
                            }
                        }
                        adapter.notifyDataSetChanged();
                    } catch (JSONException e) {
                        Log.e("WatchApp", "Failed to parse JSON", e);
                    }
                }
            }
        });

        adapter = new TrashcountAdapter(dataList, trashTypes, this);
        trashcountRecyclerView.setAdapter(adapter);

        //total = dataList.get()
        //TextView totalTxt = findViewById(R.id.totalTxt);

        adapter.setOnTotalChangeListener(total -> {
            this.total = total;
            //totalTxt.setText("Total: " + total);
        });

        DataClient dataClient = Wearable.getDataClient(this);
        dataClient.addListener(this);

        // Initial total update
       //  totalTxt.setText("Total: " + adapter.getTotalCount());

// 앱 실행 시 워치에서 최신 데이터 요청 (필요 시 구현)
        requestLatestDataFromWatch();

        //+앱이랑 연결해야함
    }
    private void requestLatestDataFromWatch() {
        // 워치로부터 데이터를 요청하는 로직을 여기에 추가
    }

    @Override //핸드폰에서 값 받아오는 거
    public void onDataChanged(DataEventBuffer dataEvents) {
        Log.d("WatchApp", "onDataChanged called, checking for data events...");

        for (DataEvent event : dataEvents) {
            if (event.getType() == DataEvent.TYPE_CHANGED) {
                DataItem item = event.getDataItem();
                Log.d("WatchApp", "Data event received. Path: " + item.getUri().getPath());

                if (item.getUri().getPath().equals("/path/to/data")) {
                    DataMap dataMap = DataMapItem.fromDataItem(item).getDataMap();
                    String jsonString = dataMap.getString("json_data");
                    Log.d("WatchApp", "Received JSON string: " + jsonString);

                    try {
                        // JSON 문자열을 JSONObject로 변환
                        JSONObject jsonObject = new JSONObject(jsonString);

                        if(jsonObject != null) {
                            for (int i = 0; i < trashTypes.length; i++) {
                                int value = jsonObject.getInt(trashTypes[i]); // 쓰레기 종류별 개수를 가져옴
                                Log.d("WatchApp", "Updating " + trashTypes[i] + " with value: " + value);

                                dataList.set(i, value); // 해당 리스트 항목을 업데이트
                            }

                            // 어댑터를 통해 RecyclerView를 업데이트
                            adapter.notifyDataSetChanged();
                            Log.d("WatchApp", "RecyclerView updated with new data");

                            // 총합 업데이트
                            total = adapter.getTotalCount();
                        } else {
                            Log.w("WatchApp", "Received null JSONObject, resetting dataList to zeros.");

                            for (int i = 0; i < trashTypes.length; i++) {
                                dataList.set(i, 0); // 해당 리스트 항목을 업데이트
                            }
                        }

                        //totalTxt.setText("Total: " + total); // UI에 총합 반영
                    } catch (Exception e) {
                        Log.e("WatchApp", "Failed to parse JSON data", e);
                    }
                }
            }
        }
    }

    @Override
    public void onBackPressed() {
        total = adapter.getTotalCount(); // 총합을 가져옴
        Intent resultIntent = new Intent();
        resultIntent.putExtra("totalCount", total); // 값을 전달
        setResult(RESULT_OK, resultIntent); // 결과 설정
        super.onBackPressed(); // 기본 동작인 finish()를 호출하여 액티비티 종료
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putIntegerArrayList("tData", new ArrayList<>(adapter.getData()));
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState != null) {
            List<Integer> restoredData = savedInstanceState.getIntegerArrayList("mData");
            if (restoredData != null) {
                adapter.setData(restoredData);
                adapter.notifyDataSetChanged();
            }
        }
    }
    @Override
    protected void onPause() {
        super.onPause();
        total = adapter.getTotalCount(); // 총합을 가져옴
        saveTotalToPreferences(total); // SharedPreferences에 저장
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        total = adapter.getTotalCount(); // 총합을 가져옴
        saveTotalToPreferences(total); // SharedPreferences에 저장
    }

    private void saveTotalToPreferences(int total) {
        SharedPreferences sharedPreferences = getSharedPreferences("trashData", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("total", total);
        editor.apply(); // 데이터 저장
    }

    @Override
    public void onTrashTypeUpdate(String trashType, int count) {

    }
}