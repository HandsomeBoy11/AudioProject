package com.wj.audioproject

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.wj.audioproject.audio.AudioPlayerUtil
import com.wj.audioproject.audio.PlayNativeAudioUtils
import com.wj.audioproject.audio.PlayNativeAudioUtils.FIRST_CASTLE_COMPLETE_AUDIO
import com.wj.audioproject.audio.SoundPoolPlayer
import com.wj.audioproject.audio.bgm.BgmHelper
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), View.OnClickListener {
    private val bgUrl: String = "https://m3.8js.net:99/20210522/benfuxingkong-yinximian.mp3"
    private var bgPlay: Boolean = false
    private var nativePlay: Boolean = false
    private val soundPoolPlayer by lazy {
        SoundPoolPlayer(lifecycle).apply {
            preloadRes(R.raw.castle_patch_get, R.raw.castle_coin_get)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        clickEvent()
        initAudio()
    }

    private fun initAudio() {
        // 带有seekBar的播放器
        audioReadQuestionView.initPlayer(bgUrl)
    }

    private fun clickEvent() {
        btn1.setOnClickListener(this)
        btn12.setOnClickListener(this)
        btn2.setOnClickListener(this)
        btn3.setOnClickListener(this)
        btn4.setOnClickListener(this)
        btn5.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            // 简短铃声
            R.id.btn1 -> soundPoolPlayer.play(R.raw.castle_patch_get)
            R.id.btn12 -> soundPoolPlayer.play(R.raw.castle_coin_get)
            // 背景音乐的播放和暂停
            R.id.btn2 -> {
                bgPlay = !bgPlay
                if (bgPlay) {
                    BgmHelper.play(lifecycle, bgUrl, bgmId = null, interruptAudio = false)
                    btn2.text = getString(R.string.pausePlay)
                } else {
                    BgmHelper.stop(lifecycle)
                    btn2.text = getString(R.string.rePlay)
                }
            }
            // 播放本地音频
            R.id.btn3 -> {
                nativePlay = !nativePlay
                if (nativePlay) {
                    PlayNativeAudioUtils.playAudios(FIRST_CASTLE_COMPLETE_AUDIO, this, null)
                    btn3.text = getString(R.string.pausePlay)
                } else {
                    PlayNativeAudioUtils.stopAudios()
                    btn3.text = getString(R.string.rePlay)
                }
            }
            // 播放网络音频
            R.id.btn4 -> {
                if (AudioPlayerUtil.isPlaying) {
                    AudioPlayerUtil.stop()
                    btn4.text = getString(R.string.rePlay)
                } else {
                    AudioPlayerUtil.play(bgUrl)
                    btn4.text = getString(R.string.pausePlay)
                }
            }
            // 底部弹出录音
            R.id.btn5 -> {
                startActivity(Intent(this, AudioRecordActivity::class.java))
            }

        }
    }
}