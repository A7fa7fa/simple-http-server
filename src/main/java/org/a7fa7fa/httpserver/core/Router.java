package org.a7fa7fa.httpserver.core;

import org.a7fa7fa.httpserver.config.Configuration;
import org.a7fa7fa.httpserver.controller.Controller;
import org.a7fa7fa.httpserver.controller.ControllerType;
import org.a7fa7fa.httpserver.controller.RegisterFunction;
import org.a7fa7fa.httpserver.http.Context;
import org.a7fa7fa.httpserver.http.HttpParsingException;
import org.a7fa7fa.httpserver.http.HttpResponse;
import org.a7fa7fa.httpserver.http.tokens.HttpMethod;
import org.a7fa7fa.httpserver.http.HttpRequest;
import org.a7fa7fa.httpserver.http.tokens.HttpStatusCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.function.Consumer;

public class Router {

    private final static Logger LOGGER = LoggerFactory.getLogger(Router.class);

    private final Map<String, Consumer<Context>> routes = new HashMap<>();

    private static Router myRouter;
    private final Configuration configuration;

    private Router(Configuration configuration) {
        this.configuration = configuration;
    }

    public static Router getInstance(Configuration configuration) {
        if (myRouter == null) {
            myRouter = new Router(configuration);
        }
        return myRouter;
    }

    private boolean implementsControllerInterface(Class<?> clazz) {
        Class<?>[] interfaces = clazz.getInterfaces();
        if (interfaces.length > 0) {
            Set<Class<?>> res = new HashSet<>(Arrays.asList(interfaces));
            return res.contains(Controller.class);
        }
        return false;
    }

    private Consumer<Context> createLambdaFunction(Method method) {
        return context -> {
            try {
                method.invoke(null, context);
            } catch (IllegalAccessException | InvocationTargetException e) {
                LOGGER.debug("Some error happened while invoking function.");
                throw new RuntimeException(e.getCause());
            }
        };
    }

    public <ControllerInterface extends Controller> void register(Class<ControllerInterface> clazz) throws Exception {

        if (!this.implementsControllerInterface(clazz)){
            throw new Exception("Class does not implement controller interface");
        }

        // Get all the methods declared in the provided class
        Method[] methods = clazz.getDeclaredMethods();

        // Iterate through each method
        for (Method method : methods) {
            // Check if the method has annotations
            RegisterFunction annotation = method.getAnnotation(RegisterFunction.class);
            if (annotation == null) {
                continue;
            }
            String funcKey = this.getFunctionNameFromAnnotation(annotation, method);
            if (method.getParameterCount() == 1 && method.getParameterTypes()[0] == Context.class) {
                // Create a Consumer<HttpRequest> function that invokes the method with a String parameter
                Consumer<Context> function = this.createLambdaFunction(method);
                // Add the function to the functionMap with the method's name as the key
                routes.put(funcKey, function);
                LOGGER.info("Registered route : {}", funcKey);
            } else {
                LOGGER.warn("Function could not be registered : {}", method.getName());
                LOGGER.warn("No Context object as parameter specified.");
            }
        }

    }

    private String getFunctionNameFromAnnotation(RegisterFunction annotation, Method method) {
        HttpMethod targetMethod = annotation.targetMethod();
        String target = annotation.target();
        ControllerType controllerType = annotation.controllerType();
        if (targetMethod == null || target == null || controllerType == null) {
            throw new RuntimeException("Target method or target not specified on : " + method.getName());
        }

        String funcName = this.generateName(targetMethod, target, controllerType);

        if (controllerType == ControllerType.API){
            funcName = this.generateName(targetMethod, this.configuration.getApiPath() + target, controllerType);
        }

        if (routes.containsKey(funcName)) {
            throw new RuntimeException("Target method and target already specified : " + funcName);
        }
        return funcName;
    }

    public ArrayList<String> getRegisteredRoutes() {
        ArrayList<String> names = new ArrayList<>();
        for (Map.Entry<String, Consumer<Context>> entry : this.routes.entrySet()) {
            names.add(entry.getKey());
        }
        return names;
    }

    private String generateName(HttpMethod type, String target, ControllerType controllerType) {
        String name = type.name() + "~" + ControllerType.STATIC.toString() + "~" + "/";
        if (controllerType == ControllerType.API) {
            name = type.name() + "~" + ControllerType.API.toString() + "~" + target;
        }
        return name;
    }


    private Consumer<Context> getFunction(String functionName) {
        return routes.get(functionName);
    }

    public void invoke(Context context) throws HttpParsingException {
        ControllerType endpoint = ControllerType.getControllerTypeOfEndpoint(context.getHttpRequest(), this.configuration.getApiPath());

        String functionName = this.generateName(context.getHttpRequest().getMethod(), context.getHttpRequest().getRequestTarget(), endpoint);
        LOGGER.debug("Requesting function... : {}", functionName);
        Consumer<Context> function = this.getFunction(functionName);
        if (function != null) {
            function.accept(context);
        } else {
            LOGGER.warn("Function not found");
            throw new HttpParsingException(HttpStatusCode.CLIENT_ERROR_404_NOT_FOUND);
        }
    }

}