package com.wyh.pen.helper;

import android.os.Debug;

import androidx.annotation.RestrictTo;

import com.wyh.pen.core.Pen;
import com.wyh.pen.core.PenTag;
import com.wyh.pen.util.LogUtil;

import java.util.LinkedList;
import java.util.List;

@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
public class PerformanceHelper {

    public static void recordMemory() {
        long maxMemory = Runtime.getRuntime().maxMemory();
        long totalMemory = Runtime.getRuntime().totalMemory();
        long freeMemory = Runtime.getRuntime().freeMemory();
        long useMemory = totalMemory - freeMemory;
        long remainMemory = maxMemory - useMemory;
        List<String> msgList = new LinkedList<>();
        //返回的是java虚拟机（这个进程）能构从操作系统那里挖到的最大的内存
        msgList.add("max:" + LogUtil.b2Mb(maxMemory));
        //返回的是java虚拟机现在已经从操作系统那里挖过来的内存大小，也就是java虚拟机这个进程当时所占用的所有 内存
        msgList.add("total:" + LogUtil.b2Mb(totalMemory));
        // 在java程序运行的过程的，内存总是慢慢的从操 作系统那里挖的，基本上是用多少挖多少，
        // 但是java虚拟机100％的情况下是会稍微多挖一点的，这些挖过来而又没有用上的内存，实际上就是 freeMemory()，
        // 所以freeMemory()的值一般情况下都是很小的
        msgList.add("free:" + LogUtil.b2Mb(freeMemory));
        msgList.add("use:" + LogUtil.b2Mb(useMemory));
        msgList.add("remain:" + LogUtil.b2Mb(remainMemory));
        //返回的是当前进程navtive堆中已使用的内存大小
        msgList.add("native:" + LogUtil.b2Mb(Debug.getNativeHeapAllocatedSize()));
        Pen.d(PenTag.MEMORY_TAG, msgList);
    }

}
