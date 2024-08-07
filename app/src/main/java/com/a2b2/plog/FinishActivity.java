package com.a2b2.plog;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.kakao.vectormap.GestureType;
import com.kakao.vectormap.KakaoMap;
import com.kakao.vectormap.KakaoMapReadyCallback;
import com.kakao.vectormap.KakaoMapSdk;
import com.kakao.vectormap.LatLng;
import com.kakao.vectormap.MapLifeCycleCallback;
import com.kakao.vectormap.MapView;
import com.kakao.vectormap.camera.CameraPosition;
import com.kakao.vectormap.camera.CameraUpdateFactory;
import com.kakao.vectormap.route.RouteLine;
import com.kakao.vectormap.route.RouteLineLayer;
import com.kakao.vectormap.route.RouteLineOptions;
import com.kakao.vectormap.route.RouteLineSegment;
import com.kakao.vectormap.route.RouteLineStyle;
import com.kakao.vectormap.shape.Polyline;
import com.kakao.vectormap.shape.PolylineOptions;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class FinishActivity extends AppCompatActivity {
    HashMap<String, Integer> trashCountMap = new HashMap<>();

    private ImageView downloadBtn, nextBtn, routeCreateBtn;
    private TextView todayDateTextView, totalTrashAmountTextView;
    private int totalTrashAmount = 0;
    private TextView tvFinalTime, tvFinalDistance;

    private MapView mapView;
    private KakaoMap kakaoMap;
    private static final String KAKAO_API_KEY = "1b96fc67568f72bcc29317e838ad740f";
    private List<LatLng> routePoints = new ArrayList<>();
    private RouteLine routeLine;
    private RouteLineLayer layer;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_finish);

        Intent intent = getIntent();
        trashCountMap = (HashMap<String, Integer>) intent.getSerializableExtra("trashCountMap");

        for (String trashType : trashCountMap.keySet()) {
            int count = trashCountMap.get(trashType);
            Log.d("PloggingActivity", trashType + ": " + count);
        }
        tvFinalTime = findViewById(R.id.timeTextView);
        tvFinalDistance = findViewById(R.id.distanceTextView);

        String time = getIntent().getStringExtra("time");
        String distance = getIntent().getStringExtra("distance");

        tvFinalTime.setText(time);
        tvFinalDistance.setText(distance);

        todayDateTextView = findViewById(R.id.todayDateTextView);
        totalTrashAmountTextView = findViewById(R.id.totalTrashAmountTextView);
        // 현재 날짜를 yyyy.MM.dd(E) 형식으로 설정
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd(E)", Locale.getDefault());
        String currentDate = dateFormat.format(new Date());
        todayDateTextView.setText(currentDate);


        downloadBtn = findViewById(R.id.downloadBtn);
        nextBtn = findViewById(R.id.nextBtn);
        routeCreateBtn = findViewById(R.id.routeBtn);

        LinearLayout trashContainer1 = findViewById(R.id.trashContainer1);
        LinearLayout trashContainer2 = findViewById(R.id.trashContainer2);
        String[] trashTypes1 = {"일반쓰레기", "플라스틱", "종이류"};
        String[] trashTypes2 = {"캔/고철류", "유리류", "비닐류"};
        LayoutInflater inflater = LayoutInflater.from(this);

        for (String trashType : trashTypes1) {
            View itemView = inflater.inflate(R.layout.item_trash_finish, trashContainer1, false);
            TextView tvTrashType = itemView.findViewById(R.id.trashType);
            tvTrashType.setText(trashType);

            TextView tvTrashAmount = itemView.findViewById(R.id.trashAmount);

            Integer count = trashCountMap.get(trashType);
            if (count == null) {
                count = 0; // 기본값 설정
            }
            Log.d("finishActivity", trashType + ": " + count);
            tvTrashAmount.setText(count + "개");
            totalTrashAmount += count;

            trashContainer1.addView(itemView);
        }
        for (String trashType : trashTypes2) {
            View itemView = inflater.inflate(R.layout.item_trash_finish, trashContainer2, false);
            TextView tvTrashType = itemView.findViewById(R.id.trashType);
            tvTrashType.setText(trashType);

            TextView tvTrashAmount = itemView.findViewById(R.id.trashAmount);

            Integer count = trashCountMap.get(trashType);
            if (count == null) {
                count = 0; // 기본값 설정
            }
            Log.d("finishActivity", trashType + ": " + count);
            tvTrashAmount.setText(count + "개");
            totalTrashAmount += count;

            trashContainer2.addView(itemView);
        }
        totalTrashAmountTextView.setText("Total : " + totalTrashAmount + "개");

