package com.a2b2.plog;

import android.widget.ImageView;

import java.time.LocalDate;

public class CommunityItem {
    private int badge;
    private int postId;
    private String nickname;
    private String date;
    private String title;

    public CommunityItem(int postId, int badge, String nickname, String date, String title) {
        this.postId = postId;
        this.badge = badge;
        this.nickname = nickname;
        this.date = date;
        this.title = title;
    }
    public int getBadge() {
        return badge;
    }
    public String getNickname() {
        return nickname;
    }
    public String getDate() {
        return date;
    }
    public String getTitle() {
        return title;
    }
}
