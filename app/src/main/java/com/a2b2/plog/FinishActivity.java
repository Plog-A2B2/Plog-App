package com.a2b2.plog;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.app.Activity;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.PixelCopy;
import android.view.Surface;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.Manifest;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;


import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.FirebaseApp;
import com.google.firebase.storage.UploadTask;
import com.kakao.vectormap.GestureType;
import com.kakao.vectormap.KakaoMap;
import com.kakao.vectormap.KakaoMapReadyCallback;
import com.kakao.vectormap.KakaoMapSdk;
import com.kakao.vectormap.LatLng;
import com.kakao.vectormap.MapLifeCycleCallback;
import com.kakao.vectormap.MapView;
import com.kakao.vectormap.camera.CameraPosition;
import com.kakao.vectormap.camera.CameraUpdateFactory;
import com.kakao.vectormap.graphics.gl.GLSurfaceView;
import com.kakao.vectormap.route.RouteLine;
import com.kakao.vectormap.route.RouteLineLayer;
import com.kakao.vectormap.route.RouteLineOptions;
import com.kakao.vectormap.route.RouteLineSegment;
import com.kakao.vectormap.route.RouteLineStyle;
import com.kakao.vectormap.shape.Polyline;
import com.kakao.vectormap.shape.PolylineOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;


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
    private Handler handler;
    private String trashType;
    private static final int REQUEST_CAMERA_PERMISSION = 200;

    static final int REQUEST_IMAGE_CAPTURE = 1;
    private String currentPhotoPath;
    private ImageView mapRouteSnapShot;

    private boolean isPhotoVisible = false;
    private ImageView switchViewButton;
    private ImageView photoImageView;
    int activityId;
    ArrayList<LatLng> runRoutePoints = new ArrayList<>();
    String message = null;
    String presignedUrl = null;
    private Bitmap kakao;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_finish);

        FirebaseApp.initializeApp(this);

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
        activityId = getIntent().getIntExtra("activityId", 0);
        ArrayList<com.a2b2.plog.LatLng> getRoutePoints = (ArrayList<com.a2b2.plog.LatLng>) intent.getSerializableExtra("runRoutePoints");

        for (com.a2b2.plog.LatLng point : getRoutePoints) {
            LatLng plogPoint = LatLng.from(point.getLatitude(), point.getLongitude());
            runRoutePoints.add(plogPoint);
        }

        UUID uuid = UserManager.getInstance().getUserId();

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

        handler = new Handler();
        // routeBtn 버튼 클릭 리스너 설정
        routeCreateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 팝업 다이얼로그 생성
                AlertDialog.Builder builder = new AlertDialog.Builder(FinishActivity.this);
                builder.setMessage("루트로 등록하시겠습니까?")
                        .setPositiveButton("네", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                                String url;
                                url = "http://15.164.152.246:8080/activitys/" + uuid + "/" + activityId + "/route";
                                // JSON 문자열을 구성하기 위한 StringBuilder 사용
                                String data = "";

                                // jsonData를 서버에 전송
                                Log.d("data", data);
                                new Thread(() -> {
                                    String result = httpPostBodyConnectionRouteCreate(url, data);
                                    if (handler == null) {
                                        handler = new Handler(Looper.getMainLooper());
                                    }
                                    handler.post(() -> Log.d("FinishActivity", "routeCreate completed: " + result));
                                }).start();

                                // "네" 버튼 클릭 시 알림창 표시
                                Toast.makeText(FinishActivity.this, "루트로 등록되었습니다", Toast.LENGTH_SHORT).show();
                                // 다이얼로그 닫기
                                dialog.dismiss();
                            }
                        })
                        .setNegativeButton("아니요", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // "아니요" 버튼 클릭 시 다이얼로그 닫기
                                dialog.dismiss();
                            }
                        });
                // 다이얼로그 보여주기
                AlertDialog alert = builder.create();
                alert.show();
            }
        });

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

