package com.a2b2.plog;

import android.os.Parcel;
import android.os.Parcelable;

public class RealtimePloggerItem implements Parcelable {
    private int icon;
    private String distance;
    private String time;

    // Constructor
    public RealtimePloggerItem(int icon, String distance, String time) {
        this.icon = icon;
        this.distance = distance;
        this.time = time;
    }

    // Parcelable implementation
    protected RealtimePloggerItem(Parcel in) {
        icon = in.readInt();
        distance = in.readString();
        time = in.readString();
    }

    public static final Creator<RealtimePloggerItem> CREATOR = new Creator<RealtimePloggerItem>() {
        @Override
        public RealtimePloggerItem createFromParcel(Parcel in) {
            return new RealtimePloggerItem(in);
        }

        @Override
        public RealtimePloggerItem[] newArray(int size) {
            return new RealtimePloggerItem[size];
        }
    };

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(icon);
        dest.writeString(distance);
        dest.writeString(time);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    // Getters
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
