package com.longfor.audioproject.audio.bgm

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.OnLifecycleEvent

/**
 * 监听Activity的生命周期，进入第二层(或第n层)页面后，前一个页面的BGM不停，同时用当前页面的生命周期控制前一个页面的BGM
 */
class BgBgmObserver(val parent: BgmObserver, private val releaseCallback: (() -> Any?)? = null) : BgmObserver() {

    override fun enablePause(enablePause: Boolean) {
        parent.enablePause(enablePause)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    override fun onResume(owner: LifecycleOwner) {
        parent.onResume(owner)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    override fun onPause(owner: LifecycleOwner) {
        // BGM 暂停
        parent.onPause(owner)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    override fun onDestroy(owner: LifecycleOwner) {
        // BGM 恢复
        parent.let {
            it.willResume = false
            it.enablePause(true)
        }
        releaseCallback?.invoke()
    }
}