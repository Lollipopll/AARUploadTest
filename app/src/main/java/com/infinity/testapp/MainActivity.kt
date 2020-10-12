package com.infinity.testapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import com.infinity.easyeventbus.EasyEventBus
import com.infinity.easyeventbus.Subscribe
import com.infinity.testapp.recycler.IntentUtils
import kotlinx.android.synthetic.main.activity_easy_event_bus.*

class MainActivity : AppCompatActivity() {
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_main)
//
//        findViewById<Button>(R.id.btnOpen).setOnClickListener {
//            IntentUtils.openToUri(this,"http://www.shapekeeper.net/privacy_policy.html")
//        }
//    }



    private val eventTest = EasyBusEventTest()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_easy_event_bus)
        EasyEventBus.register(this)
        eventTest.register()

        btn_postString.setOnClickListener {
            EasyEventBus.post("Hello")
        }
        btn_postBean.setOnClickListener {
            EasyEventBus.post(HelloBean("hi"))
        }
    }

    @Subscribe
    fun stringFun(msg: String) {
//        showToast("$msg EasyEventBusActivity")

        Log.e("EasyEventBus","$msg EasyEventBusActivity")
    }

    @Subscribe
    fun benFun(msg: HelloBean) {
//        showToast("${msg.data} EasyEventBusActivity")
        Log.e("EasyEventBus","$msg EasyEventBusActivity")

    }

    override fun onDestroy() {
        super.onDestroy()
        EasyEventBus.unregister(this)
        eventTest.unregister()
    }

}

class EasyBusEventTest {

    @Subscribe
    fun stringFun(msg: String) {
//        showToast("$msg EasyBusEventTest")
    }

    @Subscribe
    fun benFun(msg: HelloBean) {
//        showToast("${msg.data} EasyBusEventTest")
    }

    fun register() {
        EasyEventBus.register(this)
    }

    fun unregister() {
        EasyEventBus.unregister(this)
    }

}


data class HelloBean(val data: String)
