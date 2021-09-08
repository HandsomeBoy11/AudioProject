package com.longfor.audioproject.utils

import android.view.Gravity
import android.widget.Toast
import com.longfor.audioproject.MyApplication

/**
 *
 *  @author wangjun
 *  @date  2021/9/7 17:02
 *  @Des  :
 *
 */
object ToastUtil {
    fun show(msg:String){
        Toast.makeText(MyApplication.instance,msg,Toast.LENGTH_SHORT).apply {
            setGravity(Gravity.CENTER,0,0)
            show()
        }
    }
}