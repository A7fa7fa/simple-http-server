package org.a7fa7fa.httpserver.controller;

import org.a7fa7fa.httpserver.http.HttpRequest;

public enum ControllerType {
    STATIC("static"),
    API("api");

    private final String name;

    ControllerType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static ControllerType getControllerTypeOfEndpoint(HttpRequest httpRequest, String apiPath) {
        if (httpRequest.getRequestTarget().startsWith(apiPath)){
            return ControllerType.API;
        }
        return ControllerType.STATIC;
    }
}
