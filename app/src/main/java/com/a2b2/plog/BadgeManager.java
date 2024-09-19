package com.a2b2.plog;

import android.content.Context;
import android.util.SparseArray;

public class BadgeManager {

    private static SparseArray<Integer> badgeIdToDrawableMap;
    private static SparseArray<String> drawableToBadgeIdMap;

    static {
        badgeIdToDrawableMap = new SparseArray<>();
        drawableToBadgeIdMap = new SparseArray<>();

        // 배지 ID와 드로어블을 매핑
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

    // 배지 ID에 해당하는 잠금 해제된 이미지 얻기
    public static int getDrawableForBadgeId(int badgeId) {
        Integer drawableId = badgeIdToDrawableMap.get(badgeId);
        return drawableId != null ? drawableId : R.drawable.basic_badge; // 기본 배지 이미지
    }

    // 배지 ID에 해당하는 잠긴 버전 이미지 얻기
    public static int getLockedDrawableForBadgeId(int badgeId, Context context) {
        Integer unlockedDrawableId = badgeIdToDrawableMap.get(badgeId);
        if (unlockedDrawableId != null) {
            // 리소스 이름에 _lock을 붙여서 잠긴 이미지 리소스를 찾음
            String unlockedDrawableName = context.getResources().getResourceEntryName(unlockedDrawableId);
            String lockedDrawableName = unlockedDrawableName + "_lock";

            // 리소스 이름을 기반으로 잠긴 이미지 리소스 ID를 가져옴
            int lockedDrawableId = context.getResources().getIdentifier(lockedDrawableName, "drawable", context.getPackageName());

            // 잠긴 이미지 리소스가 존재하는지 확인
            if (lockedDrawableId != 0) {
                return lockedDrawableId;
            }
        }
        return R.drawable.basic_badge; // 기본 잠긴 배지 이미지 (기본 이미지가 없으면 기본 배지로 설정)
    }

    // lock 드로어블 ID로 배지 ID를 찾는 메서드
    public static int getBadgeIdForLockedDrawable(int lockedDrawableId, Context context) {
        // 드로어블 이름을 통해 원본 배지 ID를 찾음
        String lockedDrawableName = context.getResources().getResourceEntryName(lockedDrawableId);
        String unlockedDrawableName = lockedDrawableName.replace("_lock", ""); // _lock을 제거해서 원본 배지의 리소스 이름 추출

        // 리소스 ID로 원본 배지의 ID를 조회
        int unlockedDrawableId = context.getResources().getIdentifier(unlockedDrawableName, "drawable", context.getPackageName());

        // 해당 배지 ID 반환 (없을 경우 기본 배지 ID인 1 반환)
        for (int i = 0; i < badgeIdToDrawableMap.size(); i++) {
            int badgeId = badgeIdToDrawableMap.keyAt(i);
            if (badgeIdToDrawableMap.get(badgeId) == unlockedDrawableId) {
                return badgeId;
            }
        }

        return 1; // 기본 배지 ID 반환
    }

    public static int getBadgeMapSize() {
        return badgeIdToDrawableMap.size();
    }
}
