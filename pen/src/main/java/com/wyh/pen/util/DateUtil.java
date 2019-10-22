package com.wyh.pen.util;

import android.annotation.SuppressLint;

import androidx.annotation.RestrictTo;
import androidx.annotation.StringDef;

import com.wyh.pen.core.Pen;
import com.wyh.pen.core.PenTag;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.text.SimpleDateFormat;
import java.util.Date;

@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
public class DateUtil {

    static final String FORMAT_DATE_EN = "yyyy-MM-dd";
    static final String FORMAT_HOUR_EN = "HH:mm:ss";


    @StringDef({FORMAT_DATE_EN, FORMAT_HOUR_EN})
    @Retention(RetentionPolicy.SOURCE)
    @interface FORMAT {
    }

    @SuppressLint("SimpleDateFormat")
    private static final SimpleDateFormat SDF = new SimpleDateFormat(FORMAT_DATE_EN);


    public static String getDate() {
        return format(FORMAT_DATE_EN, new Date());
    }

    public static String getHour() {
        return format(FORMAT_HOUR_EN, new Date());
    }

    public static String getTime() {
        Date date = new Date();
        return format(FORMAT_DATE_EN, date) + "-" + format(FORMAT_HOUR_EN, date);
    }

    public static String format(@FORMAT String timeFormat, Date date) {
        try {
            SDF.applyPattern(timeFormat);
            return SDF.format(date);
        } catch (Exception e) {
            Pen.printE(PenTag.INTERNAL_TAG, "format: " + e.toString());
            return "UnknownTime";
        }
    }
}
