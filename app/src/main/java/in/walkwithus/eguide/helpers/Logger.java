package in.walkwithus.eguide.helpers;

import android.util.Log;

import in.walkwithus.eguide.BuildConfig;

/**
 * Updated by bahwan on 12/25/17.
 * Project name: Eguide
 */


public class Logger {

    public static void v(String tag, String msg) {
        if (BuildConfig.DEBUG) {
            Log.v(tag, msg);
        }
    }

    public static void v(String tag, String msg, Throwable throwable) {
        if (BuildConfig.DEBUG) {
            Log.v(tag, msg, throwable);
        }
    }

    public static void d(String tag, String msg) {
        if (BuildConfig.DEBUG) {
            Log.d(tag, msg);
        }
    }

    public static void d(String tag, String msg, Throwable throwable) {
        if (BuildConfig.DEBUG) {
            Log.d(tag, msg, throwable);
        }
    }

    public static void i(String tag, String msg) {
        if (BuildConfig.DEBUG) {
            Log.i(tag, msg);
        }
    }

    public static void i(String tag, String msg, Throwable throwable) {
        if (BuildConfig.DEBUG) {
            Log.i(tag, msg, throwable);
        }
    }

    public static void w(String tag, String msg) {
        if (BuildConfig.DEBUG) {
            Log.w(tag, msg);
        }
    }

    public static void w(String tag, String msg, Throwable throwable) {
        if (BuildConfig.DEBUG) {
            Log.w(tag, msg, throwable);
        }
    }

    public static void e(String tag, String msg) {
        if (BuildConfig.DEBUG) {
            Log.e(tag, msg);
        }
    }

    public static void e(String tag, String msg, Throwable throwable) {
        if (BuildConfig.DEBUG) {
            Log.e(tag, msg, throwable);
        }
    }

    public static void request(String tag, String url, String request) {
        if (BuildConfig.DEBUG) {
            Log.i(tag, String.format("Request url: %s\n    payload:%s\n", url,request));
        }
    }
    public static void response(String tag,String request) {
        if (BuildConfig.DEBUG) {
            Log.i(tag, String.format("Response : %s\n", request));
        }
    }
}
