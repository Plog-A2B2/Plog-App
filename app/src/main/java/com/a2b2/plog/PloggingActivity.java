package com.a2b2.plog;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.kakao.vectormap.KakaoMap;
import com.kakao.vectormap.KakaoMapReadyCallback;
import com.kakao.vectormap.KakaoMapSdk;
import com.kakao.vectormap.LatLng;
import com.kakao.vectormap.MapLifeCycleCallback;
import com.kakao.vectormap.MapType;
import com.kakao.vectormap.MapView;
import com.kakao.vectormap.MapViewInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import com.kakao.vectormap.Poi;

//import net.daum.mf.map.api.MapPOIItem;
//import net.daum.mf.map.api.MapPoint;
//import net.daum.mf.map.api.MapView;

public class PloggingActivity extends AppCompatActivity {

    private ImageView backBtn;
    private RecyclerView recyclerView;
    private PloggerAdapter ploggerAdapter;
    private List<RealtimePloggerItem> ploggingItems;
    private BottomSheetBehavior<LinearLayout> bottomSheetBehavior;
    private ImageView topBtn;
    private MapView mapView;
    private static final String KAKAO_API_KEY = "1b96fc67568f72bcc29317e838ad740f";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plogging);

        KakaoMapSdk.init(this, "1b96fc67568f72bcc29317e838ad740f");
        mapView = findViewById(R.id.map);

        mapView.start(new MapLifeCycleCallback() {
            @Override
            public void onMapDestroy() {
                // 지도 API가 정상적으로 종료될 때 호출됨
            }

            @Override
            public void onMapError(Exception error) {
                // 인증 실패 및 지도 사용 중 에러가 발생할 때 호출됨
            }
        }, new KakaoMapReadyCallback() {
            @Override
            public void onMapReady(KakaoMap kakaoMap) {
                // 인증 후 API가 정상적으로 실행될 때 호출됨
            }

            @Override
            public LatLng getPosition() {
                // 지도 시작 시 위치 좌표를 설정
                return LatLng.from(37.406960, 127.115587);
            }

            @Override
            public int getZoomLevel() {
                // 지도 시작 시 확대/축소 줌 레벨 설정
                return 15;
            }

        });

        List<String> addressList = ExcelUtils.readExcelFile(this, "trashcan_seoul.xlsx");

        Retrofit retrofit = RetrofitClient.getClient("https://dapi.kakao.com/");
        KakaoApiService apiService = retrofit.create(KakaoApiService.class);

        for (String address : addressList) {
            apiService.getCoordinates(address, KAKAO_API_KEY).enqueue(new Callback<AddressResponse>() {
                @Override
                public void onResponse(Call<AddressResponse> call, Response<AddressResponse> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        AddressResponse.Document.Address address = response.body().documents.get(0).address;
                        double latitude = Double.parseDouble(address.y);
                        double longitude = Double.parseDouble(address.x);
//                        addMarker(mapView, latitude, longitude);
                    }
                }

                @Override
                public void onFailure(Call<AddressResponse> call, Throwable t) {
                    t.printStackTrace();
                }
            });
        }

        try {
            // RecyclerView 초기화
            recyclerView = findViewById(R.id.ploggerRecyclerView);
            recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

            // 이전 액티비티에서 전달된 데이터 받기
            Intent intent = getIntent();
            ArrayList<RealtimePloggerItem> ploggingItems = (ArrayList<RealtimePloggerItem>) intent.getSerializableExtra("ploggingItems");

            // Adapter 설정
            ploggerAdapter = new PloggerAdapter(ploggingItems);
            recyclerView.setAdapter(ploggerAdapter);

            // 뒤로가기 버튼 초기화
            backBtn = findViewById(R.id.backBtn);
            backBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(PloggingActivity.this, HomeActivity.class);
                    startActivity(intent);
                    overridePendingTransition(0, 0);
                    finish();
                }
            });

            // BottomSheet 초기화
            LinearLayout bottomSheet = findViewById(R.id.bottom_sheet);
            bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);

            // BottomSheet 초기 설정
            bottomSheetBehavior.setPeekHeight(200); // 화면 하단에서 살짝 보이게 하는 높이 설정
            bottomSheetBehavior.setHideable(true);  // 스와이프 시 BottomSheet가 숨겨지도록 설정

            // 이미지뷰 클릭 리스너 설정
            topBtn = findViewById(R.id.topBtn);
            topBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (bottomSheetBehavior.getState() != BottomSheetBehavior.STATE_EXPANDED) {
                        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                        topBtn.setImageResource(R.drawable.bottom);
                    } else {
                        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                        topBtn.setImageResource(R.drawable.top);
                    }
                }
            });

            // CoordinatorLayout 클릭 리스너 설정
            CoordinatorLayout coordinatorLayout = findViewById(R.id.coordinatorLayout);
            coordinatorLayout.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
                        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                        topBtn.setImageResource(R.drawable.top);
                        return true;
                    }
                    return false;
                }
            });

            // BottomSheet 상태 변경 시 로그 출력
            bottomSheetBehavior.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
                @Override
                public void onStateChanged(@NonNull View bottomSheet, int newState) {
                    // BottomSheetBehavior state에 따른 이벤트
                    switch (newState) {
                        case BottomSheetBehavior.STATE_HIDDEN:
                            Log.d("PloggingActivity", "state: hidden");
                            break;
                        case BottomSheetBehavior.STATE_EXPANDED:
                            topBtn.setImageResource(R.drawable.bottom);
                            Log.d("PloggingActivity", "state: expanded");
                            break;
                        case BottomSheetBehavior.STATE_COLLAPSED:
                            topBtn.setImageResource(R.drawable.top);
                            Log.d("PloggingActivity", "state: collapsed");
                            break;
                        case BottomSheetBehavior.STATE_DRAGGING:
                            Log.d("PloggingActivity", "state: dragging");
                            break;
                        case BottomSheetBehavior.STATE_SETTLING:
                            Log.d("PloggingActivity", "state: settling");
                            break;
                        case BottomSheetBehavior.STATE_HALF_EXPANDED:
                            Log.d("PloggingActivity", "state: half expanded");
                            break;
                    }
                }

                @Override
                public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                    // Handle slide offset if needed
                }
            });

            // 쓰레기 종류와 개수를 저장할 변수 초기화
            final HashMap<String, Integer> trashCountMap = new HashMap<>();

            LinearLayout trashContainer = findViewById(R.id.trashContainer);
            String[] trashTypes = {"일반쓰레기", "플라스틱", "종이류", "캔/고철류", "병류", "비닐류"};
            LayoutInflater inflater = LayoutInflater.from(this);

            for (String trashType : trashTypes) {
                View itemView = inflater.inflate(R.layout.item_trash, trashContainer, false);
                TextView tvTrashType = itemView.findViewById(R.id.trashType);
                tvTrashType.setText(trashType);

                EditText etTrashAmount = itemView.findViewById(R.id.trashAmount);
                ImageView plusBtn = itemView.findViewById(R.id.plusBtn);
                ImageView minusBtn = itemView.findViewById(R.id.minusBtn);

                // 초기값 설정
                trashCountMap.put(trashType, 0);

                // Plus 버튼 클릭 이벤트
                plusBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int count = trashCountMap.get(trashType);
                        count++;
                        trashCountMap.put(trashType, count);
                        etTrashAmount.setText(String.valueOf(count));
                    }
                });

                // Minus 버튼 클릭 이벤트
                minusBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int count = trashCountMap.get(trashType);
                        if (count > 0) {
                            count--;
                            trashCountMap.put(trashType, count);
                            etTrashAmount.setText(String.valueOf(count));
                        }
                    }
                });

                // EditText 텍스트 변경 이벤트
                etTrashAmount.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        try {
                            int count = Integer.parseInt(s.toString());
                            trashCountMap.put(trashType, count);
                        } catch (NumberFormatException e) {
                            trashCountMap.put(trashType, 0); // 유효하지 않은 숫자 입력 시 0으로 설정
                        }
                    }
                });

                trashContainer.addView(itemView);
            }

            // 종료하기 버튼 클릭 이벤트
            Button finishButton = findViewById(R.id.endBtn);
            finishButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // 쓰레기 개수를 로그로 출력 (또는 필요한 처리)
                    for (String trashType : trashCountMap.keySet()) {
                        int count = trashCountMap.get(trashType);
                        Log.d("PloggingActivity", trashType + ": " + count);
                    }

                    // 결과 화면으로 이동
                }
            });
        } catch (Exception e) {
            Log.e("PloggingActivity", "Exception in onCreate", e);
        }
    }
    @Override
    public void onResume() {
        super.onResume();
        mapView.resume();     // MapView 의 resume 호출
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.pause();    // MapView 의 pause 호출
    }

//    private void addMarker(MapView mapView, double latitude, double longitude, String trashType) {
//        MapView.MapPOIItem marker = new MapView.MapPOIItem();
//        marker.setItemName(trashType);
//        marker.setTag(0);
//        marker.setMapPoint(MapPoint.mapPointWithGeoCoord(latitude, longitude));
//        marker.setMarkerType(MapView.MapPOIItem.MarkerType.BluePin);
//        marker.setSelectedMarkerType(MapView.MapPOIItem.MarkerType.RedPin);
//        mapView.addPOIItem(marker);
//    }

}
