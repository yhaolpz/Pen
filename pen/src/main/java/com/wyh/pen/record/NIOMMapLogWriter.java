package com.wyh.pen.record;

import androidx.annotation.WorkerThread;

import com.wyh.pen.core.Pen;
import com.wyh.pen.core.PenConfig;
import com.wyh.pen.core.PenConstant;
import com.wyh.pen.core.PenTag;
import com.wyh.pen.helper.FileHelper;
import com.wyh.pen.util.AppUtil;
import com.wyh.pen.util.DeviceUtil;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.util.Calendar;

/**
 * java.nio 实现 MMap 方式持久化日志
 *
 * @author WangYingHao
 * @since 2019-07-06
 */
class NIOMMapLogWriter {

    private static final long ONE_DAY_MS = 24 * 60 * 60 * 1000L; // 一天毫秒数
    private static final int FILE_MAX_SIZE = 10 * 1024 * 1024;  // 日志文件上限
    private static final int PAGE_SIZE = 80 * 1024;  // 内存映射页大小，最好是文件大小的因数
    private final PenConfig config;
    private long mZeroPointOfDayMs;
    private String mZeroPointOfDayStr;
    private File mWritingFile;
    private MappedByteBuffer mWritingBuffer;

    NIOMMapLogWriter(PenConfig config) {
        this.config = config;
    }

    /**
     * 持久化日志
     *
     * @param content 日志
     */
    @WorkerThread
    void write(String content) {
        final byte[] bytes = content.getBytes(StandardCharsets.UTF_8);
        try {
            checkMapped(bytes.length);
            mWritingBuffer.put(bytes);
        } catch (Exception e) {
            e.printStackTrace();
            Pen.printE(PenTag.INTERNAL_TAG, "NIOMMapLogWriter write: " + e.toString());
            if (e instanceof FileNotFoundException) {
                //文件被删除
                Pen.printE(PenTag.INTERNAL_TAG, "NIOMMapLogWriter checkWriteFile:  FileNotFoundException 日志文件被异常删除，mWritingFile 置 null，重新创建");
                try {
                    flushAndUnMap();
                    mWritingFile = null;
                    checkMapped(bytes.length);
                    mWritingBuffer.put(bytes);
                } catch (Exception e1) {
                    e1.printStackTrace();
                    Pen.printE(PenTag.INTERNAL_TAG, "NIOMMapLogWriter checkWriteFile:  日志文件被异常删除重新创建后写入异常: " + e.toString());
                }
            }
        }
    }

    /**
     * 停止写入
     */
    @WorkerThread
    void stopWrite() {
        flushAndUnMap();
        FileHelper.deleteMMapFileBlankContent(mWritingFile);
    }

