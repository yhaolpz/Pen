package com.wyh.pen.record;

import androidx.annotation.AnyThread;
import androidx.annotation.NonNull;
import androidx.annotation.RestrictTo;

import android.text.TextUtils;

import com.wyh.pen.core.Pen;
import com.wyh.pen.core.PenConfig;
import com.wyh.pen.core.PenConstant;
import com.wyh.pen.core.PenExecutor;
import com.wyh.pen.core.PenTag;
import com.wyh.pen.helper.FileHelper;
import com.wyh.pen.helper.PermissionHelper;
import com.wyh.pen.upload.PrepareUploadListener;
import com.wyh.pen.util.DateUtil;
import com.wyh.pen.util.ZipUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
public class LogRecorderImpl {


    private final NIOMMapLogWriter logWriter;
    private final PenConfig mConfig;
    private final StringBuilder content = new StringBuilder();

    public LogRecorderImpl(final PenConfig config) {
        this.mConfig = config;
        this.logWriter = new NIOMMapLogWriter(config);
    }

    @AnyThread
    public void record(final int debugLevel, final String tag, final String msg) {
        if (debugLevel < mConfig.getRecordDebugLevel() || TextUtils.isEmpty(tag) || TextUtils.isEmpty(msg)) {
            return;
        }
        if (!PermissionHelper.hasWriteAndReadStoragePermission(mConfig.getContext())) {
            Pen.printE(PenTag.INTERNAL_TAG, "log-->!hasWriteAndReadStoragePermission");
            return;
        }
        PenExecutor.executeWork(new Runnable() {
            @Override
            public void run() {
                logWriter.write(format(debugLevel, tag, msg));
            }
        });
    }

    /**
     * 准备即将上传日志文件
     * 日志存储的文件名为 "2019-6-11-mmap-new" 或 "2019-6-11-mmap-old"
     * 这里要做的工作就是将当前存储的日志文件压缩加密为 zip 文件
     */
    @AnyThread
    public void prepareUploadAsync(@NonNull final PrepareUploadListener listener) {
        PenExecutor.executeWork(new Runnable() {
            @Override
            public void run() {
                //停止当前文件的写入与映射
                logWriter.stopWrite();
                //删除过期文件
                FileHelper.cleanOverdueFile(new File(mConfig.getLogDir()).listFiles());
                //合并同一天的日志文件，添加 txt 后缀
                ArrayList<File> txtFiles = new ArrayList<>();
                List<File> logFiles = FileHelper.filterFile(mConfig.getLogDir(), PenConstant.MMAP_NEW);
                for (File file : logFiles) {
                    String oldFileName = file.getName().replace(PenConstant.MMAP_NEW, PenConstant.MMAP_OLD);
                    File oldFile = new File(file.getParent(), oldFileName);
                    String resultPath = file.getName().replace(PenConstant.MMAP_NEW, PenConstant.TXT);
                    File resultFile = new File(file.getParent(), resultPath);
                    if (oldFile.exists()) {
                        FileHelper.mergeFiles(new String[]{file.getPath(), oldFile.getPath()}, resultFile.getPath());
                    } else {
                        FileHelper.mergeFiles(new String[]{file.getPath()}, resultFile.getPath());
                    }
                    if (FileHelper.isFileExist(resultFile)) {
                        txtFiles.add(resultFile);
                    }
                }
                //压缩所有 txt 文件为一个 zip 文件
                final String zipFilePath = mConfig.getLogDir() + File.separator + DateUtil.getTime() + PenConstant.ZIP;
                final File zipFile = ZipUtil.doZipFilesWithPassword(txtFiles, zipFilePath, mConfig.getCipherKey());
                Pen.print(PenTag.INTERNAL_TAG, "LogRecorderImpl--> prepareUploadAsync zip done  mConfig.getCipherKey()=" + mConfig.getCipherKey());
                //删除 txt 文件
                for (File file : txtFiles) {
                    Pen.print(PenTag.INTERNAL_TAG, "LogRecorderImpl--> prepareUploadAsync delete txtFile:" + file.getPath());
                    FileHelper.deleteFileQuietly(file);
                }
                PenExecutor.executeMain(new Runnable() {
                    @Override
                    public void run() {
                        if (FileHelper.isFileExist(zipFile)) {
                            Pen.print(PenTag.INTERNAL_TAG, "LogRecorderImpl--> prepareUploadAsync zipFile:" + zipFile.getAbsolutePath());
                            listener.readyToUpload(zipFile);
                        } else {
                            listener.failToReady();
                        }
                    }
                });

            }
        });
    }


    private String format(@Pen.DebugLevel int debugLevel, String tag, String msg) {
        if (content.length() > 0) {
            content.delete(0, content.length());
        }
        content.append(DateUtil.getHour());
        content.append(PenConstant.FIELD_SEPERATOR);
        content.append(getLevelStr(debugLevel));
        content.append(tag);
        content.append(": ");
        content.append(msg);
        content.append('\n');
        return content.toString();
    }


    private String getLevelStr(@Pen.DebugLevel int debugLevel) {
        switch (debugLevel) {
            case Pen.DebugLevel.VERBOSE:
                return "V/";
            case Pen.DebugLevel.DEBUG:
                return "D/";
            case Pen.DebugLevel.INFO:
                return "I/";
            case Pen.DebugLevel.WARNING:
                return "W/";
            case Pen.DebugLevel.ERROR:
                return "E/";
            case Pen.DebugLevel.ALL:
            case Pen.DebugLevel.NONE:
                return "";
        }
        return "";
    }

}
