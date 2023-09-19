package org.a7fa7fa.httpserver.router;

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
}