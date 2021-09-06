package com.longfor.audioproject.audio.bgm

import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import com.longfor.audioproject.audio.Logger

/**
 * 背景音乐BGM播放.
 * 一级页面开启的是Bgm，进入二级页面Bgm不停即为BgBgm
 *
 * @author liuweiping
 * @since 2020-06-24
 */
object BgmHelper : LifecycleObserver {

    private const val IGC_READ_PLAN_BGM = "igc_read_plan_bgm"
    private const val KEY_BGM_SWITCH = "KEY_BGM_SWITCH"
    private const val KEY_LIFECYCLE = "lifecycle"

    private val sp: SPUtils by lazy { SPUtils.getInstance(IGC_READ_PLAN_BGM) }
    private val cache by lazy { mutableMapOf<Int, BgmObserver>() }

    /**
     * @param lifecycle Lifecycle
     * @param path 本地文件地址，优先级高
     * @param bgmId 资源文件，优先级低
     * @param reset true - 在同一个Lifecycle页面多次调用 play 方法时，重新开发播放，并支持更换背景音乐资源
     * @param interruptAudio true - 阻断音频播放器，播放bgm音频  false - 不阻断音频播放器，继续播放，不播放bgm音频
     */
    fun play(
        lifecycle: Lifecycle,
        path: String?,
        bgmId: Int?,
        reset: Boolean = false,
        interruptAudio: Boolean = true
    ): BgmObserver? {
        if (!sp.getBoolean(KEY_BGM_SWITCH, true)) {
            return null
        }
//        if (PlayerManager.getInstance().isPlaying) {
//            if (interruptAudio) {
//                PlayerManager.getInstance().pause()
//            } else {
//                return null
//            }
//        }
        return getBgmObserver(lifecycle).apply {
            this.interruptAudio = interruptAudio
            this.playPathOrId(path, bgmId, reset)
        }
    }

    fun stop(lifecycle: Lifecycle?) {
        lifecycle?.let { getBgmObserver(it).stop() }
    }

    private fun getBgmObserver(lifecycle: Lifecycle): BgmObserver {
        val cacheKey = lifecycle.hashCode()
        return getBgmObserver(cacheKey).apply {
            lifecycle.addObserver(this)
        }
    }

    /**
     * @param cacheKey BgmObserver 缓存的 Key
     */
    private fun getBgmObserver(cacheKey: Int): BgmObserver {
        return cache[cacheKey] ?: BgmObserver {
            cache.remove(cacheKey)
            Logger.i("移除 #%s 的Lifecycle缓存\ncache: %s  ${Integer.toHexString(cacheKey)}   $cache")
        }.apply {
            cache[cacheKey] = this
        }
    }

    /**
     * 当进入此页面时，仍然播放前面的BGM，并需要同时按照当前页面的生命周期控制BGM时，需要调用此方法
     */
    fun registerBgBgmObserver(intent: Intent?, lifecycle: Lifecycle): BgBgmObserver? {
        val cacheKey = lifecycle.hashCode()
        return cache[intent?.getIntExtra(KEY_LIFECYCLE, -1) ?: -1]?.let {
            BgBgmObserver(it) {
                cache.remove(cacheKey)
                Logger.i(
                    "移除 #%s 的Lifecycle缓存\ncache: %s  ${Integer.toHexString(cacheKey)}   $cache")
            }.apply {
                lifecycle.addObserver(this)
                cache[cacheKey] = this
            }
        }
    }

    /**
     * 当进入下一个Activity时不暂停BGM，需要调用此方法生成Bundle，传入Intent
     *
     * 生成 Bundle 传入下个Activity 的 Intent，用于生成下个Activity 的 BgmObserverWrapper
     *
     *
     * @param bundle 为空时新建Bundle对象并返回；也可传入已有bundle合并字段
     */
    fun getBgBgmObserverBundle(lifecycle: Lifecycle, bundle: Bundle? = null): Bundle {
        return (bundle ?: Bundle()).apply {
            val cacheKey = lifecycle.hashCode()
            putInt(KEY_LIFECYCLE, cacheKey)
        }
    }

}