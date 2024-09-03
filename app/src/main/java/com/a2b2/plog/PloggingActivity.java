package com.a2b2.plog;

import static androidx.fragment.app.FragmentManager.TAG;

import android.graphics.Point;
import android.location.Location;

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
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
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
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.gson.Gson;
import com.kakao.vectormap.GestureType;
import com.kakao.vectormap.KakaoMap;
import com.kakao.vectormap.KakaoMapReadyCallback;
import com.kakao.vectormap.KakaoMapSdk;
import com.kakao.vectormap.LatLng;
import com.kakao.vectormap.MapLifeCycleCallback;
import com.kakao.vectormap.MapView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.kakao.vectormap.camera.CameraPosition;
import com.kakao.vectormap.label.Label;
import com.kakao.vectormap.label.LabelLayer;
import com.kakao.vectormap.label.LabelOptions;
import com.kakao.vectormap.label.LabelStyle;
import com.kakao.vectormap.label.LabelStyles;

import com.kakao.vectormap.label.TrackingManager;
import com.kakao.vectormap.shape.DimScreenLayer;

import org.json.JSONObject;


public class PloggingActivity extends AppCompatActivity {


    private ImageView backBtn;
    private ImageView trashReport;
    private RecyclerView recyclerView;
    private PloggerAdapter ploggerAdapter;
    private List<RealtimePloggerItem> ploggingItems;
    private BottomSheetBehavior<LinearLayout> bottomSheetBehavior;
    private ImageView topBtn;
    private MapView mapView;
    private static final String KAKAO_API_KEY = "1b96fc67568f72bcc29317e838ad740f";

    private TextView distanceTextView, timeTextView;
    private String time;
    private android.location.Location lastLocation;
    private double distance = 0.0;
    private long startTime = 0L;
    private Handler handler = new Handler();
    private Runnable updateTimeTask = new Runnable() {
        @Override
        public void run() {
            final long start = startTime;
            long millis = SystemClock.uptimeMillis() - start;
            int seconds = (int) (millis / 1000);
            int minutes = seconds / 60;
            int hours = minutes / 60;
            seconds = seconds % 60;
            minutes = minutes % 60;
            timeTextView.setText(String.format("%02d:%02d:%02d", hours, minutes, seconds));
            handler.postDelayed(this, 1000);
        }
    };
    Handler locationHandler;
    private Runnable locationRunnable;
    private KakaoMap map;
    private LabelLayer labelLayer;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private FusedLocationProviderClient fusedLocationClient;
    private LocationCallback locationCallback;

    private final String[] locationPermissions = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};
    private List<LatLng> routePoints = new ArrayList<>(); // 경로 점들을 저장할 리스트

    private HashMap<String, LabelOptions> userMarkers = new HashMap<>();
    private String userMarkerKey = "user_location_marker";

    // 위치 업데이트가 요청된 적 있는지 여부를 저장합니다.
    private boolean requestingLocationUpdates = false;
    private LocationRequest locationRequest;
    Label userLabel;
    private LatLng startPosition = null;
    private boolean isActivityDestroyed = false;

    private ImageView userCurrentLocation;
//    public String[] trashTypes = {"종이류","유리류","일반쓰레기", "플라스틱",  "캔/고철류",  "비닐류"};
//    public String trashType;

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
            Map<String, com.a2b2.plog.Location> locationMap = ExcelUtils.readExcelFile(PloggingActivity.this, "trashcan_location.xlsx");

//             위치 정보를 기반으로 마커 추가
            for (Map.Entry<String, com.a2b2.plog.Location> entry : locationMap.entrySet()) {
                com.a2b2.plog.Location location = entry.getValue();
                //Log.d("trashcanVisible", String.valueOf(trashcanVisible));
                if(trashcanVisible == true) {
                    addMarker(location.getLatitude(), location.getLongitude());
                }
            }
// 현재 사용자 위치를 중심으로 마커를 필터링하여 추가
//            addMarkersNearby(locationMap, startPosition, 5000); // 5000미터 (5킬로미터) 반경
            setupTracking(startPosition);

