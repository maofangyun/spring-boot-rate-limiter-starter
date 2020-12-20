package com.mfy.limiter.annotation;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Limiter {

    String limiterName() default "";

    String luaName() default "";

    String[] urlPatterns() default {};

}
