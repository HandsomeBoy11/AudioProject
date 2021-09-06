package com.longfor.audioproject.audio

import android.util.Log
import java.lang.Exception

/**
 *
 *  @author wangjun
 *  @date  2021/9/6 14:13
 *  @Des  :
 *
 */
object Logger {
    private val Tag: String = "Logger:: "
    fun i(msg: String) {
        Log.i(Tag, msg)
    }

    fun d(msg: String, ext: String = "") {
        Log.d(Tag, msg + ext)
    }

    fun e(msg: String, ext: String = "") {
        Log.e(Tag, msg + ext)
    }

    fun e(e: Exception, msg: String, ext: String = "") {
        Log.e(Tag, "$e $msg $ext")
    }
}