//            loadMarkersInView(locationMap);

            // 지도 이동 이벤트 설정
            map.setOnCameraMoveEndListener(new KakaoMap.OnCameraMoveEndListener() {
                @Override
                public void onCameraMoveEnd(@NonNull KakaoMap kakaoMap,
                                            @NonNull CameraPosition cameraPosition,
                                            @NonNull GestureType gestureType) {
//                    // 카메라 위치에 따라 마커를 동적으로 로드
//                    LatLng newCameraPosition = cameraPosition.getPosition();
//                    addMarkersNearby(locationMap, newCameraPosition, 30000);

                }
            });
            Bitmap originalBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.user_marker);

            // 원하는 크기로 이미지 조정
            int newWidth = 70; // 새로운 너비 (픽셀 단위)
            int newHeight = 70; // 새로운 높이 (픽셀 단위)
            Bitmap resizedBitmap = Bitmap.createScaledBitmap(originalBitmap, newWidth, newHeight, false);

            userLabel = labelLayer.addLabel(LabelOptions.from("userLabel", startPosition)
                    .setStyles(LabelStyle.from(resizedBitmap).setAnchorPoint(0.5f, 0.5f))
                    .setRank(1));

            isUserLabelInitialized = true;

            trackingManager = map.getTrackingManager();
            trackingManager.startTracking(userLabel);
            startLocationUpdates();


            // 지도 이동 이벤트 설정
            map.setOnCameraMoveEndListener(new KakaoMap.OnCameraMoveEndListener() {
                @Override
                public void onCameraMoveEnd(@NonNull KakaoMap kakaoMap,
                                            @NonNull CameraPosition cameraPosition,
                                            @NonNull GestureType gestureType) {
// 카메라 위치에 따라 마커를 동적으로 로드
//                    LatLng newCameraPosition = cameraPosition.getPosition();
//                    addMarkersNearby(locationMap, newCameraPosition, 30000);
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

    private boolean isUserLabelInitialized = false;

    private StompClient stompClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plogging);

        stompClient = new StompClient();
        stompClient.connect("ws://15.164.152.246:8080/ws");

//        UUID uuid = UUID.fromString("36994BC9-E1BD-426B-AA36-99A1B31E9980");
//        UUID uuid = UserManager.getInstance().getUserId();

        locationHandler = new Handler(Looper.getMainLooper());

        trashReport = findViewById(R.id.trashReport);
        trashReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(PloggingActivity.this);
               // builder.setTitle("확인");
                builder.setMessage("🗑️쓰레기통 위치 신고하기");
                builder.setPositiveButton("쓰레기통이 없어요!", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //쓰레기통 없다는 거 백으로 전송하기(유유아이디, 현 위치 위도경도, 쓰레기통 없음
                        Toast.makeText(PloggingActivity.this, "신고해주셔서 감사합니다", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    }
                });
                builder.setNegativeButton("쓰레기통 있어요!", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(PloggingActivity.this, "신고해주셔서 감사합니다", Toast.LENGTH_SHORT).show();
                    }
                });
                builder.setNeutralButton("취소", null);
                builder.create().show();


            }
        });

        KakaoMapSdk.init(this, "1b96fc67568f72bcc29317e838ad740f");
        mapView = findViewById(R.id.map);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        if (ContextCompat.checkSelfPermission(this, locationPermissions[0]) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this, locationPermissions[1]) == PackageManager.PERMISSION_GRANTED) {
            getStartLocation();
        } else {
            ActivityCompat.requestPermissions(this, locationPermissions, LOCATION_PERMISSION_REQUEST_CODE);
        }

        locationRequest = new LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 2000L).build();
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {

                if (isActivityDestroyed) {
                    Log.d("PloggingActivity", "Activity is destroyed, ignoring location updates.");
                    return; // 액티비티가 종료된 후에는 콜백 무시
                }

                for (android.location.Location location : locationResult.getLocations()) {

                    startLocationSendingTask(location);
                    Log.d("onLocationResult", "onLocationResult executed");

                    if (isUserLabelInitialized && userLabel != null) {
                        userLabel.moveTo(LatLng.from(location.getLatitude(), location.getLongitude()));
                    } else {
                        Log.d("PloggingActivity", "userLabel is not initialized yet.");
                    }
                    // 경로 점 추가
                    routePoints.add(LatLng.from(location.getLatitude(), location.getLongitude()));


                }
            }
        };

        userCurrentLocation = findViewById(R.id.current_location);

        userCurrentLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                map.moveCamera(CameraUpdateFactory.newCenterPosition(userLatLng, 15),
//                CameraAnimation.from(100));
                trackingManager.startTracking(userLabel);
            }
        });

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
            distanceTextView = findViewById(R.id.distanceTextView);
            timeTextView = findViewById(R.id.timeTextView);


            // 쓰레기 종류와 개수를 저장할 변수 초기화
            final HashMap<String, Integer> trashCountMap = new HashMap<>();

            LinearLayout trashContainer = findViewById(R.id.trashContainer);
            String[] trashTypes = {"종이류","유리류","일반쓰레기", "플라스틱",  "캔/고철류",  "비닐류"};
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

                        //워치로 값 보내기
                        try {
                            // JSON 객체 생성
                            JSONObject jsonObject = new JSONObject();
                            jsonObject.put("\""+trashType+"\"", count);
                            Log.d(trashType, Integer.toString(count));


                            // JSON을 문자열로 변환
                            String jsonString = jsonObject.toString();

                            // PutDataMapRequest를 사용하여 데이터 전송
                            PutDataMapRequest putDataMapReq = PutDataMapRequest.create("/path/to/data");
                            putDataMapReq.getDataMap().putString("json_data", jsonString);
                            PutDataRequest putDataReq = putDataMapReq.asPutDataRequest();

                            Wearable.getDataClient(getApplicationContext()).putDataItem(putDataReq)
                                    .addOnSuccessListener(dataItem -> Log.d("MobileApp", "JSON data sent successfully"))
                                    .addOnFailureListener(e -> Log.e("MobileApp", "Failed to send JSON data", e));
                        } catch (Exception e) {
                            Log.e("MobileApp", "Failed to create JSON data", e);
                        }


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
            startPlogging();
            startLocationUpdates();

            setupLocationCallback();

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

