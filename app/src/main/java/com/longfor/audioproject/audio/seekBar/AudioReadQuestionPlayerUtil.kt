package com.longfor.audioproject.audio.seekBar

import android.media.MediaPlayer
import androidx.lifecycle.LifecycleObserver
import java.util.*

/**
 * @Create By   : wangjun
 * @Create Time : 2021/3/1
 * @description : 音频播放器控制类
 **/
object AudioReadQuestionPlayerUtil : LifecycleObserver {

    private var mediaPlayer: MediaPlayer? = null
    private var listener: IAudioPlayerListener? = null
    private var url = ""
    var isInit: Boolean = false
    var shouldRelease: Boolean = false
    var isPrepared: Boolean = false

    /**
     * 是否正在播放中
     */
    var isPlaying = false

    /**
     * 开始播放
     */
    fun initPlayer(currentUrl: String) {
        try {
            if (!isInit) {
                this.url = currentUrl
                mediaPlayer = MediaPlayer()
                isInit = true
                mediaPlayer!!.setDataSource(url)
                mediaPlayer!!.setOnPreparedListener {
                    isPrepared = true
                    listener?.onCanTouchSeekBar(isPrepared)
                    shouldRelease = true
                    listener?.let { audioListener ->
                        audioListener.onPrepared()
                    }
                }
                mediaPlayer!!.setOnCompletionListener {
                    restMediaPlayer()
                }

                mediaPlayer!!.setOnErrorListener { _, _, _ ->
                    stopPlay()
                    false
                }
                mediaPlayer!!.isLooping = false
                mediaPlayer!!.prepareAsync()

            }
        } catch (ex: Exception) {
            ex.printStackTrace()
            stopPlay()
        }
    }

    private var timer: Timer? = null
    private fun doTimer() {
        timer?.cancel()
        timer = Timer()
        timer?.schedule(object : TimerTask() {
            override fun run() {
                listener?.onCurrentPosition(getCurrentPosition())
            }
        }, 0, 50)
    }

    /**
     * 设置播放暂停
     */
    private fun stopPlay() {
        isPlaying = false
        listener?.onPlayStateChange(isPlaying)
    }

    /**
     * 重置
     */
    private fun restMediaPlayer() {
        try {
            stopPlay()
            listener?.let { audioListener ->
                audioListener.onPause(this.url, false)
                // 重置
                mediaPlayer!!.reset()
                isPrepared = false
                listener?.onCanTouchSeekBar(isPrepared)
                mediaPlayer!!.setDataSource(url)
                mediaPlayer!!.isLooping = false
                mediaPlayer!!.prepareAsync()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * 停止timer
     */
    fun timerStop() {
        timer?.cancel()
    }

    /**
     * 开始播放
     */
    fun play() {
        try {
            if (isPrepared) {
                mediaPlayer?.start()
                isPlaying = true
                listener?.onPlayStateChange(isPlaying)
                doTimer()
            }

        } catch (ex: Exception) {
            stopPlay()
            ex.printStackTrace()
        }
    }

    /**
     * 获得当前进度
     */
    fun getCurrentPosition(): Int? {
        return try {
            mediaPlayer?.currentPosition
        } catch (ex: Exception) {
            ex.printStackTrace()
            null
        }
    }

    /**
     * 获取mediaPlayer
     */
    fun getMediaPlayer(): MediaPlayer? {
        return mediaPlayer
    }

    /**
     * 停止所有音频播放，回调 onStop
     */
    fun pause() {
        try {
            stopPlay()
            mediaPlayer?.pause()
            listener?.let {
                it.onPause(url, false)
            }
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }

    /**
     * finish
     */
    fun finish() {
        try {
            stopPlay()
            if (shouldRelease) {
                isPrepared = false
                mediaPlayer?.stop()
                mediaPlayer?.release()
            }
            shouldRelease = false
            isInit = false
            if (timer != null) {
                timer!!.cancel()
                timer = null
            }
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }

    /**
     * 为 View 添加 IAudioPlayerListener
     * * 在 View Detach 时自动移除
     * * 在 Activity onDestroy 时也会自动移除
     */
    fun setAudioListener(listener: IAudioPlayerListener) {
        this.listener = listener
    }

    interface IAudioPlayerListener {
        fun onPlayStateChange(isPlaying: Boolean)
        fun onPrepared()
        fun onCurrentPosition(position: Int?)
        fun onPause(url: String, preStopAudio: Boolean)
        fun onCanTouchSeekBar(touch: Boolean)
    }
}