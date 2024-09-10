package com.a2b2.plog;

import java.util.UUID;

public class UserManager {

    private static UserManager instance;
    private String userNickname;
    private UUID userUUID;
    private boolean isMembership = false;
    private int badgeId = 1;

    private UserManager() {}

    public static UserManager getInstance() {
        if (instance == null) {
            instance = new UserManager();
        }
        return instance;
    }

    public void setMembership(boolean membership) {
        isMembership = membership;
    }

    public boolean getIsMembership() {
        return isMembership;
    }

    public void setBadgeId(int badgeId) {
        this.badgeId = badgeId;
    }

    public int getBadgeId() {
        return badgeId;
    }

    public void setUserNickname(String userNickname) {
        this.userNickname = userNickname;
    }

    public String getUserNickname() {
        return userNickname;
    }

    public void setUserId(UUID userUUID) {
        this.userUUID = userUUID;
    }

    public UUID getUserId() {
        return userUUID;
    }
}
