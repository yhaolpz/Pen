package com.wyh.pen.internal;

import android.os.Process;
import androidx.annotation.AnyThread;
import androidx.annotation.RestrictTo;

import com.wyh.pen.core.Pen;
import com.wyh.pen.core.PenTag;

@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
public class PenExceptionHandler implements Thread.UncaughtExceptionHandler {

    private final Thread.UncaughtExceptionHandler mDefaultHandler;

    @AnyThread
    public PenExceptionHandler() {
        mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    private static void logUncaughtException(Thread t, Throwable th) {
        StringBuilder message = new StringBuilder();
        message.append("FATAL EXCEPTION: ").append("\n");
        message.append("PID: ").append(Process.myPid()).append(" ");
        message.append("Thread: ").append(t.getName()).append("\n");
        message.append("Type: ").append(th.getClass().getName()).append(" ");
        message.append("Message: ").append(th.getMessage()).append("\n");
        for (StackTraceElement s : th.getStackTrace()) {
            message.append(" ")
                    .append(s.getClassName())
                    .append(" ")
                    .append(s.getMethodName())
                    .append("(")
                    .append(s.getFileName())
                    .append(":")
                    .append(s.getLineNumber())
                    .append(")\n");
        }
        Pen.e(PenTag.EXCEPTION_TAG, message.toString());
    }

    @Override
    public void uncaughtException(Thread t, Throwable e) {
        logUncaughtException(t, e);
        if (mDefaultHandler != null) {
            mDefaultHandler.uncaughtException(t, e);
        }
    }

}