//        // trashCountMap을 사용하여 필요한 작업을 수행
//        if (trashCountMap != null) {
//            for (String key : trashCountMap.keySet()) {
//                Integer count = trashCountMap.get(key);
//                // count를 사용하여 필요한 작업 수행
//            }
//        }
        mapView = findViewById(R.id.map);
        mapView.start(new MapLifeCycleCallback() {
            @Override
            public void onMapDestroy() {

            }

            @Override
            public void onMapError(Exception e) {

            }
        }, new KakaoMapReadyCallback() {

            @Override
            public void onMapReady(KakaoMap map) {
                kakaoMap = map;
                layer = kakaoMap.getRouteLineManager().getLayer();

                fetchRoutePointsFromServer();
            }
        });

        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 분리수거 확인 화면으로 이동
                Intent intent = new Intent(FinishActivity.this, RecyclingInfoActivity.class);
                startActivity(intent);
                overridePendingTransition(0, 0);
                finish();
            }
        });


    }

    private void fetchRoutePointsFromServer() {
        // 실제 서버에서 데이터를 받아오는 코드로 대체해야 합니다.
        // 여기서는 더미 데이터를 사용합니다.
        List<LatLng> fetchedPoints = getDummyRoutePoints();

        if (fetchedPoints != null && !fetchedPoints.isEmpty()) {
            routePoints.addAll(fetchedPoints);
            drawRouteOnMap(routePoints);
        } else {
            Toast.makeText(this, "경로 데이터를 받아오는 데 실패했습니다.", Toast.LENGTH_SHORT).show();
        }
    }

    private void drawRouteOnMap(List<LatLng> routePoints) {
        if (kakaoMap != null) {
//            PolylineOptions polylineOptions = new PolylineOptions()
//                    .addAll(routePoints)
//                    .color(0xFF0000FF) // 선 색상 (파란색)
//                    .width(5); // 선 두께

            RouteLineStyle style = RouteLineStyle.from(getBaseContext(),
                    R.style.SimpleRouteLineStyle);
            RouteLineOptions options = RouteLineOptions.from(
                    Arrays.asList(RouteLineSegment.from(routePoints, style)));


//            Polyline polyline = kakaoMap.addPolyline(polylineOptions);

            routeLine = layer.addRouteLine(options);
            kakaoMap.moveCamera(CameraUpdateFactory.newCenterPosition(
                    LatLng.from(37.338549743448546,127.09368565409382), 16));

        }
    }

    // 더미 데이터 생성
    private List<LatLng> getDummyRoutePoints() {
        List<LatLng> routePoints = Arrays.asList(
                LatLng.from(37.338549743448546,127.09368565409382),
                LatLng.from(37.33856778190988,127.093663107081),
                LatLng.from(37.33860015104726,127.09374891110167),
                LatLng.from(37.33866855056389,127.09384830168884),
                LatLng.from(37.33881977657985,127.09403355969684),
                LatLng.from(37.33881977657985,127.09403355969684),
                LatLng.from(37.338798130341964,127.09406061609467),
                LatLng.from(37.33874386013671,127.0943223542757),
                LatLng.from(37.33869695980336,127.09438097621258),
                LatLng.from(37.337824766739104,127.09437537101812),
                LatLng.from(37.33770221229771,127.09439327300674),
                LatLng.from(37.33770221229771,127.09439327300674),
                LatLng.from(37.3376974871616,127.09578804101909),
                LatLng.from(37.3376974871616,127.09578804101909),
                LatLng.from(37.336219787367654,127.0957997057665),
                LatLng.from(37.33621663148788,127.09524451138867),
                LatLng.from(37.336234684781665,127.09520391048807),
                LatLng.from(37.336234684781665,127.09520391048807),
                LatLng.from(37.33645497790997,127.09465351015245));

        return routePoints;
    }

}