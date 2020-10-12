package com.infinity.testapp.annotation;

import android.view.View;

import static com.infinity.testapp.annotation.CustomAnnotation.*;


/**
 * @author wang
 * @date 2020/9/11
 * des
 */
class Test {

    @BindView(100)
    View view1;

    @BindView(200)
    View view2;

    @BindView(300)
    View view3;

    @BindView(400)
    View view4;
}
