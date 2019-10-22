package com.wyh.pen.core;

import androidx.annotation.RestrictTo;

@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
public interface PenConstant {

    String LOG_DIR = "pen_log"; // 日志存放目录
    long LOG_OVERDUE_TIME_MS = 3 * 24 * 60 * 60 * 1000L; // 日志过期时间
    long MIN_SDCARD_FREE_SPACE_MB = 50; //可存放到sd卡目录的sd卡剩余空间下限，小于此值时采用cache目录
    String MMAP_NEW = "-mmap-new"; // 正在写的文件后缀
    String MMAP_OLD = "-mmap-old"; // 当天较早写入的文件后缀
    String TXT = ".txt"; // 压缩进zip的文件
    String ZIP = ".zip";// 压缩后的日志文件后缀
    int FORMAT_MB = 1024 * 1024;
    String FIELD_SEPERATOR = " ";// 日志分隔符号
    String PARAMETER_PRINT_FORMAT = "%s=\"%s\"";
    String RETURN_PRINT_FORMAT = "<- %s[%sms]=\"%s\"";

}
