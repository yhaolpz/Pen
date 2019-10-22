package com.wyh.pen.util;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RestrictTo;

import com.wyh.pen.BuildConfig;
import com.wyh.pen.core.PenConstant;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.Objects;

@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
public class LogUtil {

    /**
     * 字节数（B）转为兆字节数（MB）
     *
     * @param byteCount 字节数
     * @return 多少 MB
     */
    public static String b2Mb(long byteCount) {
        return b2MbValue(byteCount) + "MB";
    }

    /**
     * 字节数（B）转为兆字节数（MB）
     *
     * @param byteCount 字节数
     * @return 多少 MB
     */
    public static float b2MbValue(long byteCount) {
        double d = (byteCount * 1.0 / (PenConstant.FORMAT_MB));
        return (float) Math.round(d * 100) / 100;
    }

    /**
     * 格式化日志消息
     *
     * @param message 日志消息
     * @param args    占位替换参数，常用：
     *                %s  -  String
     *                %d  -  int/long
     *                %f  -  float/double
     *                %b  -  boolean
     *                %c  -  char
     * @return 格式化之后的日志消息
     */
    @NonNull
    public static String formatMsg(@Nullable String message, @Nullable Object... args) {
        if (message == null) {
            return "";
        } else if (args == null || args.length == 0) {
            return message;
        } else {
            return String.format(message, args);
        }
    }

    /**
     * 将异常转为字符串
     *
     * @param tr 异常
     * @return 转换后的字符串
     */
    public static String getStackTraceString(@Nullable Throwable tr) {
        if (tr == null) {
            return "";
        }
        Throwable t = tr;
        while (t != null) {
            if (t instanceof UnknownHostException) {
                return "";
            }
            t = t.getCause();
        }
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        tr.printStackTrace(pw);
        pw.flush();
        return sw.toString();
    }

    /**
     * 将实例转换为字符串
     *
     * @param obj 实例，可直接格式数组、集合类型输出
     * @return 转换后的字符串
     */
    @NonNull
    public static String toString(@Nullable Object obj) {
        if (obj == null) {
            return "null";
        }
        if (!obj.getClass().isArray()) {
            return obj.toString();
        }
        if (obj instanceof boolean[]) {
            return Arrays.toString((boolean[]) obj);
        }
        if (obj instanceof byte[]) {
            return Arrays.toString((byte[]) obj);
        }
        if (obj instanceof char[]) {
            return Arrays.toString((char[]) obj);
        }
        if (obj instanceof short[]) {
            return Arrays.toString((short[]) obj);
        }
        if (obj instanceof int[]) {
            return Arrays.toString((int[]) obj);
        }
        if (obj instanceof long[]) {
            return Arrays.toString((long[]) obj);
        }
        if (obj instanceof float[]) {
            return Arrays.toString((float[]) obj);
        }
        if (obj instanceof double[]) {
            return Arrays.toString((double[]) obj);
        }
        if (obj instanceof Object[]) {
            return Arrays.deepToString((Object[]) obj);
        }
        return "Couldn't find a correct type for the object";
    }

    /**
     * 添加日志输出位置
     *
     * @param msg 日志
     * @return 添加输出位置的日志
     */
    public static String addTraceIfDebug(@NonNull String msg) {
        if (BuildConfig.DEBUG) {
            int index = 3;
            StackTraceElement[] stacks = new Throwable().getStackTrace();
            if (index >= stacks.length) {
                return msg;
            }
            final String fileName = stacks[index].getFileName();
            if (Objects.equals("PugLifecycle.java", fileName)) {
                return msg;
            }
            return msg + " (" + stacks[index].getFileName() + ":" + stacks[index].getLineNumber() + ")";
        } else {
            return msg;
        }
    }

}
