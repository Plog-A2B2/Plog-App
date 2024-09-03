package com.a2b2.plog;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.StateListDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MyCommunityActivity extends AppCompatActivity {

    private Button mytext, finish,like;
    private Button selectedButton;
    private RecyclerView MycommunityRecycler;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private List<CommunityList> MycommunityList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_community);

        MycommunityList = new ArrayList<>();
//        MycommunityList.add(new CommunityItem(R.drawable.tiger,"지구지킴이","2024.08.18(일)","8월 30일에 같이 플로깅 하실 분 있나요?"));
//        MycommunityList.add(new CommunityItem(R.drawable.tiger,"지구지킴이","2024.08.18(일)","8월 30일에 같이 플로깅 하실 분 있나요?"));
//        MycommunityList.add(new CommunityItem(R.drawable.tiger,"지구지킴이","2024.08.18(일)","8월 30일에 같이 플로깅 하실 분 있나요?"));
//        MycommunityList.add(new CommunityItem(R.drawable.tiger,"지구지킴이","2024.08.18(일)","8월 30일에 같이 플로깅 하실 분 있나요?"));
//        MycommunityList.add(new CommunityItem(R.drawable.tiger,"지구지킴이","2024.08.18(일)","8월 30일에 같이 플로깅 하실 분 있나요?"));
//        MycommunityList.add(new CommunityItem(R.drawable.tiger,"지구지킴이","2024.08.18(일)","8월 30일에 같이 플로깅 하실 분 있나요?"));

        MycommunityRecycler = findViewById(R.id.recyclerView);
        MycommunityRecycler.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        MycommunityRecycler.setLayoutManager(layoutManager);
        adapter = new CommunityAdapter(MycommunityList, this);
        MycommunityRecycler.setAdapter(adapter);

        mytext = findViewById(R.id.mytext);
        finish = findViewById(R.id.finish);
        like = findViewById(R.id.like);
        ImageView backBtn = findViewById(R.id.backBtn);

        //resetButtonBackgrounds();
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MyCommunityActivity.this, CommunityActivity.class);
                startActivity(intent);
                finish();
            }
        });

        TextView check = findViewById(R.id.check);

        if (check.getText() == "mytext"){
            setButtonPressedState(mytext,true);
        }
        mytext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mytext.setBackgroundResource(R.drawable.round_white);
                finish.setBackgroundResource(R.drawable.round_gray);
                like.setBackgroundResource(R.drawable.round_gray);
            }
        });
        finish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish.setBackgroundResource(R.drawable.round_white);
                mytext.setBackgroundResource(R.drawable.round_gray);
                like.setBackgroundResource(R.drawable.round_gray);
            }
        });
        like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                like.setBackgroundResource(R.drawable.round_white);
                finish.setBackgroundResource(R.drawable.round_gray);
                mytext.setBackgroundResource(R.drawable.round_gray);
            }
        });

        Drawable roundWhiteDrawable = getResources().getDrawable(R.drawable.round_white);

//        if (mytext.getBackground().getConstantState().equals(roundWhiteDrawable)){
//            finish.setBackgroundResource(R.drawable.round_gray);
//            like.setBackgroundResource(R.drawable.round_gray);
//        } else if(finish.getBackground().getConstantState().equals(roundWhiteDrawable)){
//            mytext.setBackgroundResource(R.drawable.round_gray);
//            like.setBackgroundResource(R.drawable.round_gray);
//        }else if(like.getBackground().getConstantState().equals(roundWhiteDrawable)){
//            finish.setBackgroundResource(R.drawable.round_gray);
//            mytext.setBackgroundResource(R.drawable.round_gray);
//        }
    }
    // 모든 버튼의 배경을 기본값으로 초기화하는 메서드
    private void resetButtonBackgrounds() {
        setButtonBackground(mytext, "#E5E5EC");
        setButtonBackground(finish, "#E5E5EC");
        setButtonBackground(like, "#E5E5EC");
    }

    // 버튼의 상태를 업데이트하는 메서드
    private void updateButtonState(Button clickedButton) {
        if (selectedButton != null) {
            setButtonBackground(selectedButton, "#E5E5EC"); // 기본 색상으로 설정
        }

        setButtonBackground(clickedButton, "#FFFFFF"); // 흰색으로 설정
        selectedButton = clickedButton; // 현재 클릭된 버튼을 선택된 버튼으로 설정
    }
    // 버튼의 pressed 상태를 설정하는 메서드
    private void setButtonPressedState(Button button, boolean pressed) {
        StateListDrawable stateListDrawable = (StateListDrawable) button.getBackground();
        if (pressed) {
            // Button을 'pressed' 상태로 설정
            stateListDrawable.setState(new int[]{android.R.attr.state_pressed});
        } else {
            // Button을 기본 상태로 설정
            stateListDrawable.setState(new int[]{});
        }
        button.setBackground(stateListDrawable);
    }

    // 버튼의 배경색을 설정하는 메서드
    private void setButtonBackground(Button button, String color) {
        Drawable background = button.getBackground();
        if (background instanceof StateListDrawable) {
            StateListDrawable stateListDrawable = (StateListDrawable) background;
            Drawable current = stateListDrawable.getCurrent();

            if (current instanceof GradientDrawable) {
                GradientDrawable gradientDrawable = (GradientDrawable) current;
                gradientDrawable.setColor(Color.parseColor(color));
            }
        }
    }

}