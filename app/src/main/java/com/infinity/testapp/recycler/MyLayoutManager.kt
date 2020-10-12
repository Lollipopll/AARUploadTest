package com.infinity.testapp.recycler

import androidx.recyclerview.widget.RecyclerView

/**
 * @author wang
 * @date   2020/9/21
 * des
 */
class MyLayoutManager : RecyclerView.LayoutManager() {
    override fun generateDefaultLayoutParams(): RecyclerView.LayoutParams {
        return RecyclerView.LayoutParams(
            RecyclerView.LayoutParams.WRAP_CONTENT,
            RecyclerView.LayoutParams.WRAP_CONTENT
        )
    }


//    fun getDDD(){
//        getChildMeasureSpec()
//    }


}