package com.infinity.testapp

import com.infinity.testapp.annotation.CustomAnnotation
import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }

    @Test
    fun testA() {

        var clzz = Class.forName("com.infinity.testapp.annotation.Test")

        val declaredFields = clzz.declaredFields

        for (declaredField in declaredFields) {
            val isExit = declaredField.isAnnotationPresent(CustomAnnotation.BindView::class.java)
            if (isExit) {
                val bind = declaredField.getAnnotation(CustomAnnotation.BindView::class.java)
                val id = bind.value
                print("viewId->$id")
            }
        }

    }
}