package com.longfor.audioproject.audio

import android.app.Activity
import android.media.MediaPlayer
import android.view.View
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.OnLifecycleEvent
import com.longfor.audioproject.lifecycleOwner
import java.util.concurrent.CopyOnWriteArraySet

/**
 * @Create By   : wj
 * @Create Time : 2021/9/6
 * @description : 音频播放器控制类
 **/
object AudioPlayerUtil {

    private var mediaPlayer: MediaPlayer? = null

    private var audioListeners = CopyOnWriteArraySet<IAudioPlayerListener>()

    var url = ""

    /**
     * 是否正在播放中
     */
    var isPlaying = false

    /**
     * 开始播放
     */
    fun play(url: String) {
        try {
            // 播放前先暂停和释放旧资源
            stop()

            // 开始播放
            this.url = url
            mediaPlayer = MediaPlayer()
            mediaPlayer?.setDataSource(url)
            mediaPlayer?.setOnErrorListener { _, what, extra ->
                Logger.e("what: $what; extra: $extra")
                true
            }
            mediaPlayer?.setOnPreparedListener {
                start()
            }
            mediaPlayer?.setOnCompletionListener {
                stop(preStopAudio = false, doComplete = true)
            }
            mediaPlayer?.setOnErrorListener { _, _, _ ->
                stop()
                false
            }
            mediaPlayer?.isLooping = false
            mediaPlayer?.prepareAsync()
        } catch (ex: Exception) {
            ex.printStackTrace()
            stop()
        }
    }

    /**
     * 开始播放
     */
    private fun start() {
        try {
            mediaPlayer?.start()
            isPlaying = true
            foreachIterator {
                it.onReady(url)
            }
        }catch (e:Exception){
            e.printStackTrace()
        }
    }

    /**
     * 停止所有音频播放，回调 onStop
     */
    fun stop(preStopAudio: Boolean = false, doComplete: Boolean = false) {
        try {
            if (!isPlaying) {
                // 未播放时
                return
            }
            isPlaying = false
            mediaPlayer?.stop()
            mediaPlayer?.release()
            mediaPlayer = null
            foreachIterator {
                it.onStop(url)
                if (doComplete) {
                    it.onComplete(url)
                }
            }
        }
        catch (e:Exception){
            e.printStackTrace()
        }
    }

    /**
     * 为 View 添加 IAudioPlayerListener
     * * 在 View Detach 时自动移除
     * * 在 Activity onDestroy 时也会自动移除
     */
    fun addListener(view: View, listener: IAudioPlayerListener) {
        audioListeners.add(listener)
        Logger.d("IAudioPlayerListener count: %d", audioListeners.size.toString())

        // Detach 时自动移除
        view.addOnAttachStateChangeListener(object : View.OnAttachStateChangeListener {
            override fun onViewAttachedToWindow(v: View?) {
                // no-op
            }

            override fun onViewDetachedFromWindow(v: View?) {
                audioListeners.remove(listener)
                Logger.d("IAudioPlayerListener count: %d", audioListeners.size.toString())
            }

        })
        view.context.lifecycleOwner()?.let {
            addLifecycleObserver(it, listener)
        }
    }

    /**
     * 为 LifecycleOwner（Activity、Fragment） 添加 IAudioPlayerListener
     * * 在 LifecycleOwner onDestroy 时也会自动移除
     */
    fun addListener(lifecycleOwner: LifecycleOwner?, listener: IAudioPlayerListener) {
        audioListeners.add(listener)
        Logger.d("IAudioPlayerListener count: %d", audioListeners.size.toString())

        addLifecycleObserver(lifecycleOwner, listener)
    }

    /**
     * 使用 LifecycleObserver 自动移除 IAudioPlayerListener
     */
    private fun addLifecycleObserver(lifecycleOwner: LifecycleOwner?, listener: IAudioPlayerListener) {
        lifecycleOwner?.lifecycle?.addObserver(object : LifecycleObserver {
            @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
            fun onDestroy() {
                audioListeners.remove(listener)
                Logger.d("IAudioPlayerListener count: %d", audioListeners.size.toString())
            }
        })
    }

    private fun foreachIterator(block: (IAudioPlayerListener) -> Unit) {
        audioListeners.forEach {
            block(it)
        }
    }

    interface IAudioPlayerListener {
        fun onReady(url: String)
        fun onStop(url: String)
        fun onComplete(url: String)
    }
}