package com.a2b2.plog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.kakao.vectormap.KakaoMap;
import com.kakao.vectormap.KakaoMapReadyCallback;
import com.kakao.vectormap.LatLng;
import com.kakao.vectormap.MapLifeCycleCallback;
import com.kakao.vectormap.MapView;
import com.kakao.vectormap.camera.CameraUpdateFactory;
import com.kakao.vectormap.route.RouteLine;
import com.kakao.vectormap.route.RouteLineLayer;
import com.kakao.vectormap.route.RouteLineOptions;
import com.kakao.vectormap.route.RouteLineSegment;
import com.kakao.vectormap.route.RouteLineStyle;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;


public class RouteDialogFragment extends DialogFragment {

    private static final String ARG_ROUTE = "route";
    private Route route;
    private boolean isRouteSelected = false;
    private SharedPreferencesHelper prefsHelper;


    private KakaoMap kakaoMap;
    private RouteLineLayer layer;
    private List<LatLng> routePoints = new ArrayList<>();
    private RouteLine routeLine;
    MapView mapView;
    private Route selectedRoute;
    String url;
    Handler handler = new Handler();
    String result;

    int routeId;
    public static RouteDialogFragment newInstance(Route route) {
        RouteDialogFragment fragment = new RouteDialogFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_ROUTE, route);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            route = (Route) getArguments().getSerializable(ARG_ROUTE);
        }
        prefsHelper = new SharedPreferencesHelper(requireContext()); // Initialize SharedPreferencesHelper

    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_route, null);

        TextView textViewRouteDetails = view.findViewById(R.id.textViewRouteDetails);
        Button buttonSelect = view.findViewById(R.id.selectBtn);
        Button buttonClose = view.findViewById(R.id.closeBtn);

        textViewRouteDetails.setText(route.getOrigin() + " -> " + route.getDestination() + "\n" + route.getDistance() + ", " + route.getTime());

//        if (prefsHelper.getRoute() != null) {
//            Log.d("RouteDialogFragment", String.valueOf(prefsHelper.getRoute().equals(route)));
//            Log.d("RouteDialogFragment", prefsHelper.getRoute().getId());
//            Log.d("RouteDialogFragment", route.getId());
//        } else {
//            Log.d("RouteDialogFragment", "prefsHelper is null");
//        }

        if (prefsHelper.getRoute() != null && prefsHelper.getRoute().getId() == route.getId()) {
            isRouteSelected = true;
            buttonSelect.setText("선택 취소");
            buttonSelect.setBackground(getActivity().getDrawable(R.drawable.round_rectangle_realgreen));
        }

        buttonSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isRouteSelected = !isRouteSelected;
                buttonSelect.setText(isRouteSelected ? "선택 취소" : "루트 선택");
                if(isRouteSelected) {
                    buttonSelect.setBackground(getActivity().getDrawable(R.drawable.round_rectangle_realgreen));
                    prefsHelper.saveRoute(route);

                } else {
                    buttonSelect.setBackground(getActivity().getDrawable(R.drawable.round_rectangle_realblue));
                }
            }
        });

        buttonClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        // MapView 초기화 및 경로 표시
        mapView = view.findViewById(R.id.map);
        mapView.start(new MapLifeCycleCallback() {
            @Override
            public void onMapDestroy() {}

            @Override
            public void onMapError(Exception e) {}
        }, new KakaoMapReadyCallback() {
            @Override
            public void onMapReady(KakaoMap map) {
                kakaoMap = map;
                layer = kakaoMap.getRouteLineManager().getLayer();
//                fetchRoutePointsFromServer();
                getRoutePoints();
            }
        });


        builder.setView(view);
        return builder.create();
    }

//    private void selectRoute(Route route) {
//        // 선택된 루트를 저장하는 로직
//
//        // Route 객체 생성 및 저장
//        route = new Route();
//        route.setStartLocation("Start");
//        route.setEndLocation("End");
//        prefsHelper.saveRoute(route);
//
//    }

