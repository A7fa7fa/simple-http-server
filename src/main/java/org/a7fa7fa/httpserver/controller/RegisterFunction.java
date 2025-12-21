package org.a7fa7fa.httpserver.controller;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.a7fa7fa.httpserver.http.tokens.HttpMethod;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
public @interface RegisterFunction {

    HttpMethod targetMethod();
    String target();
    ControllerType controllerType() default ControllerType.API;

}