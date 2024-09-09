package com.a2b2.plog;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.io.Serializable;

public class MyPloggingItem implements Serializable {
    private int activityId;
    private String ploggingDate;
    private int ploggingTime;  // 초 단위 시간
    private double distance;  // km 단위 거리
    private int trashSum;  // 주운 쓰레기 개수

    // 생성자
    public MyPloggingItem(int activityId, String ploggingDate, int ploggingTime, double distance, int trashSum) {
        this.activityId = activityId;
        this.ploggingDate = ploggingDate;
        this.ploggingTime = ploggingTime;
        this.distance = distance;
        this.trashSum = trashSum;
    }

    // Getter 메서드
    public int getActivityId() {
        return activityId;
    }

    public String getPloggingDate() {
        return ploggingDate;
    }

    public int getTime() {
        return ploggingTime;
    }

    public double getDistance() {
        return distance;
    }

    public int getTrashSum() {
        return trashSum;
    }

    // JSON 데이터를 이용해 MyPloggingItem 객체를 생성하는 메서드
    public static MyPloggingItem fromJson(JsonObject jsonObject) {
        int activityId = jsonObject.get("activityId").getAsInt();

        // ploggingDate를 [year, month, day] 형식으로 받아오는 배열을 처리
        JsonArray dateArray = jsonObject.getAsJsonArray("ploggingDate");
        String ploggingDate = dateArray.get(0).getAsInt() + "." +
                dateArray.get(1).getAsInt() + "." +
                dateArray.get(2).getAsInt();

        int ploggingTime = jsonObject.get("ploggingTime").getAsInt();
        double distance = jsonObject.get("distance").getAsDouble();
        int trashSum = jsonObject.get("trash_sum").getAsInt();

        return new MyPloggingItem(activityId, ploggingDate, ploggingTime, distance, trashSum);
    }
}
