package com.wyh.pen.util;

import android.text.TextUtils;

import androidx.annotation.RestrictTo;

import com.wyh.pen.core.Pen;

import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.util.Zip4jConstants;

import java.io.File;
import java.util.ArrayList;

@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
public class ZipUtil {

    private static final String TAG = "ZipUtil";

    private ZipUtil() {
    }

    /**
     * 对文件列表压缩加密
     */
    public static File doZipFilesWithPassword(ArrayList<File> srcFiles, String destZipFile, String password) {
        if (srcFiles == null || srcFiles.size() == 0) {
            return null;
        }
        ZipParameters parameters = new ZipParameters();
        // 压缩方式
        parameters.setCompressionMethod(Zip4jConstants.COMP_DEFLATE);
        // 压缩级别
        parameters.setCompressionLevel(Zip4jConstants.DEFLATE_LEVEL_NORMAL);
        // 加密方式
        if (!TextUtils.isEmpty(password)) {
            parameters.setEncryptFiles(true);
            parameters.setEncryptionMethod(Zip4jConstants.ENC_METHOD_STANDARD);
            parameters.setPassword(password);
        }
        try {
            ZipFile zipFile = new ZipFile(destZipFile);
            zipFile.addFiles(srcFiles, parameters);
            return zipFile.getFile();
        } catch (ZipException e) {
            Pen.e(TAG, e);
            return null;
        }
    }


    /**
     * 对文件夹加密
     */
    public static File doZipFilesWithPassword(File folder, String destZipFile, String password) {
        return doZipFileWithPassword(folder, destZipFile, password);
    }


    /**
     * 单文件压缩并加密
     *
     * @param file        要压缩的zip文件
     * @param destZipFile zip保存路径
     * @param password    密码   可以为null
     */
    public static File doZipFileWithPassword(File file, String destZipFile, String password) {
        if (!file.exists()) {
            return null;
        }
        ZipParameters parameters = new ZipParameters();
        // 压缩方式
        parameters.setCompressionMethod(Zip4jConstants.COMP_DEFLATE);
        // 压缩级别
        parameters.setCompressionLevel(Zip4jConstants.DEFLATE_LEVEL_NORMAL);
        // 加密方式
        if (!TextUtils.isEmpty(password)) {
            parameters.setEncryptFiles(true);
            parameters.setEncryptionMethod(Zip4jConstants.ENC_METHOD_STANDARD);
            parameters.setPassword(password);
        }
        try {
            ZipFile zipFile = new ZipFile(destZipFile);
            if (file.isDirectory()) {
                zipFile.addFolder(file, parameters);
            } else if (file.isFile()) {
                zipFile.addFile(file, parameters);
            }
            return zipFile.getFile();
        } catch (ZipException e) {
            Pen.e(TAG, e);
            return null;
        }
    }


}
