package com.sky.annotation;

import com.sky.enumeration.OperationType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 自定义注解，用于自动填充公共字段
 */
@Target({ElementType.METHOD})//在方法上生效
@Retention(RetentionPolicy.RUNTIME)
public @interface AutoFill {
    OperationType value();
}
