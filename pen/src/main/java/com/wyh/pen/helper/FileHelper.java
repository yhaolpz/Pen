package com.wyh.pen.helper;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Environment;
import android.os.StatFs;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.RestrictTo;

import com.wyh.pen.core.Pen;
import com.wyh.pen.core.PenConstant;
import com.wyh.pen.core.PenTag;

import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
public final class FileHelper {

    /**
     * 确保目录存在
     *
     * @param dirPath 目录路径
     */
    public static void ensureStorageDir(String dirPath) {
        try {
            File storageDir = new File(dirPath);
            if (!storageDir.exists() || !storageDir.isDirectory()) {
                storageDir.mkdirs();
            }
        } catch (Exception ignored) {
        }
    }

    /**
     * 默认日志存放目录
     */
    public static File getDefaultLogDir(Context context, @NonNull String curProcessName) {
        if (context == null) {
            return null;
        }
        File dir = getSDLogDirFile(context, curProcessName);
        if (dir == null || !isSDEnough()) {
            dir = getCacheLogDirFile(context, curProcessName);
        }
        return dir;
    }

    /**
     * 获取sd卡日志存放目录
     */
    @SuppressLint("SdCardPath")
    private static File getSDLogDirFile(Context context, @NonNull String curProcessName) {
        if (context == null) {
            return null;
        }

        try {//fabric: issues/5cae1b91f8b88c29635d16b8
            if (!Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
                return null;
            }
        } catch (Throwable ignored) {
        }

        String path = "/sdcard";
        try {
            // fabric : issues/5bf73f22f8b88c29630c64a3
            path = Environment.getExternalStorageDirectory().getAbsolutePath();
        } catch (Throwable ignored) {
        }
        File sdFile = new File(path + "/Android/data/" + context.getApplicationContext().getPackageName() + "/files");
        sdFile = new File(sdFile, getDirName(curProcessName));
        if (!sdFile.exists() || !sdFile.isDirectory()) {
            sdFile.mkdirs();
        }
        return sdFile;
    }

    /**
     * 获取内置存储日志存放目录
     */
    private static File getCacheLogDirFile(Context context, @NonNull String curProcessName) {
        if (context == null) {
            return null;
        }
        File cacheFile;
        try {
            cacheFile = context.getCacheDir();
        } catch (Exception ex) {
            ex.printStackTrace();
            cacheFile = context.getExternalCacheDir();
        }

        if (cacheFile != null) {
            cacheFile = new File(cacheFile, getDirName(curProcessName));
            if (!cacheFile.exists() || !cacheFile.isDirectory()) {
                cacheFile.mkdirs();
            }
        }
        return cacheFile;
    }

    /**
     * 定义日志路径为：pen_log/进程名
     */
    private static String getDirName(@NonNull String curProcessName) {
        return PenConstant.LOG_DIR +
                File.separator +
                curProcessName;
    }

    public static boolean isFileExist(String filePath) {
        return !TextUtils.isEmpty(filePath) && isFileExist(new File(filePath));
    }

    public static boolean isFileExist(File file) {
        return file != null && file.exists();
    }

    /**
     * 清理过期文件
     */
    public static void cleanOverdueLog(Context context, String logDirPath, @NonNull String curProcessName) {
        {
            File file = getSDLogDirFile(context, curProcessName);
            if (file != null) {
                cleanOverdueFile(file.listFiles());
            }
        }
        {
            File file = getCacheLogDirFile(context, curProcessName);
            if (file != null) {
                cleanOverdueFile(file.listFiles());
            }
        }
        {
            File file = new File(logDirPath);
            cleanOverdueFile(file.listFiles());
        }
    }

    /**
     * 清理无效&过期文件
     */
    public static void cleanOverdueFile(File[] files) {
        if (files == null || files.length == 0) {
            return;
        }
        for (File file : files) {
            if (isFileExist(file) && isLogFileOverdue(file)) {
                Pen.printE(PenTag.INTERNAL_TAG, "cleanOverdueFile: " + file.getAbsolutePath());
                deleteFileQuietly(file);
            }
        }
    }