//    private boolean isRouteSelected(Route route) {
//        SharedPreferences prefs = getActivity().getSharedPreferences("app_prefs", Context.MODE_PRIVATE);
//        String selectedRouteId = prefs.getString("selected_route", null);
//        return selectedRouteId != null && selectedRouteId.equals(route.getId());
//    }

    private void fetchRoutePointsFromServer() {

        List<LatLng> fetchedPoints = getRoutePoints();

        if (fetchedPoints != null && !fetchedPoints.isEmpty()) {
            routePoints.addAll(fetchedPoints);
            drawRouteOnMap(routePoints);
        } else {
            Toast.makeText(getActivity(), "경로 데이터를 받아오는 데 실패했습니다.", Toast.LENGTH_SHORT).show();
        }
    }

    private void drawRouteOnMap(List<LatLng> routePoints) {
        if (kakaoMap != null) {

            RouteLineStyle style = RouteLineStyle.from(getActivity(),
                    R.style.SimpleRouteLineStyle);
            RouteLineOptions options = RouteLineOptions.from(
                    Arrays.asList(RouteLineSegment.from(routePoints, style)));

            routeLine = layer.addRouteLine(options);
            kakaoMap.moveCamera(CameraUpdateFactory.newCenterPosition(
                    LatLng.from(37.338549743448546,127.09368565409382), 16));

        }
    }

    // 더미 데이터 생성
    private List<LatLng> getRoutePoints() {

        List<LatLng> routePoints = new ArrayList<>();

        UUID uuid = UserManager.getInstance().getUserId();
        routeId = route.getId();

        url = "http://15.164.152.246:8080/activitys/" + uuid + "/" + routeId;
        String data = "";

        new Thread(() -> {
            String result = httpGetConnection(url);
            handler.post(() -> {
                seeNetworkResult(result);
                if (result != null && !result.isEmpty()) {
                    // Parse the result and update routePoints
                    routePoints.clear();  // Clear previous points if necessary
                    routePoints.addAll(parseRouteAll(result));
                }

                drawRouteOnMap(routePoints);
            });
        }).start();


//        List<LatLng> routePoints = Arrays.asList(
//                LatLng.from(37.338549743448546,127.09368565409382),
//                LatLng.from(37.33856778190988,127.093663107081),
//                LatLng.from(37.33860015104726,127.09374891110167),
//                LatLng.from(37.33866855056389,127.09384830168884),
//                LatLng.from(37.33881977657985,127.09403355969684),
//                LatLng.from(37.33881977657985,127.09403355969684),
//                LatLng.from(37.338798130341964,127.09406061609467),
//                LatLng.from(37.33874386013671,127.0943223542757),
//                LatLng.from(37.33869695980336,127.09438097621258),
//                LatLng.from(37.337824766739104,127.09437537101812),
//                LatLng.from(37.33770221229771,127.09439327300674),
//                LatLng.from(37.33770221229771,127.09439327300674),
//                LatLng.from(37.3376974871616,127.09578804101909),
//                LatLng.from(37.3376974871616,127.09578804101909),
//                LatLng.from(37.336219787367654,127.0957997057665),
//                LatLng.from(37.33621663148788,127.09524451138867),
//                LatLng.from(37.336234684781665,127.09520391048807),
//                LatLng.from(37.336234684781665,127.09520391048807),
//                LatLng.from(37.33645497790997,127.09465351015245));

        return routePoints;
    }

    public List<LatLng> parseRouteAll(String json) {
        List<LatLng> RoutePointList = new ArrayList<>();

        try {
            // JSON 전체 객체를 먼저 파싱합니다.
            JSONObject jsonObject = new JSONObject(json);

            // "data" 키에 있는 JSON 객체를 추출합니다.
            JSONObject dataObject = jsonObject.getJSONObject("data");

            // "locations" 배열을 추출합니다.
            JSONArray locationsArray = dataObject.getJSONArray("locations");

            for (int i = 0; i < locationsArray.length(); i++) {
                JSONObject locationObject = locationsArray.getJSONObject(i);

                // 필요한 데이터 추출
                double latitude = locationObject.getDouble("latitude");
                double longitude = locationObject.getDouble("longitude");

                // 객체 생성 및 리스트에 추가
                LatLng routePoint = LatLng.from(latitude,longitude);
                RoutePointList.add(routePoint);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return RoutePointList;
    }



    public String httpGetConnection(String UrlData) {
        String responseData = "";
        BufferedReader br = null;

        try {
            URL url = new URL(UrlData);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Content-Type", "application/json; utf-8");
            conn.setRequestProperty("Accept", "application/json");

            // 서버 응답 읽기
            br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
            StringBuilder sb = new StringBuilder();
            while ((responseData = br.readLine()) != null) {
                sb.append(responseData);
            }

            return sb.toString();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } finally {
            try {
                if (br != null) {
                    br.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void seeNetworkResult(String result) {
        Log.d("NetworkResult", result);
    }
}
