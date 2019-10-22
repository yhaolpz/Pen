package com.wyh.pen.upload;

import java.io.File;

/**
 * 监听上传前文件是否已准备好
 */
public interface PrepareUploadListener {
    /**
     * 文件已准备好，可以上传
     *
     * @param zipFile 要上传的日志压缩包，上传成功后务必删除本地文件，否则会丢失或下次上传时重复上传
     */
    void readyToUpload(File zipFile);

    /**
     * 文件准备出错，不用上传了
     */
    void failToReady();
}
