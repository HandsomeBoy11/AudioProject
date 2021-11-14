package com.wj.audioproject

import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.wj.audioproject.audio.audioView.AudioRecordView
import com.wj.audioproject.utils.LocalMedia
import kotlinx.android.synthetic.main.activity_chnc_audio_record.*


/**
 *
 *  @author wangjun
 *  @date  2021/9/8 10:34
 *  @Des  :
 *
 */
class AudioRecordActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 设置全透明状态栏
        setStatusBarFullTransparent()
        setContentView(R.layout.activity_chnc_audio_record)
        overridePendingTransition(R.anim.activity_translate_in, R.anim.common_none)
        // 部分手机设置透明主题 无效问题
        theme.applyStyle(R.style.ActivityTransparent, true)
        (audio_record_view as? AudioRecordView)?.setAudioCompleteListener(
            this,
            object : AudioRecordView.AudioCompleteListener {
                override fun handleAudioRes(audio: LocalMedia) {
                }

                override fun closeAudioPage() {
                    finish()
                }
            },
            true
        )
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.common_none, R.anim.activity_translate_out)
    }

    /**
     * 全透状态栏
     */
    private fun setStatusBarFullTransparent() {
        if (Build.VERSION.SDK_INT >= 21) { //21表示5.0
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_LAYOUT_STABLE)
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.statusBarColor = Color.TRANSPARENT
        } else if (Build.VERSION.SDK_INT >= 19) { //19表示4.4
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            //虚拟键盘也透明
            //getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }
    }
}