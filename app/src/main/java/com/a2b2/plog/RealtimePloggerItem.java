package com.a2b2.plog;

import android.os.Parcel;
import android.os.Parcelable;

public class RealtimePloggerItem implements Parcelable {
    private int icon;
    private String nickname;

    // Constructor
    public RealtimePloggerItem(int icon, String nickname) {
        this.icon = icon;
        this.nickname = nickname;
    }

    // Parcelable implementation
    protected RealtimePloggerItem(Parcel in) {
        icon = in.readInt();
        nickname = in.readString();
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
        dest.writeString(nickname);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    // Getters
    public int getIcon() {
        return icon;
    }

    public String getNickname() {
        return nickname;
    }

}
