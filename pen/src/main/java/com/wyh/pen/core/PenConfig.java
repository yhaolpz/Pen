package com.wyh.pen.core;

import android.app.Application;
import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.text.TextUtils;

/**
 * 构建配置
 *
 * @author WangYingHao
 * @since 2019-06-24
 */
public class PenConfig {
    private final Context application;
    @Pen.DebugLevel
    private final int logcatDebugLevel;
    @Pen.DebugLevel
    private final int recordDebugLevel;
    private final String cipherKey;
    @NonNull
    private final String currentProcessName;
    private String logDir;

    private PenConfig(final Builder builder) {
        this.application = builder.application;
        this.logDir = builder.logDir;
        this.logcatDebugLevel = builder.logcatDebugLevel;
        this.recordDebugLevel = builder.recordDebugLevel;
        this.cipherKey = builder.cipherKey;
        if (TextUtils.isEmpty(builder.currentProcessName)) {
            this.currentProcessName = "UnknownCurrentProcessName";
        } else {
            this.currentProcessName = builder.currentProcessName;
        }
    }

    @Override
    public String toString() {
        return "Config{" +
                "application=" + application +
                ", logcatDebugLevel=" + logcatDebugLevel +
                ", recordDebugLevel=" + recordDebugLevel +
                ", cipherKey='" + cipherKey + '\'' +
                ", logDir='" + logDir + '\'' +
                ", currentProcessName='" + currentProcessName + '\'' +
                '}';
    }

    @NonNull
    public String getCurrentProcessName() {
        return currentProcessName;
    }

    public String getLogDir() {
        return logDir;
    }

    void setLogDir(String logDir) {
        this.logDir = logDir;
    }

    public Context getContext() {
        return application;
    }

    public int getLogcatDebugLevel() {
        return logcatDebugLevel;
    }

    public int getRecordDebugLevel() {
        return recordDebugLevel;
    }

    public String getCipherKey() {
        return cipherKey;
    }

    public static final class Builder {
        private Application application;
        private String logDir;
        private String cipherKey;
        @Pen.DebugLevel
        private int logcatDebugLevel;
        @Pen.DebugLevel
        private int recordDebugLevel;
        private String currentProcessName;

        public Builder(Application application) {
            this.application = application;
        }

        /**
         * 日志存储路径，默认会先选择 sd 卡、失败选择 cache 目录
         */
        public Builder logDir(String logDir) {
            this.logDir = logDir;
            return this;
        }

        /**
         * 设置进程名
         */
        public Builder currentProcessName(@Nullable String currentProcessName) {
            this.currentProcessName = currentProcessName;
            return this;
        }

        /**
         * 允许输出到 logcat 的最低 debug 级别
         */
        public Builder logcatDebugLevel(@Pen.DebugLevel int level) {
            this.logcatDebugLevel = level;
            return this;
        }

        /**
         * 允许记录到文件的最低 debug 级别, 若设置为{@link Pen.DebugLevel#NONE}，则不会触发上传日志到服务端
         */
        public Builder recordDebugLevel(@Pen.DebugLevel int level) {
            this.recordDebugLevel = level;
            return this;
        }

        /**
         * 日志密钥
         */
        public Builder cipherKey(String cipherKey) {
            this.cipherKey = cipherKey;
            return this;
        }

        public PenConfig build() {
            if (application == null) {
                throw new IllegalArgumentException("application == null");
            }
            return new PenConfig(this);
        }

    }
}
