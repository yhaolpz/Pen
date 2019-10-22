package com.wyh.penApp;

import android.app.Application;

import com.wyh.pen.core.Pen;
import com.wyh.pen.core.PenConfig;

/**
 * @author WangYingHao
 * @since 2019-10-22
 */
public class BaseApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();


        Pen.init(new PenConfig.Builder(this)
                .logcatDebugLevel(Pen.DebugLevel.ALL)
                .recordDebugLevel(Pen.DebugLevel.ALL)
                .cipherKey("pen")
                .currentProcessName("penTestApp")
                .build());
    }
}
