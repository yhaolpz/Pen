package com.wyh.pen.core;

import android.annotation.SuppressLint;
import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import android.text.TextUtils;

import com.wyh.pen.helper.FileHelper;
import com.wyh.pen.helper.PermissionHelper;
import com.wyh.pen.internal.PenExceptionHandler;
import com.wyh.pen.internal.PenLifecycle;
import com.wyh.pen.record.LogRecorderImpl;
import com.wyh.pen.upload.PrepareUploadListener;
import com.wyh.pen.util.LogUtil;
import com.wyh.pen.util.NetUtil;

import java.io.File;

@SuppressLint("EnumClassName")
enum PenManager {
    @SuppressLint("StaticFieldLeak") INSTANCE;

    public static PenManager getInstance() {
        return INSTANCE;
    }

    @Nullable
    private Context mAppContext;
    @Nullable
    private PenConfig mConfig;
    @Nullable
    private LogRecorderImpl mLogRecorder;

    void init(@NonNull PenConfig penConfig) {
        PenLogcat.setDebugLevel(penConfig.getLogcatDebugLevel());
        Pen.print(PenTag.INTERNAL_TAG, "PenManager-->init " + penConfig.toString());
        mConfig = penConfig;
        mAppContext = mConfig.getContext();

        //初始化日志存储目录
        if (TextUtils.isEmpty(mConfig.getLogDir())) {
            File file = FileHelper.getDefaultLogDir(mAppContext, mConfig.getCurrentProcessName());
            if (file != null && file.exists()) {
                mConfig.setLogDir(file.getAbsolutePath());
                Pen.print(PenTag.INTERNAL_TAG, "PenManager-->init FileHelper.getDefaultLogDir=" + mConfig.getLogDir());
            } else {
                //避免极端情况下未获取到日志目录的情况，直接return，不初始化mLogUploader、mLogRecorder，调用其他方法不会出错
                Pen.print(PenTag.INTERNAL_TAG, "PenManager-->init FileHelper.getDefaultLogDir(mAppContext)() == null !");
                return;
            }
        } else {
            mConfig.setLogDir(mConfig.getLogDir() + File.separator + mConfig.getCurrentProcessName());
            FileHelper.ensureStorageDir(mConfig.getLogDir());
            Pen.print(PenTag.INTERNAL_TAG, "PenManager-->init LogDir=" + mConfig.getLogDir());
        }
        if (mAppContext != null) {
            //清除过期文件
            if (PermissionHelper.hasWriteAndReadStoragePermission(mAppContext)) {
                PenExecutor.executeWork(new Runnable() {
                    @Override
                    public void run() {
                        FileHelper.cleanOverdueLog(mAppContext, mConfig.getLogDir(), mConfig.getCurrentProcessName());
                    }
                });
            }
            PenLifecycle.init(mAppContext);
        }
        new PenExceptionHandler();
        if (mConfig.getRecordDebugLevel() != Pen.DebugLevel.NONE) {
            mLogRecorder = new LogRecorderImpl(mConfig);
        }
    }

    public void print(@Pen.DebugLevel int level,
                      @NonNull String tag,
                      @NonNull String msg,
                      @Nullable Object... args) {
        msg = LogUtil.formatMsg(msg, args);
        msg = LogUtil.addTraceIfDebug(msg);
        PenLogcat.print(level, tag, msg);
    }

    public void print(@Pen.DebugLevel int level,
                      @NonNull String tag,
                      @Nullable Object obj) {
        String msg = LogUtil.toString(obj);
        msg = LogUtil.addTraceIfDebug(msg);
        PenLogcat.print(level, tag, msg);
    }

    public void print(@Pen.DebugLevel int level,
                      @NonNull String tag,
                      @NonNull Object obj,
                      @NonNull String msg) {
        msg = LogUtil.formatMsg(msg) + "：" + LogUtil.toString(obj);
        msg = LogUtil.addTraceIfDebug(msg);
        PenLogcat.print(level, tag, msg);
    }


    private void record(@Pen.DebugLevel int level,
                        @NonNull String tag,
                        @NonNull String msg) {
        if (mLogRecorder != null) {
            mLogRecorder.record(level, tag, msg);
        }
    }

    public void printAndRecord(@Pen.DebugLevel int level,
                               @NonNull String tag,
                               @NonNull String msg,
                               @Nullable Object... args) {
        msg = LogUtil.formatMsg(msg, args);
        record(level, tag, msg);
        msg = LogUtil.addTraceIfDebug(msg);
        PenLogcat.print(level, tag, msg);
    }

    public void printAndRecord(@Pen.DebugLevel int level,
                               @NonNull String tag,
                               @Nullable Object obj) {
        String msg = LogUtil.toString(obj);
        record(level, tag, msg);
        msg = LogUtil.addTraceIfDebug(msg);
        PenLogcat.print(level, tag, msg);
    }

    public void printAndRecord(@Pen.DebugLevel int level,
                               @NonNull String tag,
                               @NonNull Object obj,
                               @NonNull String msg) {
        msg = LogUtil.formatMsg(msg) + "：" + LogUtil.toString(obj);
        record(level, tag, msg);
        msg = LogUtil.addTraceIfDebug(msg);
        PenLogcat.print(level, tag, msg);
    }

    public void printAndRecordE(@NonNull String tag,
                                @Nullable Throwable tr,
                                @Nullable String msg,
                                @Nullable Object... args) {
        msg = LogUtil.formatMsg(msg, args);
        if (tr != null) {
            msg = msg + "：" + LogUtil.getStackTraceString(tr);
        }
        record(Pen.DebugLevel.ERROR, tag, msg);
        msg = LogUtil.addTraceIfDebug(msg);
        PenLogcat.print(Pen.DebugLevel.ERROR, tag, msg);
    }


    public void upload(@NonNull PrepareUploadListener prepareUploadListener) {
        if (mConfig == null || mLogRecorder == null) {
            return;
        }
        if (!NetUtil.isNetworkAvailable(mConfig.getContext())) {
            Pen.printE(PenTag.INTERNAL_TAG, "upload--> Network not Available !");
            return;
        }
        if (mConfig.getRecordDebugLevel() == Pen.DebugLevel.NONE) {
            Pen.printE(PenTag.INTERNAL_TAG, "upload--> getRecordDebugLevel() == Pen.DebugLevel.NONE !");
            return;
        }
        if (!PermissionHelper.hasWriteAndReadStoragePermission(mConfig.getContext())) {
            Pen.printE(PenTag.INTERNAL_TAG, "upload-->!hasWriteAndReadStoragePermission");
            return;
        }
        mLogRecorder.prepareUploadAsync(prepareUploadListener);
    }
}