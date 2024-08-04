package com.a2b2.plog;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
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

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.kakao.vectormap.GestureType;
import com.kakao.vectormap.KakaoMap;
import com.kakao.vectormap.KakaoMapReadyCallback;
import com.kakao.vectormap.KakaoMapSdk;
import com.kakao.vectormap.LatLng;
import com.kakao.vectormap.MapLifeCycleCallback;
import com.kakao.vectormap.MapView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.kakao.vectormap.camera.CameraPosition;
import com.kakao.vectormap.camera.CameraUpdate;
import com.kakao.vectormap.label.Label;
import com.kakao.vectormap.label.LabelLayer;
import com.kakao.vectormap.label.LabelOptions;
import com.kakao.vectormap.label.LabelStyle;
import com.kakao.vectormap.label.LabelStyles;

import com.kakao.vectormap.camera.CameraUpdate;
import com.kakao.vectormap.camera.CameraUpdateFactory;
import com.kakao.vectormap.camera.CameraAnimation;
import com.kakao.vectormap.label.PathOptions;
import com.kakao.vectormap.label.TrackingManager;


public class PloggingActivity extends AppCompatActivity {

    private ImageView backBtn;
    private RecyclerView recyclerView;
    private PloggerAdapter ploggerAdapter;
    private List<RealtimePloggerItem> ploggingItems;
    private BottomSheetBehavior<LinearLayout> bottomSheetBehavior;
    private ImageView topBtn;
    private MapView mapView;
    private static final String KAKAO_API_KEY = "1b96fc67568f72bcc29317e838ad740f";

    private KakaoMap map;
    private LabelLayer labelLayer;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private FusedLocationProviderClient fusedLocationClient;
    private LocationCallback locationCallback;

    private final String[] locationPermissions = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};

    private HashMap<String, LabelOptions> userMarkers = new HashMap<>();
    private String userMarkerKey = "user_location_marker";

    // 위치 업데이트가 요청된 적 있는지 여부를 저장합니다.
    private boolean requestingLocationUpdates = false;
    private LocationRequest locationRequest;
    Label userLabel;
    private LatLng startPosition = null;

    private ImageView userCurrentLocation;

    MapLifeCycleCallback readyCallback1 = new MapLifeCycleCallback() {
        @Override
        public void onMapDestroy() {

        }

        @Override
        public void onMapError(Exception e) {

        }
    };
    boolean trashcanVisible;
    TrackingManager trackingManager;
    KakaoMapReadyCallback readyCallback2 = new KakaoMapReadyCallback() {

        @Override
        public void onMapReady(KakaoMap kakaoMap) {
            map = kakaoMap;
            labelLayer = map.getLabelManager().getLayer();

            // 엑셀 파일에서 위치 정보를 가져옴
            Map<String, Location> locationMap = ExcelUtils.readExcelFile(PloggingActivity.this, "trashcan_location.xlsx");

            // 위치 정보를 기반으로 마커 추가
            for (Map.Entry<String, Location> entry : locationMap.entrySet()) {
                Location location = entry.getValue();
                Log.d("trashcanVisible", String.valueOf(trashcanVisible));
                if(trashcanVisible == true) {
                    addMarker(location.getLatitude(), location.getLongitude());
                }
            }

            Bitmap originalBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.user_marker);

            // 원하는 크기로 이미지 조정
            int newWidth = 70; // 새로운 너비 (픽셀 단위)
            int newHeight = 70; // 새로운 높이 (픽셀 단위)
            Bitmap resizedBitmap = Bitmap.createScaledBitmap(originalBitmap, newWidth, newHeight, false);

            userLabel = labelLayer.addLabel(LabelOptions.from("userLabel", startPosition)
                    .setStyles(LabelStyle.from(resizedBitmap).setAnchorPoint(0.5f, 0.5f))
                    .setRank(1));

            trackingManager = map.getTrackingManager();
            trackingManager.startTracking(userLabel);
            startLocationUpdates();


            // 지도 이동 이벤트 설정
            map.setOnCameraMoveEndListener(new KakaoMap.OnCameraMoveEndListener() {
                @Override
                public void onCameraMoveEnd(@NonNull KakaoMap kakaoMap,
                                            @NonNull CameraPosition cameraPosition,
                                            @NonNull GestureType gestureType) {

                    trackingManager.stopTracking();
                }
            });
        }

        @Override
        public LatLng getPosition() {
            return startPosition;
        }

        @Override
        public int getZoomLevel() {
            return 17;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plogging);

        KakaoMapSdk.init(this, "1b96fc67568f72bcc29317e838ad740f");
        mapView = findViewById(R.id.map);

