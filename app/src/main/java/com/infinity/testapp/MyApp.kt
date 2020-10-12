package com.infinity.testapp

import android.app.Application
import com.alibaba.android.arouter.launcher.ARouter

/**
 * @author wang
 * @date   2020/10/12
 * des
 */
class MyApp : Application() {
    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) {
            ARouter.openDebug()
            ARouter.openLog()
        }
        ARouter.init(this)
    }
}