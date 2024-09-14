package com.a2b2.plog;

import android.util.SparseArray;

public class BadgeManager {

    private static SparseArray<Integer> badgeIdToDrawableMap;

    static {
        badgeIdToDrawableMap = new SparseArray<>();
        //1번부터 시작, 1은 뱃지 없는 기본이미지로 설정
        badgeIdToDrawableMap.put(1, R.drawable.basic_badge);
        badgeIdToDrawableMap.put(2, R.drawable.lion);
        badgeIdToDrawableMap.put(3, R.drawable.polarbear);
        badgeIdToDrawableMap.put(4, R.drawable.stork);
        badgeIdToDrawableMap.put(5, R.drawable.small_clawed_otter);
        badgeIdToDrawableMap.put(6, R.drawable.red_wolf);
        badgeIdToDrawableMap.put(7, R.drawable.sturgeon);
        badgeIdToDrawableMap.put(8, R.drawable.humpback_whale);
        badgeIdToDrawableMap.put(9, R.drawable.elephant);
        badgeIdToDrawableMap.put(10, R.drawable.tiger);
        badgeIdToDrawableMap.put(11, R.drawable.win);
        badgeIdToDrawableMap.put(12, R.drawable.garbage_row);
        badgeIdToDrawableMap.put(13, R.drawable.garbage_middle);
        badgeIdToDrawableMap.put(14, R.drawable.garbage_high);
        badgeIdToDrawableMap.put(15, R.drawable.plastic_row);
        badgeIdToDrawableMap.put(16, R.drawable.plastic_middle);
        badgeIdToDrawableMap.put(17, R.drawable.plastic_high);
    }

    public static int getDrawableForBadgeId(int badgeId) {
        Integer drawableId = badgeIdToDrawableMap.get(badgeId);
        return drawableId != null ? drawableId : R.drawable.basic_badge; // 기본 배지 이미지 설정
    }

    public static int getBadgeMapSize() {
        return badgeIdToDrawableMap.size();
    }
}
