package com.longfor.audioproject.audio

import android.content.Context
import android.media.AudioManager
import android.media.MediaPlayer
import java.io.IOException

/**
 * Description:播放本地音频
 * Data：2018/10/15-下午2:11
 * Author:wj
 */
object PlayNativeAudioUtils {
    /**
     * 答题错误
     */
    const val ANSWER_FAIL = "answer_fail.mp3"

    /**
     * 答题正确
     */
    const val ANSWER_RIGHT = "answer_right.mp3"


    /**
     * 签到成功
     */
    const val COIN_AUIDO = "coin_auido.mp3"

    /**
     * 小组讨论发言完毕
     */
    const val DISCUSS_COIN_AUIDO = "discuss_coin.mp3"

    /**
     * 签到成功无金币
     */
    const val SIGN_NO_COIN_AUDIO = "sign_no_coin_audio.mp3"

    /**
     * 城堡完成搭建音效
     */
    const val FIRST_CASTLE_COMPLETE_AUDIO = "first_castle_complete.mp3"
    const val SECOND_CASTLE_COMPLETE_AUDIO = "second_castle_complete.mp3"
    const val THIRD_CASTLE_COMPLETE_AUDIO = "third_castle_complete.mp3"

    private var mPlayer: MediaPlayer? = null
    private var isPlaying: Boolean = false

    fun playAudios(autioName: String, mContext: Context?, playListener: PlayListener?) {
        try {
            if (isPlaying) {
                return
            }
            mPlayer = MediaPlayer()
            isPlaying = true
            val fileDescriptor = mContext?.assets?.openFd(autioName)
            fileDescriptor?.let {
                mPlayer?.setDataSource(
                        it.fileDescriptor,
                        it.startOffset,
                        it.length)
            }
            mPlayer?.prepare()
            val mAudioManager: AudioManager = mContext?.getSystemService(Context.AUDIO_SERVICE) as AudioManager
            val result = mAudioManager.requestAudioFocus({ focusChange ->
                when (focusChange) {
                    //重新获取焦点
                    AudioManager.AUDIOFOCUS_GAIN -> {
                        mPlayer?.setVolume(1f, 1f)
                    }
                    //暂时失去焦点
                    AudioManager.AUDIOFOCUS_LOSS_TRANSIENT -> {
                        mPlayer?.setVolume(1f, 1f)
                    }
                    //时期焦点
                    AudioManager.AUDIOFOCUS_LOSS -> {
                        mPlayer?.setVolume(1f, 1f)
                    }
                }
            }, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN)
            if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
                //请求成功，这里你就可以开始播放了  （比如这时候在拨打电话，那请求是不成功的）
                mPlayer?.start()
            } else {
                playListener?.onError()
            }

            mPlayer?.setOnErrorListener { mp, what, extra ->
                playListener?.complete()
                isPlaying = false
                return@setOnErrorListener false
            }
            mPlayer?.setOnCompletionListener {
                isPlaying = false
                playListener?.complete()
            }
        } catch (e: IOException) {
            isPlaying = false
            playListener?.complete()
        }
    }


    fun stopAudios() {
        isPlaying = false
        mPlayer?.stop()
    }

    interface PlayListener {
        fun complete()
        fun onError()
    }
}