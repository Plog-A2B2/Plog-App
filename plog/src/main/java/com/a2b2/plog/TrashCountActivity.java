package com.a2b2.plog;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.wearable.DataClient;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataItem;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.MessageClient;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class TrashCountActivity extends AppCompatActivity implements DataClient.OnDataChangedListener, TrashcountAdapter.OnTrashTypeUpdateListener {

    private RecyclerView trashcountRecyclerView;
    //private RecyclerView.Adapter adapter;
    private TrashcountAdapter adapter;
    private List<TrashcountItem> dataList;
    private String[] trashTypes = {"종이류", "유리류", "일반쓰레기", "플라스틱", "캔/고철류", "비닐류"};
    private String trashType;
    public int total = 0;
    private int count = 0;
    private Boolean getAllTrash = true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trashcount);



        trashcountRecyclerView = findViewById(R.id.trashcountRecyclerView);
        trashcountRecyclerView.setLayoutManager(new LinearLayoutManager(this));


        dataList = new ArrayList<>();
        for (int i = 0; i < trashTypes.length; i++) {
            dataList.add(new TrashcountItem(trashTypes[i],0));
        }

        // MessageClient를 통해 데이터 수신 리스너 설정
        Wearable.getMessageClient(this).addListener(new MessageClient.OnMessageReceivedListener() {
            @Override
            public void onMessageReceived(MessageEvent messageEvent) {
                if (messageEvent.getPath().equals("/path/to/EachTrashGet")) {
                    String jsonString = new String(messageEvent.getData());
                    try {
                        JSONObject jsonObject = new JSONObject(jsonString);
                        List<TrashcountItem> trashList = new ArrayList<>();

                        for (String trashType : trashTypes) {
                            if (jsonObject.has(trashType)) {
                                int count = jsonObject.getInt(trashType);
                                trashList.add(new TrashcountItem(trashType, count));
                            }
                        }

                        // 어댑터에 데이터 갱신
                        adapter.updateData(trashList);

                    } catch (JSONException e) {
                        Log.e("WatchApp", "Failed to parse JSON", e);
                    }
                }
            }
        });
//        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//        View itemView = inflater.inflate(R.layout.item_trashcount, null);
//
//        ImageView plus = itemView.findViewById(R.id.plus);
//        ImageView min = itemView.findViewById(R.id.min);
//
//        for(String trashType : trashTypes){
//            sendDataToPhone(trashType,dataList.getCnt());
//        }

        adapter = new TrashcountAdapter(this, dataList, trashTypes, this);
        trashcountRecyclerView.setAdapter(adapter);

        //total = dataList.get()
        //TextView totalTxt = findViewById(R.id.totalTxt);

        adapter.setOnTotalChangeListener(total -> {
            this.total = total;
            //totalTxt.setText("Total: " + total);
        });

        DataClient dataClient = Wearable.getDataClient(this);
        dataClient.addListener(this);

        sendDataToPhone("getAllTrash",getAllTrash);
        // Initial total update
       //  totalTxt.setText("Total: " + adapter.getTotalCount());

// 앱 실행 시 워치에서 최신 데이터 요청 (필요 시 구현)
        requestLatestDataFromWatch();

        //+앱이랑 연결해야함
    }
    private void requestLatestDataFromWatch() {
        // 워치로부터 데이터를 요청하는 로직을 여기에 추가
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
        ArrayList<TrashcountItem> itemList = new ArrayList<>(adapter.getData());
        outState.putParcelableArrayList("tData", itemList);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState != null) {
            ArrayList<TrashcountItem> restoredData = savedInstanceState.getParcelableArrayList("tData");
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

    @Override
    public void onDataChanged(@NonNull DataEventBuffer dataEventBuffer) {

    }
//    private void sendDataToPhone(String key, boolean getAllTrash) {
//        Log.d("sendDataToPhone","전송완료" + getAllTrash);
//        // JSON 데이터를 "/json_data" 경로로 전송
//        PutDataMapRequest dataMap = PutDataMapRequest.create("/getAllTrash");
//        dataMap.getDataMap().putBoolean(key, getAllTrash);
//        PutDataRequest request = dataMap.asPutDataRequest();
//
//        // DataClient 사용하여 데이터 전송
//        DataClient dataClient = Wearable.getDataClient(this);
//        dataClient.putDataItem(request)
//                .addOnSuccessListener(dataItem -> {
//                    // 전송 성공 시 로그
//                    Log.d("sendDataToPhone", "데이터 전송 성공");
//                })
//                .addOnFailureListener(e -> {
//                    // 전송 실패 시 로그
//                    Log.e("sendDataToPhone", "데이터 전송 실패", e);
//                });
//    }
    private void sendDataToPhone(String key, boolean getAllTrash) {
        Log.d("sendDataToPhone", "전송완료 " + getAllTrash);

        // DataItem을 생성할 때 타임스탬프 추가
        PutDataMapRequest dataMap = PutDataMapRequest.create("/getAllTrash");
        dataMap.getDataMap().putBoolean(key, getAllTrash);

        // 타임스탬프 추가 - 매번 데이터 전송 시 시간이 달라지므로 데이터가 갱신됨
        dataMap.getDataMap().putLong("timestamp", System.currentTimeMillis());

        PutDataRequest request = dataMap.asPutDataRequest();

        // DataClient를 통해 데이터 전송
        DataClient dataClient = Wearable.getDataClient(this);
        dataClient.putDataItem(request)
                .addOnSuccessListener(dataItem -> Log.d("sendDataToPhone", "데이터 전송 성공"))
                .addOnFailureListener(e -> Log.e("sendDataToPhone", "데이터 전송 실패", e));
    }

}