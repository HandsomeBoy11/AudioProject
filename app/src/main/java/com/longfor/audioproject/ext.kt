package com.longfor.audioproject

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.fragment.app.FragmentActivity
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

/**
 * 防止应用市场测试手机崩溃
 *
 * @return Context == null || Context !is Activity , 返回 true
 *
 * @see Activity.isFinishing
 * @see Activity.isDestroyed
 */
@RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
fun Context?.isFinishingOrDestroyed(): Boolean {
    return when (val activity = this.activity()) {
        null -> true
        else -> activity.isFinishing || activity.isDestroyed
    }
}

fun Context?.getActivity(): Activity? = when {
    this == null -> null
    this is Activity -> this
    this is ContextWrapper -> this.baseContext.activity()
    else -> null
}

fun Context?.fragmentActivity(): FragmentActivity? = when {
    this == null -> null
    this is FragmentActivity -> this
    this is ContextWrapper -> this.baseContext.fragmentActivity()
    else -> null
}