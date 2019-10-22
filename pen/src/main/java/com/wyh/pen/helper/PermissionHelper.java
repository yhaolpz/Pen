package com.wyh.pen.helper;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.annotation.RestrictTo;
import androidx.core.content.ContextCompat;

@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
public class PermissionHelper {

    private PermissionHelper() {
    }

    private static boolean hasStoragePermission = false;

    public static boolean hasWriteAndReadStoragePermission(Context context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        //如果已经有权限了，就不再检查
        if (hasStoragePermission) {
            return true;
        }
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            hasStoragePermission = false;
            return false;
        }
        hasStoragePermission = true;
        return true;
    }

}
