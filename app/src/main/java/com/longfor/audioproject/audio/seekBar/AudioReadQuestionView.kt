package com.longfor.audioproject.audio.seekBar

import android.content.Context
import android.media.AudioManager
import android.media.AudioManager.OnAudioFocusChangeListener
import android.os.SystemClock
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import android.widget.SeekBar
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import com.longfor.audioproject.R
import com.longfor.audioproject.lifecycleOwner
import kotlinx.android.synthetic.main.audio_read_question_view_layout.view.*
import java.text.SimpleDateFormat


/**
 *
 * @Description:    音频读题控件
 * @Author:         wangjun
 * @CreateDate:     2021/3/1
 *
 */
class AudioReadQuestionView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet?,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr), LifecycleObserver {
    private val format = SimpleDateFormat("mm:ss");
    private var isSeekBarChanging: Boolean = false

    /**
     * 上次播放的位置
     */
    private var lastPosition: Int = 0
    private var startTime: Long = 0
    private var url: String? = null
    private var mOnAudioFocusChangeListener: OnAudioFocusChangeListener? = null

    init {
        LayoutInflater.from(context).inflate(R.layout.audio_read_question_view_layout, this, true)
        initView()
    }

    fun initPlayer(url: String, totalTime: Int = -1) {
        this.url = url
        if (AudioReadQuestionPlayerUtil.isPlaying) {
            playIv.isSelected = true
            allTime?.text = format.format(
                AudioReadQuestionPlayerUtil.getMediaPlayer()?.duration
                    ?: 0
            )
        } else {
            if (totalTime != -1)
                allTime?.text = format.format(totalTime * 1000)
        }
        AudioReadQuestionPlayerUtil.initPlayer(url)
        AudioReadQuestionPlayerUtil.setAudioListener(object :
            AudioReadQuestionPlayerUtil.IAudioPlayerListener {
            override fun onPlayStateChange(isPlaying: Boolean) {
                playIv.isSelected = isPlaying
            }

            override fun onPrepared() {
                seekBar?.max = AudioReadQuestionPlayerUtil.getMediaPlayer()?.duration ?: 0
                allTime?.text = format.format(
                    AudioReadQuestionPlayerUtil.getMediaPlayer()?.duration
                        ?: 0
                )
                seekBar?.progress = 0
            }

            override fun onCurrentPosition(position: Int?) {
                if (!isSeekBarChanging) {
                    post {
                        current_time?.text = format.format(position)
                        seekBar?.progress = position ?: 0
                    }
                }
            }

            override fun onPause(url: String, preStopAudio: Boolean) {
                playIv.isSelected = false
            }

            override fun onCanTouchSeekBar(touch: Boolean) {
                seekBar?.setTouch(touch)
            }

        })
    }

    private var mAudioManager: AudioManager? = null
    private var isAudioFocus = false


    private fun initView() {
        context.lifecycleOwner()?.lifecycle?.addObserver(this)
        // 进度条监听
        seekBar.setOnSeekBarChangeListener(AudioSeekBarListener())
        // 播放与暂停
        playView.setOnClickListener {
            if (AudioReadQuestionPlayerUtil.isPlaying) {
                AudioReadQuestionPlayerUtil.pause()
            } else {
//                if (PlayerManager.getInstance().isPlaying) {
//                    PlayerManager.getInstance().pause()
//                    MiniBarHelper.hideMiniBar()
//                }
//                if (AudioPlayerUtil.isPlaying) {
//                    AudioPlayerUtil.stop()
//                }
                AudioReadQuestionPlayerUtil.play()
                startTime = SystemClock.elapsedRealtime()
                lastPosition = (AudioReadQuestionPlayerUtil.getCurrentPosition() ?: 0) / 1000
            }
        }

        /**
         * audioFocusListener
         */
        mOnAudioFocusChangeListener = OnAudioFocusChangeListener { focusChange ->
            when (focusChange) {
                AudioManager.AUDIOFOCUS_GAIN -> {
                    if (!AudioReadQuestionPlayerUtil.isPlaying && isAudioFocus && !url.isNullOrEmpty()) {
                        AudioReadQuestionPlayerUtil.play()
                    }
                    isAudioFocus = false
                }
                AudioManager.AUDIOFOCUS_LOSS, AudioManager.AUDIOFOCUS_LOSS_TRANSIENT, AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK -> {
                    isAudioFocus = AudioReadQuestionPlayerUtil.isPlaying
                    if (AudioReadQuestionPlayerUtil.isPlaying) {
                        AudioReadQuestionPlayerUtil.pause()
                    }
                }
                else -> {
                }
            }
        }
        if (mAudioManager == null) {
            mAudioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        }
        mAudioManager?.requestAudioFocus(
            mOnAudioFocusChangeListener,
            AudioManager.STREAM_MUSIC,
            AudioManager.AUDIOFOCUS_GAIN
        )
    }


    /**
     * 进度条处理
     */
    inner class AudioSeekBarListener : SeekBar.OnSeekBarChangeListener {

        override fun onProgressChanged(
            seekBar: SeekBar, progress: Int,
            fromUser: Boolean
        ) {}

        /**
         * 滚动时,应当暂停后台定时器
         */
        override fun onStartTrackingTouch(seekBar: SeekBar) {
            isSeekBarChanging = true
        }

        /**
         * 滑动结束后，重新设置值
         */
        override fun onStopTrackingTouch(seekBar: SeekBar) {
            isSeekBarChanging = false
            if (!AudioReadQuestionPlayerUtil.isPlaying && seekBar.progress == seekBar.max) {
                AudioReadQuestionPlayerUtil.timerStop()
                if (AudioReadQuestionPlayerUtil.getMediaPlayer() != null)
                    AudioReadQuestionPlayerUtil.getMediaPlayer()!!.seekTo(0)
                current_time?.text = format.format(seekBar.progress)
            } else {
                if (AudioReadQuestionPlayerUtil.getMediaPlayer() != null)
                    AudioReadQuestionPlayerUtil.getMediaPlayer()!!.seekTo(seekBar.progress)
                current_time?.text = format.format(
                    AudioReadQuestionPlayerUtil.getCurrentPosition()
                        ?: 0
                )
            }
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun onStopAudio() {
        if (AudioReadQuestionPlayerUtil.isPlaying) {
            AudioReadQuestionPlayerUtil.pause()
        }
    }

    /**
     * 结束时调用
     */
    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun onDestroyAudio() {
        isSeekBarChanging = true
        if (AudioReadQuestionPlayerUtil.shouldRelease) {
            AudioReadQuestionPlayerUtil.finish()
        }
        if (mAudioManager != null) {
            mAudioManager!!.abandonAudioFocus(mOnAudioFocusChangeListener)
            mAudioManager = null
            isAudioFocus = false
        }
    }
}