    /**
     * 检查映射规则
     */
    private void checkMapped(final int byteLength) throws IOException {
        if (mWritingFile == null) { //若未映射则新建文件映射
            updateZeroPointOfDay();
            FileHelper.ensureStorageDir(config.getLogDir());
            mWritingFile = new File(config.getLogDir() + File.separator + mZeroPointOfDayStr + PenConstant.MMAP_NEW);
            if (mWritingFile.exists() && mWritingFile.length() > 0) {
                Pen.print(PenTag.INTERNAL_TAG, "NIOMMapLogWriter checkWriteFile: exists length=" + mWritingFile.length());
                FileHelper.deleteMMapFileBlankContent(mWritingFile);
                Pen.print(PenTag.INTERNAL_TAG, "NIOMMapLogWriter checkWriteFile: exists deleteMMapFileBlankContent length=" + mWritingFile.length());
            }
        } else {
            if (System.currentTimeMillis() - mZeroPointOfDayMs >= ONE_DAY_MS) {
                //判断日期已过期，新建文件
                Pen.printE(PenTag.INTERNAL_TAG, "NIOMMapLogWriter checkWriteFile:  >= ONE_DAY_MS 日期已过期，新建文件");
                flushAndUnMap();
                FileHelper.deleteMMapFileBlankContent(mWritingFile);
                mWritingFile = null;
                checkMapped(byteLength);
                return;
            }
        }
        if (mWritingBuffer == null) {
            if (mWritingFile.length() + PAGE_SIZE > FILE_MAX_SIZE) {
                Pen.print(Pen.DebugLevel.INFO, PenTag.INTERNAL_TAG, "NIOMMapLogWriter checkWriteFile:  > FILE_MAX_SIZE");
                File oldFile = new File(config.getLogDir() + File.separator + mZeroPointOfDayStr + PenConstant.MMAP_OLD);
                if (oldFile.exists()) {
                    Pen.printE(PenTag.INTERNAL_TAG, "NIOMMapLogWriter checkWriteFile:  >  oldFile.delete()");
                    oldFile.delete();
                }
                Pen.print(Pen.DebugLevel.INFO, PenTag.INTERNAL_TAG, "NIOMMapLogWriter checkWriteFile: renameTo=" + oldFile.getAbsolutePath());
                FileHelper.deleteMMapFileBlankContent(mWritingFile);
                mWritingFile.renameTo(oldFile);
                mWritingFile = null;
                checkMapped(byteLength);
            } else { //重新映射一页
                FileHelper.deleteMMapFileBlankContent(mWritingFile);
                final long fileLength = mWritingFile.length();
                RandomAccessFile randomAccessFile = new RandomAccessFile(mWritingFile, "rw");
                FileChannel channel = randomAccessFile.getChannel();
                Pen.print(PenTag.INTERNAL_TAG, "NIOMMapLogWriter checkWriteFile: map position=" + fileLength + " pageSize=" + PAGE_SIZE);
                mWritingBuffer = channel.map(FileChannel.MapMode.READ_WRITE, fileLength, PAGE_SIZE);
                FileHelper.closeQuietly(channel);
                FileHelper.closeQuietly(randomAccessFile);
                if (fileLength == 0) {
                    mWritingBuffer.put(getBasicInfo().getBytes(StandardCharsets.UTF_8));
                }
            }
        } else {
            if (byteLength > mWritingBuffer.remaining()) {
                //映射页已写满，重新映射
                Pen.print(PenTag.INTERNAL_TAG, "NIOMMapLogWriter checkWriteFile:  取消映射，映射页已写满");
                flushAndUnMap();
                checkMapped(byteLength);
            }
        }
    }


    /**
     * 主动刷新到磁盘并取消映射
     */
    private void flushAndUnMap() {
        if (mWritingBuffer == null) {
            Pen.printE(PenTag.INTERNAL_TAG, "NIOMMapLogWriter flushAndUnMap error：mWritingBuffer == null");
            return;
        }
        try {
            mWritingBuffer.force();
            Pen.print(PenTag.INTERNAL_TAG, "NIOMMapLogWriter flushAndUnMap success");
        } catch (Exception e) {
            Pen.printE(PenTag.INTERNAL_TAG, "NIOMMapLogWriter flushAndUnMap error：" + e.toString());
        } finally {
            mWritingBuffer = null; //nio 的 MMap 不能主动 unMap，gc 时会 unMap
        }
    }


    /**
     * 更新当前日期
     */
    private void updateZeroPointOfDay() {
        Calendar cal = Calendar.getInstance();
        final int y = cal.get(Calendar.YEAR);
        final int m = cal.get(Calendar.MONTH) + 1;
        final int d = cal.get(Calendar.DAY_OF_MONTH);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        mZeroPointOfDayMs = cal.getTimeInMillis();
        mZeroPointOfDayStr = y + "-" + m + "-" + d;
        Pen.print(PenTag.INTERNAL_TAG,
                "NIOMMapLogWriter updateZeroPointOfDay: mZeroPointOfDayMs=" + mZeroPointOfDayMs
                        + " mZeroPointOfDayStr=" + mZeroPointOfDayStr);
    }


    /**
     * 文件头部内容
     */
    private String getBasicInfo() {
        return "Android" +
                PenConstant.FIELD_SEPERATOR +
                config.getCurrentProcessName() +
                PenConstant.FIELD_SEPERATOR +
                AppUtil.getVersionName(config.getContext()) +
                PenConstant.FIELD_SEPERATOR +
                mZeroPointOfDayStr +
                PenConstant.FIELD_SEPERATOR +
                "~" +
                PenConstant.FIELD_SEPERATOR +
                DeviceUtil.getDeviceInfo() +
                PenConstant.FIELD_SEPERATOR +
                (DeviceUtil.isRoot() ? 1 : 0) + "\n";
    }

}
