package com.a2b2.plog;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.kakao.vectormap.camera.CameraPosition;
import com.kakao.vectormap.camera.CameraUpdateFactory;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.kakao.vectormap.KakaoMap;
import com.kakao.vectormap.KakaoMapReadyCallback;
import com.kakao.vectormap.LatLng;
import com.kakao.vectormap.MapLifeCycleCallback;
import com.kakao.vectormap.MapView;
import com.kakao.vectormap.label.Label;
import com.kakao.vectormap.label.LabelLayer;
import com.kakao.vectormap.label.LabelManager;
import com.kakao.vectormap.label.LabelOptions;
import com.kakao.vectormap.label.LabelStyle;
import com.kakao.vectormap.label.LabelStyles;

import org.apache.logging.log4j.Marker;

import java.util.List;
import java.util.Map;

public class RecyclingInfoActivity extends AppCompatActivity {

    private Button recycleInfoLinkBtn, depositInfoLinkBtn;
    private Button yesBtn;
    private MapView mapView;
    private FusedLocationProviderClient fusedLocationClient;
    private KakaoMap map;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recycling_info);

        yesBtn = findViewById(R.id.yesBtn);
        mapView = findViewById(R.id.map);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        mapView.start(new MapLifeCycleCallback() {
            @Override
            public void onMapDestroy() {
                // 지도 API가 정상적으로 종료될 때 호출됨
            }

            @Override
            public void onMapError(Exception error) {
                // 인증 실패 및 지도 사용 중 에러가 발생할 때 호출됨
                Log.e("개같이 멸망", "다시");
            }
        }, new KakaoMapReadyCallback() {
            @Override
            public void onMapReady(KakaoMap kakaoMap) {
                // 엑셀 파일에서 위치 정보를 가져옴

                map = kakaoMap;
                Map<String, Location> locationMap = RecycleExcelUtils.readRecyclingExcelFile(RecyclingInfoActivity.this, "recycle_latlong.xlsx");
                requestLocationPermission();

                // 위치 정보를 기반으로 마커 추가
                LabelManager labelManager = kakaoMap.getLabelManager();

                // Bitmap 리사이즈
                BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
                bitmapOptions.inScaled = false; // 스케일링 비활성화
                Bitmap originalBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.recyclemark_fin, bitmapOptions);

                // 원하는 크기로 이미지 조정
                int newWidth = 50; // 새로운 너비 (픽셀 단위)
                int newHeight = 70; // 새로운 높이 (픽셀 단위)
                Bitmap resizedBitmap = Bitmap.createScaledBitmap(originalBitmap, newWidth, newHeight, false);

                //LabelStyle labelStyle   = LabelStyle.from(resizedBitmap).setZoomLevel(11);
                LabelStyle labelStyle = LabelStyle.from(resizedBitmap).setZoomLevel(10);
                LabelStyles styles = labelManager.addLabelStyles(LabelStyles.from(labelStyle));

//                LabelStyles noHumanStyle = LabelStyles.from(LabelStyle.from(R.drawable.recyclemark_fin).setTextStyles(20, Color.BLACK));
//                LabelStyles noHumanStyles = LabelStyles.from((List<LabelStyle>) noHumanStyle);

                for (Map.Entry<String, Location> entry : locationMap.entrySet()) {
                    //String uniqueKey = entry.getKey();
                    Location location = entry.getValue();
                    //위도, 경도 순으로 받아와야하는데 위도에 경도 들어가있고, 경도에 위도 들어가있어서 걍 둘 자리 바꿈
                    LabelOptions labelOptions = LabelOptions.from(LatLng.from(location.getLongitude(), location.getLatitude()))
                            .setStyles(styles);

                    LabelLayer labelLayer = labelManager.getLayer();
                    Label label = labelLayer.addLabel(labelOptions);

                    Log.d("RecyclingInfoActivity", "Label added at: " + location.getLatitude() + ", " + location.getLongitude());
                }
            }@Override
            public LatLng getPosition() {
                return LatLng.from(33.26626855, 126.6088838);
            }
        });

        yesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RecyclingInfoActivity.this, HomeActivity.class);
                startActivity(intent);
                overridePendingTransition(0, 0);
                finish();
            }
        });

        recycleInfoLinkBtn = findViewById(R.id.recycleInfoLinkBtn);
        depositInfoLinkBtn = findViewById(R.id.depositInfoLinkBtn);

        recycleInfoLinkBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = "https://blisgo.com";
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(url));
                startActivity(intent);
            }
        });

        depositInfoLinkBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = "https://www.cosmo.or.kr/home/sub.do?menuNo=15";
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(url));
                startActivity(intent);
            }
        });
    }

    private void requestLocationPermission() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        } else {
            getCurrentLocation();
        }
    }

    private void getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        fusedLocationClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<android.location.Location>() {
            @Override
            public void onSuccess(android.location.Location location) {
                if (location != null) {
                    // 사용자의 현재 위치로 카메라를 이동
                    map.moveCamera(CameraUpdateFactory.newCenterPosition(
                            LatLng.from(location.getLatitude(),location.getLongitude()), 16));

                } else {
                    Toast.makeText(RecyclingInfoActivity.this, "현재 위치를 찾을 수 없습니다.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    // 권한 요청 결과 처리
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            getCurrentLocation();
        } else {
            Toast.makeText(this, "위치 권한이 필요합니다.", Toast.LENGTH_SHORT).show();
        }
    }
}