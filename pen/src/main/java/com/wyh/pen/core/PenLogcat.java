package com.wyh.pen.core;

import androidx.annotation.NonNull;
import android.util.Log;

final class PenLogcat {

    private PenLogcat() {
    }

    @Pen.DebugLevel
    private static int sDebugLevel = Pen.DebugLevel.DEBUG;

    static void setDebugLevel(int debugLevel) {
        PenLogcat.sDebugLevel = debugLevel;
    }

    public static void v(@NonNull String tag, @NonNull String msg) {
        print(Pen.DebugLevel.VERBOSE, tag, msg);
    }

    public static void d(@NonNull String tag, @NonNull String msg) {
        print(Pen.DebugLevel.DEBUG, tag, msg);
    }

    public static void i(@NonNull String tag, @NonNull String msg) {
        print(Pen.DebugLevel.INFO, tag, msg);
    }

    public static void w(@NonNull String tag, @NonNull String msg) {
        print(Pen.DebugLevel.WARNING, tag, msg);
    }

    public static void e(@NonNull String tag, @NonNull String msg) {
        print(Pen.DebugLevel.ERROR, tag, msg);
    }

    public static void print(@Pen.DebugLevel int level, @NonNull String tag, @NonNull String msg) {
        if (level >= sDebugLevel) {
            switch (level) {
                case Pen.DebugLevel.VERBOSE:
                    Log.v(tag, msg);
                    break;
                case Pen.DebugLevel.DEBUG:
                    Log.d(tag, msg);
                    break;
                case Pen.DebugLevel.INFO:
                    Log.i(tag, msg);
                    break;
                case Pen.DebugLevel.WARNING:
                    Log.w(tag, msg);
                    break;
                case Pen.DebugLevel.ERROR:
                    Log.e(tag, msg);
                    break;
                case Pen.DebugLevel.ALL:
                case Pen.DebugLevel.NONE:
                    break;
            }
        }
    }


}