//        mapView.start(new MapLifeCycleCallback() {
//            @Override
//            public void onMapDestroy() {
//                // 지도 API가 정상적으로 종료될 때 호출됨
//            }
//
//            @Override
//            public void onMapError(Exception error) {
//                // 인증 실패 및 지도 사용 중 에러가 발생할 때 호출됨
//            }
//        }, new KakaoMapReadyCallback() {
//            @Override
//            public void onMapReady(KakaoMap kakaoMap) {
//                // 인증 후 API가 정상적으로 실행될 때 호출됨
//                map = kakaoMap;
//                labelLayer = kakaoMap.getLabelManager().getLayer();
//
//                // 엑셀 파일에서 위치 정보를 가져옴
//                Map<String, Location> locationMap = ExcelUtils.readExcelFile(PloggingActivity.this, "trashcan_location.xlsx");
//
//                // 위치 정보를 기반으로 마커 추가
//                for (Map.Entry<String, Location> entry : locationMap.entrySet()) {
//                    Location location = entry.getValue();
//                    addMarker(location.getLatitude(), location.getLongitude());
//                }
//                // 위치 서비스 초기화
//                fusedLocationClient = LocationServices.getFusedLocationProviderClient(PloggingActivity.this);
//
//                // 위치 권한 요청
//                checkLocationPermission();
//                updateLocationOnMap(location);
//
//            }
//
//            @Override
//            public LatLng getPosition() {
//                // 지도 시작 시 위치 좌표를 설정
//                return LatLng.from(37.5763811, 126.9728228);
//            }
//
//            @Override
//            public int getZoomLevel() {
//                // 지도 시작 시 확대/축소 줌 레벨 설정
//                return 15;
//            }
//
//        });
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        locationRequest = new LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 2000L).build();
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                for (android.location.Location location : locationResult.getLocations()) {
                    userLabel.moveTo(LatLng.from(location.getLatitude(), location.getLongitude()));
                }
            }
        };

        if (ContextCompat.checkSelfPermission(this, locationPermissions[0]) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this, locationPermissions[1]) == PackageManager.PERMISSION_GRANTED) {
            getStartLocation();
        } else {
            ActivityCompat.requestPermissions(this, locationPermissions, LOCATION_PERMISSION_REQUEST_CODE);
        }


        userCurrentLocation = findViewById(R.id.current_location);

        userCurrentLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                map.moveCamera(CameraUpdateFactory.newCenterPosition(userLatLng, 15),
//                CameraAnimation.from(100));
                trackingManager.startTracking(userLabel);
            }
        });

//// Excel 파일에서 위치 정보를 가져옴
//        Map<String, Location> locationMap = ExcelUtils.readExcelFile(PloggingActivity.this, "trashcan_location.xlsx");
//
//        Retrofit retrofit = RetrofitClient.getClient("https://dapi.kakao.com/");
//        KakaoApiService apiService = retrofit.create(KakaoApiService.class);
//
//        for (Map.Entry<String, Location> entry : locationMap.entrySet()) {
//            String address = entry.getKey();
//            Location location = entry.getValue();
//
//            apiService.getCoordinates(address, KAKAO_API_KEY).enqueue(new Callback<AddressResponse>() {
//                @Override
//                public void onResponse(Call<AddressResponse> call, Response<AddressResponse> response) {
//                    Log.d("test", "in onResponse");
//                    Log.d("test", "Response Code: " + response.code());
//                    Log.d("test", "Response Body: " + response.body());
//                    Log.d("test", "Response Message: " + response.message());
//
//                    if (response.isSuccessful() && response.body() != null) {
//                        Log.d("test", "in if");
//
//                        try {
//                            AddressResponse.Document.Address address = response.body().documents.get(0).address;
//                            double latitude = Double.parseDouble(address.y);
//                            double longitude = Double.parseDouble(address.x);
//                            Log.d("test", String.format("Latitude: %.6f, Longitude: %.6f", latitude, longitude));
//                            addMarker(latitude, longitude);
//                        } catch (Exception e) {
//                            Log.e("test", "Error parsing address", e);
//                        }
//                    } else {
//                        Log.e("test", "Response not successful or body is null");
//                    }
//                }
//
//
//                @Override
//                public void onFailure(Call<AddressResponse> call, Throwable t) {
//                    t.printStackTrace();
//                }
//            });
//        }

        try {
            // RecyclerView 초기화
            recyclerView = findViewById(R.id.ploggerRecyclerView);
            recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

            // 이전 액티비티에서 전달된 데이터 받기
            Intent intent = getIntent();
            ArrayList<RealtimePloggerItem> ploggingItems = (ArrayList<RealtimePloggerItem>) intent.getSerializableExtra("ploggingItems");
            trashcanVisible = intent.getBooleanExtra("trashcanVisible", true);

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
        fusedLocationClient.removeLocationUpdates(locationCallback);

    }

    private void addMarker(double latitude, double longitude) {

        // 마커 이미지 리사이즈
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inScaled = false; // 스케일링 비활성화
        Bitmap originalBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.trashcan, options);

        // 원하는 크기로 이미지 조정
        int newWidth = 30; // 새로운 너비 (픽셀 단위)
        int newHeight = 30; // 새로운 높이 (픽셀 단위)
        Bitmap resizedBitmap = Bitmap.createScaledBitmap(originalBitmap, newWidth, newHeight, false);

        LabelStyles styles = map.getLabelManager()
                .addLabelStyles(LabelStyles.from(LabelStyle.from(resizedBitmap).setZoomLevel(15)));
        LabelOptions label_options = LabelOptions.from(LatLng.from(latitude, longitude))
                .setStyles(styles);


        LabelLayer layer = map.getLabelManager().getLayer();

        Label trashLabel = layer.addLabel(label_options);
