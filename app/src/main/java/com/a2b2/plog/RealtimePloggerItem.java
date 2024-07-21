package com.a2b2.plog;

public class RealtimePloggerItem {
    private int icon;
    private String distance;
    private String time;

    public RealtimePloggerItem(int icon, String distance, String time) {
        this.icon = icon;
        this.distance = distance;
        this.time = time;
    }

    public int getIcon() {
        return icon;
    }

    public String getDistance() {
        return distance;
    }

    public String getTime() {
        return time;
    }
}