    /**
     * 日志文件是否过期
     */
    private static boolean isLogFileOverdue(File file) {
        try {
            String dateFormat = "yyyy-MM-dd";
            SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
            String dateStr = file.getName().substring(0, dateFormat.length());
            Date date = sdf.parse(dateStr);
            return System.currentTimeMillis() - date.getTime() > PenConstant.LOG_OVERDUE_TIME_MS;
        } catch (Exception e) {
            Pen.printE(PenTag.INTERNAL_TAG, "isLogFileOverdue: " + e.toString());
        }
        return false;
    }

    /**
     * 过滤文件名包含特定字符串的日志文件
     */
    @NonNull
    public static List<File> filterFile(String dirPath, String containStr) {
        File[] files = new File(dirPath).listFiles();
        final List<File> upLogFileList = new LinkedList<>();
        if (files == null || files.length == 0) {
            return upLogFileList;
        }
        for (File file : files) {
            if (!isFileExist(file)) {
                continue;
            }
            if (file.getName().contains(containStr)) {
                upLogFileList.add(file);
            }
        }
        return upLogFileList;
    }

    /**
     * 删除某文件夹下的所有文件
     */
    public static void cleanDir(String dirPath) {
        File[] files = new File(dirPath).listFiles();
        if (files != null) {
            for (File file : files) {
                deleteFileQuietly(file);
            }
        }
    }

    public static void deleteFileQuietly(File file) {
        if (isFileExist(file)) {
            file.delete();
        }
    }

    /**
     * 合并文件
     */
    public static boolean mergeFiles(String[] fpaths, String resultPath) {
        if (fpaths == null || fpaths.length < 1 || TextUtils.isEmpty(resultPath)) {
            return false;
        }
        if (fpaths.length == 1) {
            return nioCopyFile(new File(fpaths[0]), new File(resultPath));
        }
        File[] files = new File[fpaths.length];
        for (int i = 0; i < fpaths.length; i++) {
            files[i] = new File(fpaths[i]);
        }
        File resultFile = new File(resultPath);
        FileChannel resultFileChannel = null;
        FileOutputStream fileOutputStream = null;
        try {
            fileOutputStream = new FileOutputStream(resultFile, true);
            resultFileChannel = fileOutputStream.getChannel();
            for (int i = 0; i < fpaths.length; i++) {
                FileChannel blk = new FileInputStream(files[i]).getChannel();
                resultFileChannel.transferFrom(blk, resultFileChannel.size(), blk.size());
                blk.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } finally {
            closeQuietly(fileOutputStream);
            closeQuietly(resultFileChannel);
        }
        return true;
    }

    /**
     * 拷贝文件
     */
    public static boolean nioCopyFile(File source, File target) {
        FileChannel in = null;
        FileChannel out = null;
        FileInputStream inStream = null;
        FileOutputStream outStream = null;
        try {
            inStream = new FileInputStream(source);
            outStream = new FileOutputStream(target);
            in = inStream.getChannel();
            out = outStream.getChannel();
            in.transferTo(0, in.size(), out);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } finally {
            closeQuietly(inStream);
            closeQuietly(in);
            closeQuietly(outStream);
            closeQuietly(out);
        }
        return true;
    }

    /**
     * 关闭流
     */
    public static void closeQuietly(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (Exception ignored) {

            }
        }
    }


    /**
     * 删除 MMap 文件末尾的空白
     */
    public static void deleteMMapFileBlankContent(File file) {
        if (!isFileExist(file)) {
            return;
        }
        RandomAccessFile raf = null;
        try {
            raf = new RandomAccessFile(file, "rw");
            long len = raf.length();
            if (len <= 3) {
                return;
            }
            long pos = len - 1;
            while (pos > 0) {
                --pos;
                raf.seek(pos);
                if (raf.readByte() == '\n') {
                    break;
                }
            }
            raf.getChannel().truncate(pos > 0 ? pos + 1 : pos).close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            closeQuietly(raf);
        }
    }


    private static boolean isSDEnough() {
        return getSDFreeSize() >= PenConstant.MIN_SDCARD_FREE_SPACE_MB;
    }

    private static long getSDFreeSize() {
        try {
            File path = Environment.getExternalStorageDirectory();
            StatFs sf = new StatFs(path.getPath());
            long blockSize = sf.getBlockSizeLong();
            long freeBlocks = sf.getAvailableBlocksLong();
            return (freeBlocks * blockSize) / 1024 / 1024;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return PenConstant.MIN_SDCARD_FREE_SPACE_MB;
    }


}