//        Log.d("trashMarker", "marker added");
    }

    private void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        } else {
            startLocationUpdates();
        }
    }

    @SuppressLint("MissingPermission")
    private void getStartLocation() {
        fusedLocationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY,null)
                .addOnSuccessListener(this, location -> {
                    if (location != null) {
                        startPosition = LatLng.from(location.getLatitude(), location.getLongitude());
                        mapView.start(readyCallback1, readyCallback2);
                    }
                });
    }

    @SuppressLint("MissingPermission")
    private void startLocationUpdates() {
        requestingLocationUpdates = true;
        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getStartLocation();
            } else {
                showPermissionDeniedDialog();
            }
        }
    }

    private void showPermissionDeniedDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("위치 권한 거부시 앱을 사용할 수 없습니다.")
                .setPositiveButton("권한 설정하러 가기", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        try {
                            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).setData(Uri.parse("package:" + getPackageName()));
                            startActivity(intent);
                        } catch (ActivityNotFoundException e) {
                            e.printStackTrace();
                            Intent intent = new Intent(Settings.ACTION_MANAGE_APPLICATIONS_SETTINGS);
                            startActivity(intent);
                        } finally {
                            finish();
                        }
                    }
                })
                .setNegativeButton("앱 종료하기", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                })
                .setCancelable(false)
                .show();
    }

//    private void startLocationUpdates() {
//        LocationRequest locationRequest = LocationRequest.create();
//        locationRequest.setInterval(2000); // 10초마다 위치 업데이트
//        locationRequest.setFastestInterval(2000); // 가장 빠른 위치 업데이트 간격
//        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
//
//        locationCallback = new LocationCallback() {
//            @Override
//            public void onLocationResult(LocationResult locationResult) {
//                if (locationResult == null) {
//                    return;
//                }
//
//                for (android.location.Location location : locationResult.getLocations()) {
//                    // 업데이트된 위치로 라벨 이동
//                    userLabel.moveTo(LatLng.from(location.getLatitude(), location.getLongitude()));
//                }
//            }
//        };
//
//        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            // TODO: Consider calling
//            //    ActivityCompat#requestPermissions
//            // here to request the missing permissions, and then overriding
//            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
//            //                                          int[] grantResults)
//            // to handle the case where the user grants the permission. See the documentation
//            // for ActivityCompat#requestPermissions for more details.
//            return;
//        }
//        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
//    }

    private void updateLocationOnMap(android.location.Location location) {
        LatLng userLatLng = LatLng.from(location.getLatitude(), location.getLongitude());

        // 사용자 위치 마커 추가
        addOrUpdateUserMarker(location.getLatitude(), location.getLongitude());

//        // 사용자 위치로 카메라 이동
//        map.moveCamera(CameraUpdateFactory.newCenterPosition(userLatLng, 15),
//                CameraAnimation.from(100));
    }

    private void addOrUpdateUserMarker(double latitude, double longitude) {
        LatLng userLatLng = LatLng.from(latitude, longitude);

        Bitmap originalBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.user_marker);

        // 원하는 크기로 이미지 조정
        int newWidth = 60; // 새로운 너비 (픽셀 단위)
        int newHeight = 60; // 새로운 높이 (픽셀 단위)
        Bitmap resizedBitmap = Bitmap.createScaledBitmap(originalBitmap, newWidth, newHeight, false);

//        // 기존 마커가 있으면 제거
//        if (userMarkers.containsKey(userMarkerKey)) {
//            LabelOptions existingMarker = userMarkers.get(userMarkerKey);
//            if (existingMarker != null) {
//                //기존마커제거!!!!!⭐️
////                labelLayer.removeLabel(existingMarker);
//            }
//        }

        // 새 마커 추가
        LabelOptions newMarker = LabelOptions.from(userLatLng)
                .setStyles(LabelStyles.from(LabelStyle.from(resizedBitmap).setZoomLevel(15)));


        userMarkers.put(userMarkerKey, newMarker);
        userLabel = labelLayer.addLabel(newMarker);

        //라벨의 위치가 변하더라도 항상 화면 중앙에 위치할 수 있도록 trackingManager를 통해 tracking을 시작합니다.
        TrackingManager trackingManager = map.getTrackingManager();
        trackingManager.startTracking(userLabel);
    }

}