package com.wj.audioproject.audio.seekBar

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.appcompat.widget.AppCompatSeekBar


/**
 *
 * @Description:    可控制是否可拖动的seekBar
 * @Author:         wangjun
 * @CreateDate:     2021/3/11
 *
 */
class AudioSeekBar @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet?,
        defStyleAttr: Int = 0
) : AppCompatSeekBar(context, attrs, defStyleAttr) {
    /**
     * 是否支持拖动进度
     */
    private var touch = false
    fun setTouch(touch: Boolean) {
        this.touch = touch
    }

    /**
     * onTouchEvent 是在 SeekBar 继承的抽象类 AbsSeekBar
     */
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        return if (touch) {
            super.onTouchEvent(event)
        } else false
    }
}