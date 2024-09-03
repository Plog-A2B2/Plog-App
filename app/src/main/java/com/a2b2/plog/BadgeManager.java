package com.a2b2.plog;

import android.util.SparseArray;

public class BadgeManager {

    private static SparseArray<Integer> badgeIdToDrawableMap;

    static {
        badgeIdToDrawableMap = new SparseArray<>();
        //1번부터 시작, 1은 뱃지 없는 기본이미지로 설정해야함 ⭐️
        badgeIdToDrawableMap.put(1, R.drawable.elephant);
        badgeIdToDrawableMap.put(2, R.drawable.lion);
        badgeIdToDrawableMap.put(3, R.drawable.polarbear);
        badgeIdToDrawableMap.put(4, R.drawable.stork);
        badgeIdToDrawableMap.put(5, R.drawable.small_clawed_otter);
        badgeIdToDrawableMap.put(6, R.drawable.red_wolf);
        badgeIdToDrawableMap.put(7, R.drawable.sturgeon);
        badgeIdToDrawableMap.put(8, R.drawable.humpback_whale);
        badgeIdToDrawableMap.put(9, R.drawable.tiger);
        // 추가적인 배지 ID와 drawable 매핑을 여기에 추가
    }

    public static int getDrawableForBadgeId(int badgeId) {
        Integer drawableId = badgeIdToDrawableMap.get(badgeId);
        return drawableId != null ? drawableId : R.drawable.badgetest; // 기본 배지 이미지 설정
    }
}