//                    endPlogging();

//                    stopPlogging();
//                    stopLocationUpdates();

                    ///////

                    // 결과 화면으로 이동
                    Intent intent = new Intent(PloggingActivity.this, FinishActivity.class);
                    intent.putExtra("trashCountMap", trashCountMap);
                    intent.putExtra("time", timeTextView.getText().toString());
                    intent.putExtra("distance", distanceTextView.getText().toString());
                    startActivity(intent);
                    overridePendingTransition(0, 0);
                    finish();

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

    private void addMarkersNearby(Map<String, com.a2b2.plog.Location> locationMap, LatLng centerPosition, int radius) {
        Log.d("addMarkersNearby", "addMarkersNearby executed");


        for (Map.Entry<String, com.a2b2.plog.Location> entry : locationMap.entrySet()) {
            com.a2b2.plog.Location location = entry.getValue();

            // 카메라 위치와 마커 간 거리 계산
            float[] distance = new float[1];
            Location.distanceBetween(centerPosition.latitude, centerPosition.longitude,
                    location.getLatitude(), location.getLongitude(), distance);
            Log.d("addMarkersNearby", "distance: " + distance[0]);

            // 거리 계산 및 반경 확인
            if (distance[0] <= radius) {
                Log.d("addMarkersNearby", "add marker");

                addMarker(location.getLatitude(), location.getLongitude());
            }
        }
    }

    private void setupTracking(LatLng startPosition) {
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
    }

    private void startLocationSendingTask(android.location.Location location) {

        Log.d("startLocationSendingTask", "startLocationSendingTask executed");


        if (locationHandler == null) {
            Log.e("PloggingActivity", "Handler is null, cannot post runnable");
            return; // Handler가 null이면 메서드를 종료
        }

        // 기존 Runnable이 존재하면 제거
        if (locationRunnable != null) {
            locationHandler.removeCallbacks(locationRunnable);
        }


        locationRunnable = new Runnable() {
            @Override
            public void run() {
                if (location != null) {
//                    UUID uuid  =UUID.fromString("11");
                    // 현재 위치를 서버에 전송
                    sendLocationToServer(location.getLatitude(), location.getLongitude(), UserManager.getInstance().getUserId());


                    Log.d("startLocationSendingTask", "sendLocationToServer called");
                }
                locationHandler.postDelayed(this, 5000); // 5초마다 반복

            }
        };
        locationHandler.post(locationRunnable);
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        isActivityDestroyed = true;


        Log.d("PloggingActivity", "onDestroy executed");

        // Location updates 제거
        if (fusedLocationClient != null && locationCallback != null) {
            fusedLocationClient.removeLocationUpdates(locationCallback);
        }

        // Handler와 Runnable 정리
        if (locationHandler != null) {
            if (locationRunnable != null) {
                locationHandler.removeCallbacks(locationRunnable);
            }
            locationHandler = null;
        }
        locationRunnable = null;
        locationCallback = null;


        // Debugging
        if (locationRunnable == null) {
            Log.d("onDestroy", "locationRunnable is null");
        } else {
            Log.d("onDestroy", "locationRunnable is not null");
        }

        // Confirm handler and callback are cleaned up
        if (locationHandler == null) {
            Log.d("onDestroy", "locationHandler is null");
        } else {
            Log.d("onDestroy", "locationHandler is not null");
        }

        if (userLabel != null) {
            // UserLabel 관련 리소스 정리 (필요한 경우)
            userLabel = null;
        }
    }

    private void sendLocationToServer(double latitude, double longitude, UUID uuid) {

        // 위도, 경도, UUID 값을 가진 DTO 객체 생성
        LocationDTO locationDTO = new LocationDTO(latitude, longitude, UUID.fromString("42F57079-2DF3-416D-A22D-6C5AAF1D4A78"));

        // DTO 객체를 JSON 문자열로 변환
        Gson gson = new Gson();
        String message = gson.toJson(locationDTO);

        // 메시지 전송
        stompClient.sendMessage("/app/location", message);
        Log.d("보내는 값", message);


        // 메시지 구독
        stompClient.subscribe("/topic/location");

        Log.d("sendLocationToServer", "Location sent to server: Lat: " + latitude + ", Lng: " + longitude);
//        Log.d("sendLocationToServer", "user uuid: " + uuid);
        Log.d("sendLocationToServer", "user uuid: " + UUID.fromString("42F57079-2DF3-416D-A22D-6C5AAF1D4A78"));
    }


    private void startPlogging() {
        startTime = SystemClock.uptimeMillis();
        handler.postDelayed(updateTimeTask, 1000);
    }

    private void stopPlogging() {
        handler.removeCallbacks(updateTimeTask);
        locationHandler.removeCallbacks(locationRunnable);

    }

    private void setupLocationCallback() {
        if (locationHandler == null) {
            Log.e("PloggingActivity", "Handler is null, cannot post runnable");
            return; // Handler가 null이면 메서드를 종료
        }

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                if (isActivityDestroyed) {
                    Log.d("PloggingActivity", "Activity is destroyed, ignoring location updates.");
                    return; // 액티비티가 종료된 후에는 콜백 무시
                }
                if (locationResult == null) {
                    return;
                }
                for (android.location.Location location : locationResult.getLocations()) {
                    if (lastLocation != null) {
                        distance += lastLocation.distanceTo(location);
                    }
                    lastLocation = location;
                    distanceTextView.setText(String.format("%.2f KM", distance / 1000));
                }
            }
        };
    }

