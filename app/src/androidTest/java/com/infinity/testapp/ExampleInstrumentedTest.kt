package com.infinity.testapp

import android.util.Log
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.infinity.testapp.annotation.CustomAnnotation
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {
    @Test
    fun useAppContext() {
        // Context of the app under test.
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        assertEquals("com.infinity.testapp", appContext.packageName)
    }

    @Test
    fun testA() {

        var clzz =Class.forName("com.infinity.testapp.annotation.Test")

        val declaredFields=clzz.declaredFields

        for (declaredField in declaredFields) {
            val isExit=declaredField.isAnnotationPresent(CustomAnnotation.BindView::class.java)
            if(isExit){
                val bind=declaredField.getAnnotation(CustomAnnotation.BindView::class.java)
                val id=bind.value
                Log.e("TestA", "viewId->$id")
            }
        }

    }
}