//                fetchRoutePointsFromServer();
                drawRouteOnMap(runRoutePoints);

            }
        });
        downloadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Bitmap bitmap = captureScreen(); // 화면 캡처
//                saveImageToGallery(bitmap);
                if (mapView == null) {
                    Toast.makeText(getApplicationContext(), "지도가 준비되지 않았습니다.", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(!isPhotoVisible){
                    MapCapture.capture(FinishActivity.this, (GLSurfaceView) mapView.getSurfaceView(), new MapCapture.OnCaptureListener() {
                        @Override
                        public void onCaptured(boolean isSucceed, Bitmap bitmap, String fileName) {
                            if (isSucceed) {
                                kakao = bitmap; // 캡쳐된 Bitmap을 kakao 변수에 저장

                                Bitmap fullScreenBitmap = captureScreen();
                                Bitmap mapBitmap = kakao;
                                Bitmap combinedBitmap = combineBitmaps(fullScreenBitmap, mapBitmap);
                                if (combinedBitmap != null) {
                                    saveImageToGallery(combinedBitmap);
                                } else {
                                    Toast.makeText(getApplicationContext(), "Bitmap is null, cannot save.", Toast.LENGTH_SHORT).show();
                                }
                                // 여기서 kakao Bitmap을 원하는 대로 사용할 수 있습니다.
                            } else {
                                Toast.makeText(getApplicationContext(), "캡쳐에 실패하였습니다.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                } else {
                    Bitmap fullScreenBitmap = captureScreen();
                    saveImageToGallery(fullScreenBitmap);
                }
            }
        });


        ImageView cameraIcon = findViewById(R.id.camera);
        switchViewButton = findViewById(R.id.viewPhoto);
        photoImageView = findViewById(R.id.photoImageView);

        cameraIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestCameraPermission();
            }
        });

        switchViewButton.setOnClickListener(v -> toggleMapAndPhoto());

        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    String url = "http://15.164.152.246:8080/trash/" + uuid + "/" + activityId;

                    // JSON 데이터 생성
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("garbage", String.valueOf(trashCountMap.get("일반쓰레기")));
                    jsonObject.put("can", String.valueOf(trashCountMap.get("캔/고철류")));
                    jsonObject.put("plastic", String.valueOf(trashCountMap.get("플라스틱")));
                    jsonObject.put("paper", String.valueOf(trashCountMap.get("종이류")));
                    jsonObject.put("plastic_bag", String.valueOf(trashCountMap.get("비닐류")));
                    jsonObject.put("glass", String.valueOf(trashCountMap.get("유리류")));

                    File imageFile = (currentPhotoPath != null) ? new File(currentPhotoPath) : null;

                    // 새로운 스레드에서 네트워크 작업 실행
                    new Thread(() -> {
                        Log.d("보내는 데이터", jsonObject.toString());

                        String result = MultipartUtility.uploadFile(url, jsonObject.toString(), imageFile);
                        Log.d("서버 응답", result);

                        parseImageResponse(result);
                        uploadFileToS3(presignedUrl, imageFile);
                        // 처리 결과 확인 (네트워크 처리 결과를 메인 스레드에서 처리)
                        handler.post(() -> {

                            if (result.contains("success")) {
                                // 성공 시 다음 액티비티로 이동
                                Intent intent = new Intent(FinishActivity.this, RecyclingInfoActivity.class);
                                startActivity(intent);
                                overridePendingTransition(0, 0);
                                finish();
                            }
                        });
                    }).start();

                } catch (Exception e) {
                    Log.e("서버로 전송 실패", "Error while uploading data", e);
                }
            }
        });

    }
    private Bitmap captureScreen() {
        // 루트 뷰 가져오기
        View rootView = getWindow().getDecorView().getRootView();
        rootView.setDrawingCacheEnabled(true);
        rootView.buildDrawingCache();
        Bitmap bitmap = Bitmap.createBitmap(rootView.getDrawingCache());
        rootView.setDrawingCacheEnabled(false); // 캡처 후 해제
        return bitmap;
    }
    private Bitmap combineBitmaps(Bitmap background, Bitmap foreground) {
        // Null 체크
        if (background == null || foreground == null) {
            Log.e("CombineBitmaps", "One of the bitmaps is null!");
            return null;
        }

        // Recycled 상태 체크
        if (background.isRecycled() || foreground.isRecycled()) {
            Log.e("CombineBitmaps", "One of the bitmaps is already recycled!");
            return null;
        }

        Bitmap combined = Bitmap.createBitmap(background.getWidth(), background.getHeight(), background.getConfig());
        Canvas canvas = new Canvas(combined);

        // 배경 이미지 (화면 전체)
        canvas.drawBitmap(background, 0, 0, null);

        int left = mapView.getLeft();
        int top = mapView.getTop();

        canvas.drawBitmap(foreground, left, top, null); // 지도 이미지

        return combined;
    }

    private void saveImageToGallery(Bitmap bitmap) {
        if(bitmap == null){
            Log.e("Save Image", "Bitmap is null, cannot save image");
        } else{
            ContentValues values = new ContentValues();
            values.put(MediaStore.Images.Media.DISPLAY_NAME, "finish_activity_image_" + System.currentTimeMillis() + ".png");
            values.put(MediaStore.Images.Media.MIME_TYPE, "image/png");
            values.put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES); // 갤러리 저장 경로

            Uri uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            if (uri != null) {
                try {
                    OutputStream outputStream = getContentResolver().openOutputStream(uri);
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
                    if (outputStream != null) {
                        outputStream.close();
                        Toast.makeText(this, "이미지가 갤러리에 저장되었습니다.", Toast.LENGTH_SHORT).show();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(this, "이미지 저장에 실패했습니다.", Toast.LENGTH_SHORT).show();
                }
            }
        }

    }

    public void parseImageResponse(String jsonResponse) {
        // JSON 응답을 파싱
        JSONObject responseObject = null;
        try {
            responseObject = new JSONObject(jsonResponse);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        // message와 data의 presignedUrl을 추출

        try {
            message = responseObject.getString("message");
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        try {
            presignedUrl = responseObject.getJSONObject("data").getString("presignedUrl");
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        System.out.println("Message: " + message);
        System.out.println("Presigned URL: " + presignedUrl);
    }

    public void uploadFileToS3(String presignedUrl, File file) {
        try {
            URL url = new URL(presignedUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("PUT");
            connection.setDoOutput(true);
            connection.setRequestProperty("Content-Type", "image/jpeg"); // 파일 타입에 맞게 설정

            // 파일 데이터 전송
            try (InputStream fileInputStream = new FileInputStream(file)) {
                byte[] buffer = new byte[4096];
                int bytesRead;
                try (OutputStream outputStream = connection.getOutputStream()) {
                    while ((bytesRead = fileInputStream.read(buffer)) != -1) {
                        outputStream.write(buffer, 0, bytesRead);
                    }
                }
            }

            // 응답 코드 확인
            int responseCode = connection.getResponseCode();
            System.out.println("Response Code: " + responseCode);

            // 응답 처리 (필요 시)
            // InputStream responseStream = connection.getInputStream();
            // ... 응답 스트림 읽기

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public String httpPostBodyConnectionRouteCreate(String UrlData, String ParamData) {
        String responseData = "";
        BufferedReader br = null;

        try {
            URL url = new URL(UrlData);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("PATCH");
            conn.setRequestProperty("Content-Type", "application/json; utf-8");
            conn.setRequestProperty("Accept", "application/json");
            conn.setDoOutput(true);

            try (OutputStream os = conn.getOutputStream()) {
                byte[] request_data = ParamData.getBytes("utf-8");
                os.write(request_data);
            }

            conn.connect();

            br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
            StringBuilder sb = new StringBuilder();
            while ((responseData = br.readLine()) != null) {
                sb.append(responseData);
            }

            return sb.toString();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (br != null) {
                    br.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return responseData;
    }
    private void requestCameraPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {

            // 권한이 없으므로 요청합니다.
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA},
                    REQUEST_CAMERA_PERMISSION);
        } else {
            // 권한이 이미 부여되었습니다.
            dispatchTakePictureIntent();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // 권한이 부여되었습니다.
                dispatchTakePictureIntent();
            } else {
                // 권한이 거부되었습니다.
                Toast.makeText(this, "Camera permission is required to take pictures.", Toast.LENGTH_SHORT).show();
            }
        }
    }
    private void toggleMapAndPhoto() {
        if (isPhotoVisible) {
            // 사진이 보이고 있을 때 -> 맵을 보여줌
            photoImageView.setVisibility(View.GONE);
            mapView.setVisibility(View.VISIBLE);
        } else {
            // 맵이 보이고 있을 때 -> 사진을 보여줌
            if (currentPhotoPath == null || currentPhotoPath.isEmpty()) {
                // 사진이 없을 경우, 토스트 메시지 출력
                Snackbar.make(findViewById(android.R.id.content), "사진 촬영 버튼을 눌러 플로깅 인증 사진을 찍어보세요!", Snackbar.LENGTH_SHORT).show();

                return;
            } else {
                // 사진이 있을 경우, 사진 보여줌
                Bitmap bitmap = BitmapFactory.decodeFile(currentPhotoPath);
                photoImageView.setImageBitmap(bitmap);
                mapView.setVisibility(View.GONE);
                photoImageView.setVisibility(View.VISIBLE);
            }
        }
        isPhotoVisible = !isPhotoVisible;
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.a2b2.plog.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,
                ".jpg",
                storageDir
        );
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            File imgFile = new File(currentPhotoPath);
            if (imgFile.exists()) {
                Bitmap bitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());

                // 오른쪽으로 90도 회전
                Matrix matrix = new Matrix();
                matrix.postRotate(90);
                Bitmap rotatedBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);

                photoImageView.setImageBitmap(rotatedBitmap);
                currentPhotoPath = imgFile.getAbsolutePath();

                photoImageView.setVisibility(View.VISIBLE);
                mapView.setVisibility(View.GONE);
                isPhotoVisible = true; // 사진이 보이도록 상태 업데이트

                // 원본 이미지를 회전된 이미지로 대체
                saveRotatedImage(rotatedBitmap);
            }
        }
    }

    private void saveRotatedImage(Bitmap bitmap) {
        FileOutputStream out = null;
        try {
            File file = new File(currentPhotoPath);
            out = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
//            uploadImageToFirebase(file);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void drawRouteOnMap(ArrayList<LatLng> routePoints) {
        if (kakaoMap != null) {

            RouteLineStyle style = RouteLineStyle.from(FinishActivity.this,
                    R.style.SimpleRouteLineStyle);
            RouteLineOptions options = RouteLineOptions.from(
                    Arrays.asList(RouteLineSegment.from(routePoints, style)));

            routeLine = layer.addRouteLine(options);
            kakaoMap.moveCamera(CameraUpdateFactory.newCenterPosition(
                    LatLng.from(routePoints.get(0).latitude,routePoints.get(0).longitude), 16));

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

        String responseString = returnData;
        try {
            // JSON 문자열을 JSONObject로 변환
            JSONObject jsonResponse = new JSONObject(responseString);

            // "message" 필드의 값을 추출
            String message = jsonResponse.getString("message");
            Log.d("반환값", message);
            returnData = message;
            // 추출한 값 사용
            System.out.println("서버 응답 메시지: " + message);

        } catch (Exception e) {
            e.printStackTrace();
        }

        Log.d("찐막", returnData);



        return returnData; // 네트워크 요청 결과를 반환
    }
    public void seeNetworkResult(String result) {
        // 네트워크 작업 완료 후
        Log.d(result, "network");
    }

}