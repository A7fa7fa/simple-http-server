package org.a7fa7fa.httpserver.api;

import org.a7fa7fa.httpserver.router.RegisterFunction;
import org.a7fa7fa.httpserver.http.HttpMethod;
import org.a7fa7fa.httpserver.http.HttpRequest;

public class MyStaticFunctions {

    @RegisterFunction(targetMethod = HttpMethod.GET, target = "/api/resource")
    public static void myStaticFunction(HttpRequest httpRequest) {
        System.out.println("myStaticFunction called with HttpRequest: " + httpRequest);
    }

    public static void anotherStaticFunction(HttpRequest httpRequest) {
        System.out.println("anotherStaticFunction called with HttpRequest: " + httpRequest);
    }

    @RegisterFunction(targetMethod = HttpMethod.HEAD, target = "/api/resource")
    public static void yetAnotherStaticFunction(HttpRequest httpRequest) {
        System.out.println("yetAnotherStaticFunction called with HttpRequest: " + httpRequest);
    }

}
