package com.wj.audioproject.utils

import android.Manifest
import android.content.pm.PackageManager
import android.os.Environment
import androidx.core.app.ActivityCompat
import androidx.fragment.app.FragmentActivity
import com.wj.audioproject.MyApplication
import java.io.File

object StorageUtil {


    @JvmStatic
    fun hasStoragePermission() = ActivityCompat.checkSelfPermission(MyApplication.instance!!, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED

    /**
     * @param type: The type of storage directory to return. Should be one of
     * *          {@see Environment.DIRECTORY_MUSIC},
     * *          {@see Environment.DIRECTORY_PODCASTS},
     * *          {@see Environment.DIRECTORY_RINGTONES},
     * *          {@see Environment.DIRECTORY_ALARMS},
     * *          {@see Environment.DIRECTORY_NOTIFICATIONS},
     * *          {@see Environment.DIRECTORY_PICTURES},
     * *          {@see Environment.DIRECTORY_MOVIES},
     * *          {@see Environment.DIRECTORY_DOWNLOADS},
     * *          {@see Environment.DIRECTORY_DCIM},
     * *          {@see Environment.DIRECTORY_DOCUMENTS}.
     * *          or customer filed. eg: "test"
     * *          May not be null
     *
     * @return Returns the File path for the directory. Note that this directory
     *         may not yet exist, so you must make sure it exists before using
     *         it such as with {@link File#mkdirs File.mkdirs()}.
     */
    @JvmStatic
    @JvmOverloads
    fun getExternalStorageDirPath(
            type: String,
            granted: Boolean = hasStoragePermission(),
            mkdirs: Boolean = true
    ): String {
        return getExternalStorageDir(type, granted, mkdirs).absolutePath
    }

    /**
     * @param type to the same [getExternalStorageDirPath]
     */
    @JvmStatic
    @JvmOverloads
    fun getExternalStorageDir(
            type: String,
            granted: Boolean = ActivityCompat.checkSelfPermission(MyApplication.instance!!, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED,
            mkdirs: Boolean = true): File {
        val result = if (isExternalStorageReadableAndWritable()) {
            if (granted) {
                Environment.getExternalStorageDirectory()
            } else {
                return getExternalFilesDir(type, mkdirs)
            }
        } else {
            MyApplication.instance!!.filesDir
        }
        return File(result, type).apply {
            if (!exists() && mkdirs) this.mkdirs()
        }
    }

    /**
     * @param type The type of files directory to return. May be {@code null}
     *             for the root of the files directory or one of the following
     *             constants for a subdirectory:
     * *           {@see android.os.Environment#DIRECTORY_MUSIC},
     * *           {@see android.os.Environment#DIRECTORY_PODCASTS},
     * *           {@see android.os.Environment#DIRECTORY_RINGTONES},
     * *           {@see android.os.Environment#DIRECTORY_ALARMS},
     * *           {@see android.os.Environment#DIRECTORY_NOTIFICATIONS},
     * *           {@see android.os.Environment#DIRECTORY_PICTURES}, or
     * *           {@see android.os.Environment#DIRECTORY_MOVIES}.
     * @return the absolute path to application-specific directory.  May return
     *         {@code null} if shared storage is not currently available.
     */
    @JvmStatic
    @JvmOverloads
    fun getExternalFilesDirPath(type: String, mkdirs: Boolean = true): String {
        return getExternalFilesDir(type, mkdirs).absolutePath
    }

    /**
     * @param type to the same [getExternalFilesDirPath]
     */
    @JvmStatic
    @JvmOverloads
    fun getExternalFilesDir(type: String = "", mkdirs: Boolean = true): File {
        val result = if (isExternalStorageReadableAndWritable()) {
            // AppDelegate.getApplication().getExternalCacheDir(realType) ???????????????'realType'??????.???????????????????????????????????????
            MyApplication.instance!!.getExternalFilesDir("")
                    ?: MyApplication.instance!!.filesDir
        } else {
            MyApplication.instance!!.filesDir
        }
        return File(result, type).apply {
            if (!exists() && mkdirs) this.mkdirs()
        }
    }

    @JvmStatic
    fun getExternalCacheDirPath(uniqueName: String = "") = getExternalCacheDir(uniqueName).absolutePath

    @JvmStatic
    fun getExternalCacheDir(uniqueName: String = ""): File {
        val result = if (isExternalStorageReadableAndWritable()) {
            MyApplication.instance!!.externalCacheDir ?: MyApplication.instance!!.cacheDir
        } else {
            MyApplication.instance!!.cacheDir
        }
        return File(result, uniqueName)
    }

    /**
     * ?????????????????????????????????
     * Checks if external storage is available for read and write
     */
    @JvmStatic
    fun isExternalStorageReadableAndWritable(): Boolean {
        return Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED || !Environment.isExternalStorageRemovable()
    }

    /**
     * ??????????????????????????????
     * Checks if external storage is available to at least read
     */
    @JvmStatic
    fun isExternalStorageReadable(): Boolean {
        return Environment.getExternalStorageState() in
                setOf(Environment.MEDIA_MOUNTED, Environment.MEDIA_MOUNTED_READ_ONLY)
    }

}