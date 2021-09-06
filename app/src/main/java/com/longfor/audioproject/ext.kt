package com.longfor.audioproject

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import androidx.lifecycle.LifecycleOwner

/**
 *
 *  @author wangjun
 *  @date  2021/9/6 16:57
 *  @Des  :
 *
 */

fun Context?.activity(): Activity? = when {
    this == null -> null
    this is Activity -> this
    this is ContextWrapper -> this.baseContext.activity()
    else -> null
}
fun Context?.lifecycleOwner(): LifecycleOwner? = this.activity()?.let {
    if (it is LifecycleOwner) {
        it
    } else {
        null
    }
}