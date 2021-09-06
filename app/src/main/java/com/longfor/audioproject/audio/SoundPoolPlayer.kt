package com.longfor.audioproject.audio

import android.media.AudioAttributes
import android.media.AudioManager
import android.media.SoundPool
import android.os.Build
import android.util.SparseIntArray
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import com.longfor.audioproject.MyApplication
import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import java.util.concurrent.TimeUnit

/**
 * SoundPool播放器
 * 传入有效的 @param {lifecycle}，SoundPoolPlayer会在lifecycle销毁时自动释放，否则需要手动释放
 * *** 注意事项 ***
 * 必须先调用preloadRes(),再调用playSound()
 * Created by wj on 2021/9/6 2:15 PM
 */
@Suppress("DEPRECATION")
class SoundPoolPlayer(lifecycle: Lifecycle? = null) : LifecycleObserver {

    private var soundPool: SoundPool
    private var soundMap: SparseIntArray = SparseIntArray()
    private var mCurrentId: Int = -1
    private val sampleIdSet = mutableSetOf<Int>()
    private var mDisposable: Disposable? = null

    init {
        lifecycle?.addObserver(this)
        soundPool = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            SoundPool.Builder().setMaxStreams(10)
                    .setAudioAttributes(AudioAttributes.Builder().setLegacyStreamType(AudioManager.STREAM_MUSIC).build())
                    .build()
        } else {
            SoundPool(10, AudioManager.STREAM_MUSIC, 0)
        }
        soundPool.setOnLoadCompleteListener { _, sampleId, status ->
            if (status == 0) {
                //the status of the load operation (0 = success)
                sampleIdSet.add(sampleId)
            } else {
                Logger.e("raw load error,sampleId is $sampleId")
            }
        }
    }

    /**
     * 预加载
     *
     * 在播放音频前执行效果最佳
     *
     * 推荐在页面初始化完成后调用
     * 可以有效防止sound播放延迟 or 无法播放
     */
    fun preloadRes(vararg rawResId: Int) {
        rawResId.forEach { resId ->
            soundMap.put(resId, soundPool.load(MyApplication.instance, resId, 1))
        }
    }

    /**
     * 播放
     *
     * 调用此方法前必须先调用preloadRes
     *
     *  playSound(int soundID, float leftVolume, float rightVolume, int priority, int loop, float rate) ：
     *  1)该方法的第一个参数指定播放哪个声音；
     *  2) leftVolume 、
     *  3) rightVolume 指定左、右的音量：
     *  4) priority 指定播放声音的优先级，数值越大，优先级越高；
     *  5) loop 指定是否循环， 0 为不循环， -1 为循环；
     *  6) rate 指定播放的比率，数值可从 0.5 到 2 ， 1 为正常比率。
     */
    @JvmOverloads
    fun play(resId: Int, loop: Boolean = false, rate: Float = 1f) {
//        if (PlayerManager.getInstance().isPlaying) {
//            return
//        }
        if (soundMap.indexOfKey(resId) < 0) {
            //判断到未加载，去执行一次预加载
            preloadRes(resId)
        }
        val soundId = soundMap.get(resId)
        mDisposable = Flowable.intervalRange(0, 10, 0, 100, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    //初始化完成
                    if (sampleIdSet.contains(soundId)) {
                        //关闭上一条SoundPool
                        if (mCurrentId != -1) {
                            soundPool.stop(mCurrentId)
                        }
                        mCurrentId = soundPool.play(
                                soundId,
                                1f,
                                1f,
                                1,
                                if (loop) -1 else 0,
                                rate)
                        mDisposable?.dispose()
                        mDisposable = null
                    }
                }
    }

    /**
     * 停止当前播放的音频
     */
    fun stop() {
        if (mCurrentId != -1) {
            soundPool.stop(mCurrentId)
        }
    }

    /**
     * 释放资源
     * 未传入lifecycle的场景需要手动调用此方法
     */
    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun release() {
        soundPool.autoPause()
        soundPool.release()
        mDisposable?.let {
            if (!it.isDisposed) {
                it.dispose()
                mDisposable = null
            }
        }
    }
}