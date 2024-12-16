package com.example.deploy.global.validation.type;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface AllOrNones {
    AllOrNone[] value(); // 여러 개의 AllOrNone 어노테이션을 담기 위한 배열
}
