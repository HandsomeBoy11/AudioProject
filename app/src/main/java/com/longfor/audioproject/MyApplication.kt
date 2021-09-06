package com.longfor.audioproject

import android.app.Application

/**
 *
 *  @author wangjun
 *  @date  2021/9/6 14:16
 *  @Des  :
 *
 */
class MyApplication : Application() {
    companion object {
        var instance: MyApplication? = null
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
    }
}