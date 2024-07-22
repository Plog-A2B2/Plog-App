package com.a2b2.plog;

public class BadgeItem {
    private int badgeImage;
    private String badgeType;   //여기에 배지 종류 적기..

    private String unlockCondition = "플로깅 누적 시간 50H 달성";


    public BadgeItem(int badgeImage, String badgeName) {
        this.badgeImage = badgeImage;
        this.badgeType = badgeName;
    }

    public BadgeItem(int badgeImage, String badgeName, String unlockCondition) {
        this.badgeImage = badgeImage;
        this.badgeType = badgeName;
        this.unlockCondition = unlockCondition;
    }

    public int getBadgeImage() {
        return badgeImage;
    }

    public String getBadgeType() {
        return badgeType;
    }
    public String getUnlockCondition() {
        return unlockCondition;
    }
}
