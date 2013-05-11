package com.prettygirl.app.utils;

import java.util.HashSet;

public class UserInfoManager {
    public final static class Event {
        public final static int START_APP = 1;
        public final static int IMAGE_LOAD = 2;
        public final static int Review = 3;
    }

    public final static class Rewards {
        public final static int PER_LOGIN = 30;
        public final static int PER_IMAGE = 1;

        public final static int REWARD_NOTIFICATION_THRESHOLD = 20;
        public final static int REWARD_REVIEW = 100;
    }

    public static interface LevelListener {
        public void onLevelChange(int level, int previousLevel);

        public void onPointChange(int point, int previousPoint);
    }

    private final static int[] LEVEL_ENTRIES = new int[] { 0, 800, 2000 };

    private static final String KEY_POINT = "CURRENT_POINT";

    private static final UserInfoManager INSTANCE = new UserInfoManager();

    private UserInfoManager() {
    }

    public static UserInfoManager getInstance() {
        return INSTANCE;
    }

    public int getLevelDivByLevel(int level) {
        if (level >= LEVEL_ENTRIES.length) {
            return 0;
        }
        return LEVEL_ENTRIES[level];
    }

    /**
     * return 0,1,2,3 etc
     * @return
     */
    public int getCurrentLevel() {
        int p = getCurrentPoint();
        for (int i = LEVEL_ENTRIES.length - 1; i > -1; i--) {
            if (p > LEVEL_ENTRIES[i]) {
                return i;
            }
        }
        return 0;
    }

    public int getCurrentPoint() {
        return PreferenceUtils.getInt(KEY_POINT, 0);
    }

    public void onEvent(int eventName, Object... args) {
        int lastPoint = getCurrentPoint(), lastLevel = getCurrentLevel();
        int pointToAdd = 0;
        switch (eventName) {
        case Event.START_APP: {
            long currentTime = System.currentTimeMillis();
            long lastTime = PreferenceUtils.getLong("last_start_time", 1363422002981L);
            if (MiscUtil.isSameDay(lastTime, currentTime) == false) {
                pointToAdd = Rewards.PER_LOGIN;
            }
            PreferenceUtils.setLong("last_start_time", currentTime);
            break;
        }
        case Event.IMAGE_LOAD: {
            pointToAdd = Rewards.PER_IMAGE;
            break;
        }
        case Event.Review: {
            pointToAdd = Rewards.REWARD_REVIEW;
            break;
        }
        }
        if (pointToAdd != 0) {
            int currentPoint = lastPoint + pointToAdd;
            PreferenceUtils.setInt(KEY_POINT, currentPoint);
            if (mListeners.size() != 0) {
                int lastPointNotification = PreferenceUtils.getInt("last_point_notification", 0);
                if (currentPoint - lastPointNotification >= Rewards.REWARD_NOTIFICATION_THRESHOLD) {
                    for (LevelListener lis : mListeners) {
                        lis.onPointChange(currentPoint, lastPointNotification);
                    }
                    PreferenceUtils.setInt("last_point_notification", currentPoint);
                }

                int currentLevel = getCurrentLevel();
                if (currentLevel != lastLevel) {
                    for (LevelListener lis : mListeners) {
                        lis.onLevelChange(currentPoint, lastPointNotification);
                    }
                }
            }
        }
    }

    public void registerLevelListener(LevelListener lis) {
        mListeners.add(lis);
    }

    public void removeLevelListener(LevelListener lis) {
        mListeners.remove(lis);
    }

    HashSet<LevelListener> mListeners = new HashSet<UserInfoManager.LevelListener>();
}
