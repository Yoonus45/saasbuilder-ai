package com.yoonus.backend.config;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Rate limit annotation for endpoints
 * Usage: @RateLimit(maxRequests = 10, windowSeconds = 60)
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RateLimit {
    int maxRequests() default 10;
    int windowSeconds() default 60;
}
