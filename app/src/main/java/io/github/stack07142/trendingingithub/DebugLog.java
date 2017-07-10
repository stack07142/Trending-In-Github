package io.github.stack07142.trendingingithub;

import android.util.Log;

public class DebugLog {

    static final boolean DEBUG = true;
    private static final String LOG_TAG = "DebugLog";

    public static void logD(String tag, String log) {

        if (DEBUG) {
            Log.d(LOG_TAG, tag + " >> " + log);
        }
    }

    public static void logD(String tag, String log, Throwable tr) {

        if (DEBUG) {
            Log.d(LOG_TAG, tag + " >> " + log, tr);
        }
    }

    public static void logI(String tag, String log) {

        if (DEBUG) {
            Log.i(LOG_TAG, tag + " >> " + log);
        }
    }

    public static void logE(String tag, String log) {

        if (DEBUG) {
            Log.e(LOG_TAG, tag + " >> " + log);
        }
    }

    public static void logW(String tag, String log) {

        if (DEBUG) {
            Log.w(LOG_TAG, tag + " >> " + log);
        }
    }

    public static void logW(String tag, String log, Throwable tr) {

        if (DEBUG) {
            Log.w(LOG_TAG, tag + " >> " + log, tr);
        }
    }
}
