package com.longfor.audioproject.audio.audioView

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.text.TextUtils
import android.util.AttributeSet
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import com.longfor.audioproject.R
import com.longfor.audioproject.audio.AudioPlayerUtil
import com.longfor.audioproject.audio.AudioRecorder
import com.longfor.audioproject.fragmentActivity
import com.longfor.audioproject.getActivity
import com.longfor.audioproject.isFinishingOrDestroyed
import com.longfor.audioproject.utils.LocalMedia
import com.longfor.audioproject.utils.MD5Tool
import com.longfor.audioproject.utils.StorageUtil
import com.longfor.audioproject.utils.ToastUtil
import com.longfor.audioproject.utils.timer.Timer
import com.tbruyelle.rxpermissions2.RxPermissions
import kotlinx.android.synthetic.main.layout_audio_record.view.*
import java.io.File
import java.io.IOException


/**
 * AudioRecordView
 * @author  created by liuxiaoliang@luojilab.com
 * @date 2019-10-29
 */
class AudioRecordView : ConstraintLayout, View.OnClickListener {

    companion object {
        /**
         * 开始录制
         * */
        const val STATUS_READY_START_RECORD = 0x1

        /**
         * 录制中
         * */
        const val STATUS_RECORDING = STATUS_READY_START_RECORD + 1

        /**
         * 停止录制
         * */
        const val STATUS_RECORDED = STATUS_RECORDING + 1

        /**
         * 试听语音
         * */
        const val STATUS_PRE_AUDIO = STATUS_RECORDED + 1

        private fun playAudio(filepath: String?) {
            if (TextUtils.isEmpty(filepath)) {
                ToastUtil.show("音频文件目录不能为空。")
                return
            }
            try {
                filepath?.let {
                    AudioPlayerUtil.play(it)
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }

        private fun stopAudio() {
            try {
                AudioPlayerUtil.stop()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    /**
     * 最长长度
     * */
    private var MAX_RECORD_TIME = 300
    private var defaultAction = STATUS_READY_START_RECORD
    private var mAudioRecorder: AudioRecorder? = null

    private var activity: Activity? = null
    private var showCloseBtn: Boolean? = false
    private var audioLength: Int = 0
    private var audioFilePath: String? = null
    private var audioCompleteListener: AudioCompleteListener? = null

    constructor(context: Context) : super(context) {
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        initView()
    }

    private fun resetAudioRecord() {
        reset()
        mAudioRecorder = null
        mAudioRecorder = AudioRecorder {
            resetAudioRecord()
        }
    }

    /**
     * 设置录制最大时长 默认为300
     */
    fun setMaxRecordSecond(maxRecord: Int) {
        MAX_RECORD_TIME = maxRecord
        updateRecordView()
    }

    private fun initView() {
        resetAudioRecord()

        AudioPlayerUtil.addListener(this, audioPlayerListener)

        defaultAction = STATUS_READY_START_RECORD
        updateRecordView()

        //重新录制
        iv_rerecord.setOnClickListener(this)
        tv_rerecord_tip.setOnClickListener(this)

        //录制完成
        iv_complete.setOnClickListener(this)
        tv_complete_tip.setOnClickListener(this)

        //录制、播放
        iv_audio_action.setOnClickListener(this)

        //关闭
        iv_audio_close.setOnClickListener(this)
    }


    fun setAudioCompleteListener(activity: Activity, audioCompleteListener: AudioCompleteListener, showCloseBtn: Boolean) {
        this.activity = activity
        this.audioCompleteListener = audioCompleteListener
        this.showCloseBtn = showCloseBtn
        this.iv_audio_close.visibility = if (showCloseBtn) View.VISIBLE else View.GONE
    }

    override fun onClick(v: View?) {
        when (v) {
            //重新录制
            iv_rerecord, tv_rerecord_tip -> {
                delAudioFile()
                reset()
                playOrRecord()
                updateRecordView()
            }

            //完成录制
            iv_complete, tv_complete_tip -> {
                val audio = LocalMedia()
                audio.mimeType = "audio"
                if (audioLength > MAX_RECORD_TIME) {
                    audio.duration = MAX_RECORD_TIME.toLong() * 1000
                } else {
                    audio.duration = audioLength.toLong() * 1000
                }
                audio.path = audioFilePath
                audioCompleteListener?.handleAudioRes(audio)
                reset()
            }

            //录制、播放
            iv_audio_action -> {
                requestPermission()
            }

            //关闭
            iv_audio_close -> {
                delAudioFile()
                audioCompleteListener?.closeAudioPage()
                reset()
            }
        }
    }

    private fun delAudioFile() {
        try {
            val audioFile = File(audioFilePath)
            if (audioFile.exists()) {
                audioFile.delete()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun requestPermission() {
        if (activity.isFinishingOrDestroyed()) {
            return
        }
        context.fragmentActivity()?.let {
            val rxPermissions=RxPermissions(it)
            rxPermissions.request(Manifest.permission.RECORD_AUDIO)
                .subscribe {
                    if(it){
                        playOrRecord()
                        updateRecordView()
                    }else{
                        activity?.finish()
                    }
                }
        }

//        val requestDangerousPermissionUtil = RequestDangerousPermissionUtil(activity!!, Manifest.permission.RECORD_AUDIO)
//        requestDangerousPermissionUtil.requestPermission(this@AudioRecordView.context.getString(R.string.comp_exercise_audio_record_permission),
//                this@AudioRecordView.context.getString(R.string.comp_exercise_clear_app_cache),
//                object : RequestPermissionCallback {
//                    override fun refused() {
//                        if (activity.isFinishingOrDestroyed()) {
//                            return
//                        }
//                        activity?.finish()
//                    }
//
//                    override fun granted() {
//                        playOrRecord()
//                        updateRecordView()
//                    }
//                })
    }

    private fun reset() {
        audioLength = 0
        mAudioRecorder?.stopRecord()
        stopAudio()
        Timer.getInstance().removeCountDownTimerObserver(countDownTimer)
        defaultAction = STATUS_READY_START_RECORD
        updateRecordView()
    }

    private fun playOrRecord() {
        Timer.getInstance().removeCountDownTimerObserver(countDownTimer)

        when (defaultAction) {
            STATUS_READY_START_RECORD -> {
                defaultAction++
                startAudioRecord()
            }
            STATUS_RECORDING -> {
                if (audioLength <= 0) {
                    reset()
                    ToastUtil.show("录制语音时间太短")
                    return
                }
                defaultAction++
                mAudioRecorder?.stopRecord()
            }
            STATUS_RECORDED -> {
                defaultAction++
                playAudio(audioFilePath)
            }

            STATUS_PRE_AUDIO -> {
                defaultAction = STATUS_RECORDED
                stopAudio()
            }

        }
    }

    private val audioPlayerListener = object : AudioPlayerUtil.IAudioPlayerListener {
        override fun onComplete(url: String) {
        }

        override fun onReady(url: String) {
        }

        override fun onStop(url: String) {
            stopAudio()
            defaultAction = STATUS_RECORDED
            updateRecordView()
        }

    }

    private fun startAudioRecord() {
        audioFilePath = buildAudioPath()
        if (TextUtils.isEmpty(audioFilePath)) {
            ToastUtil.show("请插入内存卡")
            defaultAction = STATUS_READY_START_RECORD
            updateRecordView()
            return
        }
        Timer.getInstance().removeCountDownTimerObserver(countDownTimer)

        audioLength = 0
        mAudioRecorder?.startRecord(audioFilePath)
        Timer.getInstance().addCountDownTimerObserver(countDownTimer)
    }

    private var countDownTimer: Timer.CountDownTimerObserver = object : Timer.CountDownTimerObserver {
        @SuppressLint("SetTextI18n")
        override fun onNext() {
            tv_record_time.text = "$audioLength\"/${MAX_RECORD_TIME}\""
            if (audioLength >= MAX_RECORD_TIME) {
                audioLength = MAX_RECORD_TIME
                Timer.getInstance().removeCountDownTimerObserver(this)
                defaultAction = STATUS_RECORDED
                mAudioRecorder?.stopRecord()
                updateRecordView()
                tv_record_time.text = "$MAX_RECORD_TIME\""
            }
            audioLength++
        }
    }

    private fun buildAudioPath(): String? {
        var soundFile: String? = null

        try {
            val audioFileName = MD5Tool.getMD5(System.currentTimeMillis().toString())
            val path = StorageUtil.getExternalFilesDirPath("sndd/audio")
            soundFile = File(path, "$audioFileName.m4a").absolutePath
        } catch (e: Exception) {
            e.printStackTrace()
            ToastUtil.show("语音文件创建失败")
            audioCompleteListener?.closeAudioPage()
        }

        return soundFile
    }

    @SuppressLint("SetTextI18n")
    private fun updateRecordView() {
        when (defaultAction) {
            STATUS_READY_START_RECORD -> {
                audioLength = 0
                tv_record_tip.text = "开始录音"
                tv_record_time.text = "${getAudioLength()}\"/${MAX_RECORD_TIME}\""
                tv_record_time.setTextColor(ContextCompat.getColor(this.context, R.color.color_FF5F2E))
                iv_audio_action.setImageResource(R.drawable.pra_icon_start_big_2)
                group_rerecord.visibility = View.GONE
                group_complete.visibility = View.GONE

//                lav_image_recording_left_anim.visibility = View.GONE
//                lav_image_recording_right_anim.visibility = View.GONE

            }

            STATUS_RECORDING -> {
                tv_record_tip.text = "结束录音"
                tv_record_time.text = "${getAudioLength()}\"/${MAX_RECORD_TIME}\""
                tv_record_time.setTextColor(ContextCompat.getColor(this.context, R.color.color_FF5F2E))
                iv_audio_action.setImageResource(R.drawable.pra_icon_stop_big_2)
                group_rerecord.visibility = View.GONE
                group_complete.visibility = View.GONE

//                lav_image_recording_left_anim.visibility = View.VISIBLE
//                lav_image_recording_right_anim.visibility = View.VISIBLE

//                lav_image_recording_left_anim.playAnimation()
//                lav_image_recording_right_anim.playAnimation()
            }

            STATUS_RECORDED -> {
                tv_record_tip.text = "录音完成，点击播放"
                tv_record_time.text = "${getAudioLength()}\""
                tv_record_time.setTextColor(ContextCompat.getColor(this.context, R.color.color_A1A1B3))
                iv_audio_action.setImageResource(R.drawable.pra_icon_play_big_2)
                group_rerecord.visibility = View.VISIBLE
                group_complete.visibility = View.VISIBLE

//                lav_image_recording_left_anim.visibility = View.GONE
//                lav_image_recording_right_anim.visibility = View.GONE

            }

            STATUS_PRE_AUDIO -> {
                tv_record_tip.text = "结束播放"
                tv_record_time.text = "${getAudioLength()}\""
                tv_record_time.setTextColor(ContextCompat.getColor(this.context, R.color.color_A1A1B3))
                iv_audio_action.setImageResource(R.drawable.pra_icon_pause_big_2)
                group_rerecord.visibility = View.VISIBLE
                group_complete.visibility = View.VISIBLE

//                lav_image_recording_left_anim.visibility = View.GONE
//                lav_image_recording_right_anim.visibility = View.GONE

            }
        }
    }

    private fun getAudioLength(): Int {
        return if (audioLength >= MAX_RECORD_TIME) {
            MAX_RECORD_TIME
        } else {
            audioLength
        }
    }


    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        reset()
    }

    interface AudioCompleteListener {
        fun handleAudioRes(audio: LocalMedia)
        fun closeAudioPage()
    }
}