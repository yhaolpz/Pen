package com.wyh.pen.core;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Looper;
import androidx.annotation.NonNull;
import androidx.annotation.RestrictTo;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
@SuppressLint("EnumClassName")
public enum PenExecutor {
    INSTANCE;

    private final MainThreadExecutor mMainExecutor = new MainThreadExecutor();
    @SuppressLint("ThreadNameRequired ")
    private ExecutorService mWorkExecutor = Executors.newSingleThreadExecutor(
            new ExecutorsThreadFactory("work"));


    public static PenExecutor getInstance() {
        return INSTANCE;
    }

    /**
     * 文件读写操作
     */
    public static void executeWork(Runnable runnable) {
        getInstance().mWorkExecutor.execute(runnable);
    }

    /**
     * 主线程操作
     */
    public static void executeMain(Runnable runnable) {
        getInstance().mMainExecutor.execute(runnable);
    }


    public static class MainThreadExecutor implements Executor {
        private Handler mainThreadHandler = new Handler(Looper.getMainLooper());

        @Override
        public void execute(@NonNull Runnable command) {
            mainThreadHandler.post(command);
        }
    }

    private static class ExecutorsThreadFactory implements ThreadFactory {

        private AtomicInteger count = new AtomicInteger(1);
        private String name;

        ExecutorsThreadFactory(String name) {
            this.name = name;
        }

        @Override
        public Thread newThread(@NonNull Runnable r) {
            return new Thread(r, "PenExecutor-" + name + "-Thread-" + count.getAndIncrement());
        }
    }

}
