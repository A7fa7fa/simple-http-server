package org.a7fa7fa.httpserver.config;

import ch.qos.logback.classic.Level;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;

public class LogLevel {

    static String[] levels = new String[5];
    static {
        levels[0] = "trace";
        levels[1] = "debug";
        levels[2] = "info";
        levels[3] = "warn";
        levels[4] = "error";
    }

    public static Level parseLogLevel(String levelLiteral) throws HttpConfigurationException {
        return switch (levelLiteral) {
            case "trace" -> Level.TRACE;
            case "debug" -> Level.DEBUG;
            case "info" -> Level.INFO;
            case "warn" -> Level.WARN;
            case "error" -> Level.ERROR;
            default -> throw new HttpConfigurationException(levelLiteral + " not a valid level. Possible values : " + Arrays.toString(levels));
        };
    }

}
