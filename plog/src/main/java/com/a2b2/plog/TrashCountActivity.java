package com.a2b2.plog;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class TrashCountActivity extends AppCompatActivity {

    private RecyclerView trashcountRecyclerView;
    //private RecyclerView.Adapter adapter;
    private TrashcountAdapter adapter;
    private List<Integer> dataList;
    private String[] trashTypes = {"종이류", "유리류", "일반쓰레기", "플라스틱", "캔/고철류", "비닐류"};
    public int total = 0;


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
        TextView totalTxt = findViewById(R.id.totalTxt);

        adapter.setOnTotalChangeListener(total -> {
            this.total = total;
            totalTxt.setText("Total: " + total);
        });

        // Initial total update
        totalTxt.setText("Total: " + adapter.getTotalCount());


        //+앱이랑 연결해야함
    }
    @Override
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