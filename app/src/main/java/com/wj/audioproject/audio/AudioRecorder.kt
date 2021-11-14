package com.wj.audioproject.audio

import android.media.MediaRecorder
import com.wj.audioproject.utils.ToastUtil


/**
 * AudioRecord
 * @author  created by liuxiaoliang@luojilab.com
 * @date 2019-10-30
 */
class AudioRecorder(private val audioRecordErrorListener: () -> Unit) {
    private var mediaRecorder: MediaRecorder? = null

    private var isRecording: Boolean = false

    private fun prepare() {
        if (mediaRecorder == null) {
            mediaRecorder = MediaRecorder()
        }
        //设置音频的来源
        mediaRecorder?.setAudioSource(MediaRecorder.AudioSource.DEFAULT)
        //设置音频的输出格式
        mediaRecorder?.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)//设置输出文件的格式
        //设置音频文件的编码
        mediaRecorder?.setAudioEncoder(MediaRecorder.AudioEncoder.AAC)//设置音频文件的编码

        mediaRecorder?.setOnErrorListener { _, _, _ ->
            try {
                mediaRecorder?.stop()
                mediaRecorder?.release()
                mediaRecorder = null
                isRecording = false
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                audioRecordErrorListener()
            }
        }
    }


    fun startRecord(filepath: String?) {
        if (filepath.isNullOrEmpty()) {
            ToastUtil.show("存储目录不能为空。")
            return
        }
        //如果正在录制，就返回了
        if (isRecording) {
            return
        }
        try {
            prepare()
            // 设置文件路径
            mediaRecorder?.setOutputFile(filepath)
            // 录制前准备工作
            mediaRecorder?.prepare()
            // 开始录制
            mediaRecorder?.start()

            isRecording = true
        } catch (e: Exception) {
            e.printStackTrace()
            audioRecordErrorListener()
        }
    }

    fun stopRecord() {
        try {
            if (isRecording) {
                // 停止录制
                mediaRecorder?.stop()
                // 重新开始
                mediaRecorder?.reset() //注意：可以通过返回setAudioSource（）步骤来重用该对象
                isRecording = false
            }
        } catch (e: Exception) {
            isRecording = false
            mediaRecorder = null
            e.printStackTrace()
        }

    }

}