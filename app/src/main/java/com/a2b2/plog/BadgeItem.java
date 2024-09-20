package com.a2b2.plog;

public class BadgeItem {
    private int badgeImage;
    private int badgeId;
    private String badgeType;   //여기에 배지 종류 적기..

    private String unlockCondition = "플로깅 누적 시간 50H 달성";
    private int cost=0;
    private boolean isMine = false;


    public BadgeItem(int badgeImage, String badgeName) {
        this.badgeImage = badgeImage;
        this.badgeType = badgeName;
        badgeId = 1;
    }
    public BadgeItem(int badgeId, int badgeImage) {
        this.badgeImage = badgeImage;
        this.badgeId = badgeId;
    }

    public BadgeItem(int badgeImage, String badgeName, String unlockCondition) {
        this.badgeImage = badgeImage;
        this.badgeType = badgeName;
        this.unlockCondition = unlockCondition;
    }

    public BadgeItem(int badgeId, int badgeImage, String badgeName, String unlockCondition) {
        this.badgeId = badgeId;
        this.badgeImage = badgeImage;
        this.badgeType = badgeName;
        this.unlockCondition = unlockCondition;
    }

    public void setMine(boolean mine) {
        isMine = mine;
    }

    public boolean isMine() {
        if (badgeId != 1) {
            return isMine;
        } else {
            isMine = true;
            return isMine;
        }
    }

    public int getCost() {
        return cost;
    }

    public void setCost(int cost) {
        this.cost = cost;
    }

    public int getBadgeId() {
        return badgeId;
    }

    public void setBadgeId(int badgeId) {
        this.badgeId = badgeId;
    }

    public int getBadgeImage() {
        return badgeImage;
    }

    public void setBadgeImage(int badgeImage) {
        this.badgeImage = badgeImage;
    }

    public String getBadgeType() {
        return badgeType;
    }
    public String getUnlockCondition() {
        return unlockCondition;
    }

    public void setUnlockCondition(String unlockCondition) {
        this.unlockCondition = unlockCondition;
    }
}
