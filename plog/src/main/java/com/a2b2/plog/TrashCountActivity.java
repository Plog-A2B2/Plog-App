package com.a2b2.plog;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.google.android.gms.wearable.DataClient;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataItem;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.Wearable;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class TrashCountActivity extends AppCompatActivity implements DataClient.OnDataChangedListener{

    private RecyclerView trashcountRecyclerView;
    //private RecyclerView.Adapter adapter;
    private TrashcountAdapter adapter;
    private List<Integer> dataList;
    private String[] trashTypes = {"종이류", "유리류", "일반쓰레기", "플라스틱", "캔/고철류", "비닐류"};
    private String trashType;
    public int total = 0;
    private int cnt = 0;


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

        adapter = new TrashcountAdapter(dataList, trashTypes);
        trashcountRecyclerView.setAdapter(adapter);

        //total = dataList.get()
        //TextView totalTxt = findViewById(R.id.totalTxt);

        adapter.setOnTotalChangeListener(total -> {
            this.total = total;
            //totalTxt.setText("Total: " + total);
        });
        Wearable.getDataClient(this).addListener(new DataClient.OnDataChangedListener() {
            @Override
            public void onDataChanged(DataEventBuffer dataEvents) {
                for (DataEvent event : dataEvents) {
                    if (event.getType() == DataEvent.TYPE_CHANGED) {
                        DataItem item = event.getDataItem();
                        if (item.getUri().getPath().compareTo("/path/to/data") == 0) {
                            DataMap dataMap = DataMapItem.fromDataItem(item).getDataMap();
                            String value = dataMap.getString("key");
                            Log.d("WatchApp", "Data item received: " + value);

                        }
                    }
                }
            }
        });

        DataClient dataClient = Wearable.getDataClient(this);
        dataClient.addListener(this);

        // Initial total update
       //  totalTxt.setText("Total: " + adapter.getTotalCount());


        //+앱이랑 연결해야함
    }

    @Override //핸드폰에서 값 받아오는 거
    public void onDataChanged(DataEventBuffer dataEvents) {
        for (DataEvent event : dataEvents) {
            if (event.getType() == DataEvent.TYPE_CHANGED) {
                DataItem item = event.getDataItem();
                if (item.getUri().getPath().equals("/path/to/data")) {
                    DataMap dataMap = DataMapItem.fromDataItem(item).getDataMap();
                    String jsonString = dataMap.getString("json_data");

                    try {
                        // JSON 문자열을 JSONObject로 변환
                        JSONObject jsonObject = new JSONObject(jsonString);
                        String value1 = jsonObject.getString(trashType);
                        int value2 = jsonObject.getInt("value");

                        Log.d("WatchApp", "Received JSON data: key1=" + value1 + ", key2=" + value2);
                    } catch (Exception e) {
                        Log.e("WatchApp", "Failed to parse JSON data", e);
                    }
                }
            }
        }
    }
    @Override //백버튼 누를 시 메인으로 값 전달
    public void onBackPressed() {
        Intent resultIntent = new Intent();
        int total = adapter.getTotalCount(); // adapter에서 총합을 가져옴
        resultIntent.putExtra("total", total);
        setResult(RESULT_OK, resultIntent);
        super.onBackPressed();
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


}