package org.a7fa7fa.httpserver.controller;

import org.a7fa7fa.httpserver.controller.ControllerType;
import org.a7fa7fa.httpserver.http.tokens.HttpMethod;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
public @interface RegisterFunction {

    HttpMethod targetMethod();
    String target();
    ControllerType controllerType() default ControllerType.API;

}