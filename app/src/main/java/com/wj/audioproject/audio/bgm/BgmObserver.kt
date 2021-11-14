package com.wj.audioproject.audio.bgm


import android.app.Activity
import android.media.MediaPlayer
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.OnLifecycleEvent
import com.wj.audioproject.MyApplication
import com.wj.audioproject.R
import com.wj.audioproject.audio.Logger

/**
 * 监听Activity的生命周期
 */
open class BgmObserver(private val releaseCallback: (() -> Any?)? = null) : LifecycleObserver {
    private val context by lazy { MyApplication.instance }

    /** 是否会在 onResume 时自动恢复播放 */
    internal var willResume = false
    internal var interruptAudio: Boolean = true
    private var currentPath: String? = null
    private var currentId: Int? = null
    private var mediaPlayer: MediaPlayer? = null

    /**
     * 当不需要在 onStop 时暂停BGM时，需要设为 false；
     * 注意需要在需要暂停时恢复为 true
     */
    private var enablePause = true

    open fun enablePause(enablePause: Boolean) {
        this.enablePause = enablePause
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    open fun onResume(owner: LifecycleOwner) {
        Logger.d("BGM MediaPlayer onResume : %s", owner.toString())
//        if (PlayerManager.getInstance().isPlaying) {
//            if (interruptAudio) {
//                PlayerManager.getInstance().pause()
//            } else {
//                willResume = false
//            }
//        }
        if (willResume) {
            willResume = false
            try {
                mediaPlayer?.start()
            } catch (e: Exception) {
                Logger.e( "$e BGM MediaPlayer resume failed : %s", owner.toString())
            }
            Logger.d("BGM MediaPlayer resume : %s", owner.toString())
        }
        enablePause = true
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    open fun onPause(owner: LifecycleOwner) {
        Logger.d("BGM MediaPlayer onPause : %s", owner.toString())
        if (enablePause && isPlaying() && !(owner is Activity && owner.isFinishing)) {
            pause(true)
            Logger.d("BGM MediaPlayer pause : %s", owner.toString())
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    open fun onDestroy(owner: LifecycleOwner) {
        Logger.d("BGM MediaPlayer onDestroy : %s", owner.toString())
        stop()
        releaseCallback?.invoke()
    }

    fun pause(willResume: Boolean = true) {
        try {
            mediaPlayer?.pause()
            this.willResume = willResume
        } catch (e: Exception) {
            Logger.e(e, "pause BGM MediaPlayer")
        }
    }

    fun isPlaying(): Boolean {
        return try {
            mediaPlayer?.isPlaying
        } catch (e: Exception) {
            Logger.e(e, "get isPlaying BGM MediaPlayer")
            false
        } ?: false
    }

    private fun setCurrent(bgmPath: String?, bgmId: Int?) {
        currentPath = bgmPath
        currentId = bgmId
    }

    /** 播放id指定的音频文件 */
    private fun playById(bgmId: Int = R.raw.default_bgm, reset: Boolean = false) {
        if (!reset && currentId != null && bgmId == currentId)
            return
        setCurrent(null, bgmId)
        mediaPlayer = MediaPlayer.create(context, bgmId)
        mediaPlayer?.apply {
            isLooping = true
            start()
        }
    }

    /** 播放path指定的音频 */
    private fun playByPath(path: String?, reset: Boolean) {
        if (!reset && currentPath != null && path == currentPath)
            return
        setCurrent(path, null)
        mediaPlayer = MediaPlayer()

        mediaPlayer?.apply {
            setDataSource(path)
            setOnPreparedListener {
                isLooping = true
                start()
            }
            prepare()
        }
    }

    fun playPathOrId(path: String?, bgmId: Int?, reset: Boolean) {
        if (!path.isNullOrEmpty()) {
            playByPath(path, reset)
        } else if (bgmId != null) {
            playById(bgmId, reset)
        } else {
            playById(reset = reset)
        }
    }

    /**
     * 停止播放背景音乐
     */
    fun stop() {
        try {
            mediaPlayer?.apply {
                stop()
                release()
                Logger.d("stop BGM MediaPlayer")
            }
        } catch (e: Exception) {
            Logger.e(e, "stop BGM MediaPlayer failed")
        }
        mediaPlayer = null
        setCurrent(null, null)
    }
}