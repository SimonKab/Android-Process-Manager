package com.simonk.projects.taskmanager.util;

import android.app.ActivityManager;
import android.content.Context;

public class MemoryUtils {

    private static final double B_TO_GB = 1024d * 1024d * 1024d;

    public static ActivityManager.MemoryInfo getMemoryInfo(Context context) {
        ActivityManager actManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo memInfo = new ActivityManager.MemoryInfo();

        actManager.getMemoryInfo(memInfo);
        return memInfo;
    }

    public static float getAvailableMemory(ActivityManager.MemoryInfo memoryInfo) {
        return ((int) (memoryInfo.availMem / B_TO_GB * 100)) / 100.0f;
    }

    public static float getTotalMemory(ActivityManager.MemoryInfo memoryInfo) {
        return ((int) (memoryInfo.totalMem / B_TO_GB * 100)) / 100.0f;
    }

}
