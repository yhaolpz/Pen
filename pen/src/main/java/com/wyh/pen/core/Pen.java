package com.wyh.pen.core;

import androidx.annotation.IntDef;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.wyh.pen.upload.PrepareUploadListener;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

public final class Pen {

    private Pen() {

    }

    public static void v(@NonNull String tag, @NonNull String msg, @Nullable Object... args) {
        PenManager.getInstance().printAndRecord(DebugLevel.VERBOSE, tag, msg, args);
    }

    public static void v(@NonNull String tag, @NonNull Object obj) {
        PenManager.getInstance().printAndRecord(DebugLevel.VERBOSE, tag, obj);
    }

    public static void v(@NonNull String tag, @NonNull Object obj, @NonNull String msg) {
        PenManager.getInstance().printAndRecord(DebugLevel.VERBOSE, tag, obj, msg);
    }

    public static void d(@NonNull String tag, @NonNull String msg, @Nullable Object... args) {
        PenManager.getInstance().printAndRecord(DebugLevel.DEBUG, tag, msg, args);
    }

    public static void d(@NonNull String tag, @NonNull Object obj) {
        PenManager.getInstance().printAndRecord(DebugLevel.DEBUG, tag, obj);
    }

    public static void d(@NonNull String tag, @NonNull Object obj, @NonNull String msg) {
        PenManager.getInstance().printAndRecord(DebugLevel.DEBUG, tag, obj, msg);
    }

    public static void i(@NonNull String tag, @NonNull String msg, @Nullable Object... args) {
        PenManager.getInstance().printAndRecord(DebugLevel.INFO, tag, msg, args);
    }

    public static void i(@NonNull String tag, @NonNull Object obj) {
        PenManager.getInstance().printAndRecord(DebugLevel.INFO, tag, obj);
    }

    public static void i(@NonNull String tag, @NonNull Object obj, @NonNull String msg) {
        PenManager.getInstance().printAndRecord(DebugLevel.INFO, tag, obj, msg);
    }

    public static void w(@NonNull String tag, @NonNull String msg, @Nullable Object... args) {
        PenManager.getInstance().printAndRecord(DebugLevel.WARNING, tag, msg, args);
    }

    public static void w(@NonNull String tag, @NonNull Object obj) {
        PenManager.getInstance().printAndRecord(DebugLevel.WARNING, tag, obj);
    }

    public static void w(@NonNull String tag, @NonNull Object obj, @NonNull String msg) {
        PenManager.getInstance().printAndRecord(DebugLevel.WARNING, tag, obj, msg);
    }

    public static void e(@NonNull String tag, @NonNull String msg, @Nullable Object... args) {
        PenManager.getInstance().printAndRecord(DebugLevel.ERROR, tag, msg, args);
    }

    public static void e(@NonNull String tag, @NonNull Object obj) {
        PenManager.getInstance().printAndRecord(DebugLevel.ERROR, tag, obj);
    }

    public static void e(@NonNull String tag, @NonNull Object obj, @NonNull String msg) {
        PenManager.getInstance().printAndRecord(DebugLevel.ERROR, tag, obj, msg);
    }

    public static void e(@NonNull String tag, @NonNull Throwable tr) {
        PenManager.getInstance().printAndRecordE(tag, tr, null);
    }

    public static void e(@NonNull String tag,
                         @NonNull Throwable tr,
                         @NonNull String msg,
                         @Nullable Object... args) {
        PenManager.getInstance().printAndRecordE(tag, tr, msg, args);
    }

    public static void print(@NonNull String tag, @NonNull String msg, @Nullable Object... args) {
        PenManager.getInstance().print(DebugLevel.DEBUG, tag, msg, args);
    }

    public static void print(@NonNull String tag, @NonNull Object obj) {
        PenManager.getInstance().print(DebugLevel.DEBUG, tag, obj);
    }

    public static void print(@NonNull String tag, @NonNull Object obj, @NonNull String msg) {
        PenManager.getInstance().print(DebugLevel.DEBUG, tag, obj, msg);
    }

    public static void print(@Pen.DebugLevel int level,
                             @NonNull String tag,
                             @NonNull String msg,
                             @Nullable Object... args) {
        PenManager.getInstance().print(level, tag, msg, args);
    }

    public static void printE(@NonNull String tag, @NonNull String msg, @Nullable Object... args) {
        PenManager.getInstance().print(DebugLevel.ERROR, tag, msg, args);
    }

    /**
     * 上传
     */
    public static void upload(@NonNull PrepareUploadListener prepareUploadListener) {
        PenManager.getInstance().upload(prepareUploadListener);
    }

    /**
     * 请在 application onCreate 中初始化
     *
     * @param config 配置
     */
    public static void init(@NonNull PenConfig config) {
        PenManager.getInstance().init(config);
    }


    /**
     * debug 级别
     */
    @IntDef({
            DebugLevel.ALL,
            DebugLevel.VERBOSE,
            DebugLevel.DEBUG,
            DebugLevel.INFO,
            DebugLevel.WARNING,
            DebugLevel.ERROR,
            DebugLevel.NONE
    })
    @Retention(RetentionPolicy.SOURCE)
    public @interface DebugLevel {
        int ALL = -1;
        int VERBOSE = 0;
        int DEBUG = 1;
        int INFO = 2;
        int WARNING = 3;
        int ERROR = 4;
        int NONE = 5;
    }

    /**
     * 对方法添加此注解，就会自动打印该方法参数、返回值以及方法耗时，类名做 tag
     * 打印方式为 {@link Pen#i(String, Object)}
     */
    @Target({
            ElementType.TYPE,
            ElementType.METHOD,
            ElementType.CONSTRUCTOR})
    @Retention(RetentionPolicy.CLASS)
    public @interface Aop {
    }
}
