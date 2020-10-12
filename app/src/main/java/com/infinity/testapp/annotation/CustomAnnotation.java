package com.infinity.testapp.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author wang
 * @date 2020/9/11
 * des
 */
class CustomAnnotation {

    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.FIELD})
    @Documented
    public @interface BindView {
        int value();
    }
}