//    private void startLocationUpdates() {
//        LocationRequest locationRequest = LocationRequest.create();
//        locationRequest.setInterval(10000); // 10초마다 업데이트
//        locationRequest.setFastestInterval(5000); // 5초마다 업데이트
//        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
//
//        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);
//    }

    private void stopLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(locationCallback);
        if (locationRunnable != null) {
            locationHandler.removeCallbacks(locationRunnable);
        }
    }

    private void endPlogging() {
        stopPlogging();
        stopLocationUpdates();
        Intent intent = new Intent(PloggingActivity.this, FinishActivity.class);
        intent.putExtra("time", timeTextView.getText().toString());
        intent.putExtra("distance", distanceTextView.getText().toString());
        startActivity(intent);
        finish();
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

//    private void addOrUpdateUserMarker(double latitude, double longitude) {
//        LatLng userLatLng = LatLng.from(latitude, longitude);
//
//        Bitmap originalBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.user_marker);
//
//        // 원하는 크기로 이미지 조정
//        int newWidth = 60; // 새로운 너비 (픽셀 단위)
//        int newHeight = 60; // 새로운 높이 (픽셀 단위)
//        Bitmap resizedBitmap = Bitmap.createScaledBitmap(originalBitmap, newWidth, newHeight, false);
//
//        // 새 마커 추가
//        LabelOptions newMarker = LabelOptions.from(userLatLng)
//                .setStyles(LabelStyles.from(LabelStyle.from(resizedBitmap).setZoomLevel(15)));
//
//
//        userMarkers.put(userMarkerKey, newMarker);
//        userLabel = labelLayer.addLabel(newMarker);
//
//        //라벨의 위치가 변하더라도 항상 화면 중앙에 위치할 수 있도록 trackingManager를 통해 tracking을 시작합니다.
//        TrackingManager trackingManager = map.getTrackingManager();
//        trackingManager.startTracking(userLabel);
//    }

    private void sendJsonData() {

    }
    public String httpPostBodyConnection(String UrlData, String ParamData) {
        // 이전과 동일한 네트워크 연결 코드를 그대로 사용합니다.
        // 백그라운드 스레드에서 실행되기 때문에 메인 스레드에서는 문제가 없습니다.

        String totalUrl = "";
        totalUrl = UrlData.trim().toString();

        //http 통신을 하기위한 객체 선언 실시
        URL url = null;
        HttpURLConnection conn = null;

        //http 통신 요청 후 응답 받은 데이터를 담기 위한 변수
        String responseData = "";
        BufferedReader br = null;
        StringBuffer sb = null;

        //메소드 호출 결과값을 반환하기 위한 변수
        String returnData = "";


        try {
            //파라미터로 들어온 url을 사용해 connection 실시
            url = null;
            url = new URL(totalUrl);
            conn = null;
            conn = (HttpURLConnection) url.openConnection();

            //http 요청에 필요한 타입 정의 실시
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json; utf-8"); //post body json으로 던지기 위함
            conn.setRequestProperty("Accept", "application/json");
            conn.setDoOutput(true); //OutputStream을 사용해서 post body 데이터 전송
            try (OutputStream os = conn.getOutputStream()) {
                byte request_data[] = ParamData.getBytes("utf-8");
                Log.d("TAGGG",request_data.toString());
                os.write(request_data);
                //os.close();
            } catch (Exception e) {
                Log.d("TAG3","여기다");
                e.printStackTrace();
            }

            //http 요청 실시
            conn.connect();
            System.out.println("http 요청 방식 : " + "POST BODY JSON");
            System.out.println("http 요청 타입 : " + "application/json");
            System.out.println("http 요청 주소 : " + UrlData);
            System.out.println("http 요청 데이터 : " + ParamData);
            System.out.println("");

            //http 요청 후 응답 받은 데이터를 버퍼에 쌓는다
            br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
            sb = new StringBuffer();
            while ((responseData = br.readLine()) != null) {
                sb.append(responseData); //StringBuffer에 응답받은 데이터 순차적으로 저장 실시
            }

            //메소드 호출 완료 시 반환하는 변수에 버퍼 데이터 삽입 실시
            returnData = sb.toString();
            Log.d("TAG2", returnData);
            //http 요청 응답 코드 확인 실시
            String responseCode = String.valueOf(conn.getResponseCode());
            System.out.println("http 응답 코드 : " + responseCode);
            System.out.println("http 응답 데이터 : " + returnData);

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            //http 요청 및 응답 완료 후 BufferedReader를 닫아줍니다
            try {
                if (br != null) {
                    br.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return returnData; // 네트워크 요청 결과를 반환
    }
    public void seeNetworkResult(String result) {
        // 네트워크 작업 완료 후
        Log.d(result, "network");
    }

}