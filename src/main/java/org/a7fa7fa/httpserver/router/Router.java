package org.a7fa7fa.httpserver.router;

import org.a7fa7fa.httpserver.api.MyStaticFunctions;
import org.a7fa7fa.httpserver.http.tokens.HttpMethod;
import org.a7fa7fa.httpserver.http.HttpRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.*;
import java.util.function.Consumer;

public class Router {

    private final static Logger LOGGER = LoggerFactory.getLogger(Router.class);

    private final Map<String, Consumer<HttpRequest>> routes = new HashMap<>();

    private static Router myRouter;
    private Router() {}

    public static Router getInstance() {
        if (myRouter == null) {
            myRouter = new Router();
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

            if (method.getParameterCount() == 1 && method.getParameterTypes()[0] == HttpRequest.class) {
                // Create a Consumer<HttpRequest> function that invokes the method with a String parameter
                Consumer<HttpRequest> function = httpRequest -> {
                    try {
                        // Invoke the static method with the provided message
                        method.invoke(null, httpRequest);
                    } catch (Exception e) {
                        // Handle any exceptions that occur during method invocation
                        LOGGER.error("Some error happened while invoking function.");
                        LOGGER.error(e.toString());
                        throw new RuntimeException(e);
                    }
                };

                // Add the function to the functionMap with the method's name as the key
                routes.put(funcKey, function);
                LOGGER.debug("Registered route : {}", funcKey);
            }
        }

    }

    private String getFunctionNameFromAnnotation(RegisterFunction annotation, Method method) {
        HttpMethod targetMethod = annotation.targetMethod();
        String target = annotation.target();
        if (targetMethod == null || target == null) {
            throw new RuntimeException("Target method or target not specified on : " + method.getName());
        }

        String funcName = this.generateName(targetMethod, target);

        if (routes.containsKey(funcName)) {
            throw new RuntimeException("Target method and target already specified : " + funcName);
        }
        return funcName;
    }

    public ArrayList<String> getRegisteredRoutes() {
        ArrayList<String> names = new ArrayList<>();
        for (Map.Entry<String, Consumer<HttpRequest>> entry : this.routes.entrySet()) {
            names.add(entry.getKey());
        }
        return names;
    }

    private String generateName(HttpMethod type, String target) {
        return type.name() + "~" + target;
    }


    private Consumer<HttpRequest> getFunction(String functionName) {
        return routes.get(functionName);
    }

    public void invoke(HttpRequest httpRequest) {
        String functionName = this.generateName(httpRequest.getMethod(), httpRequest.getRequestTarget());
        Consumer<HttpRequest> function = this.getFunction(functionName);
        LOGGER.debug("Function found : {}", functionName);
        if (function != null) {
            function.accept(httpRequest);
        } else {
            LOGGER.warn("Function not found");
        }
    }

}