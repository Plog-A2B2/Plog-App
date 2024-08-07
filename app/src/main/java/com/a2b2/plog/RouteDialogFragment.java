package com.a2b2.plog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class RouteDialogFragment extends DialogFragment {

    private static final String ARG_ROUTE = "route";
    private Route route;
    private boolean isRouteSelected = false;
    private KakaoMap kakaoMap;
    private RouteLineLayer layer;
    private List<LatLng> routePoints = new ArrayList<>();
    private RouteLine routeLine;
    MapView mapView;


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

        buttonSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isRouteSelected = !isRouteSelected;
                buttonSelect.setText(isRouteSelected ? "선택 취소" : "루트 선택");
                if(isRouteSelected) {
                    buttonSelect.setBackground(getActivity().getDrawable(R.drawable.round_rectangle_realgreen));
                } else {
                    buttonSelect.setBackground(getActivity().getDrawable(R.drawable.round_rectangle_realblue));
                }

                selectRoute(route);
                dismiss();
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
                fetchRoutePointsFromServer();
            }
        });


        builder.setView(view);
        return builder.create();
    }

    private void selectRoute(Route route) {
        // 선택된 루트를 저장하는 로직
        SharedPreferences prefs = getActivity().getSharedPreferences("app_prefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
//        editor.putString("selected_route", route.getId());
        editor.apply();
    }

//    private boolean isRouteSelected(Route route) {
//        SharedPreferences prefs = getActivity().getSharedPreferences("app_prefs", Context.MODE_PRIVATE);
//        String selectedRouteId = prefs.getString("selected_route", null);
//        return selectedRouteId != null && selectedRouteId.equals(route.getId());
//    }

    private void fetchRoutePointsFromServer() {
        // 실제 서버에서 데이터를 받아오는 코드로 대체해야 합니다.
        // 여기서는 더미 데이터를 사용합니다.
        List<LatLng> fetchedPoints = getDummyRoutePoints();

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
