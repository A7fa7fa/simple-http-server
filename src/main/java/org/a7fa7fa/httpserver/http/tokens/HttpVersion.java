package org.a7fa7fa.httpserver.http.tokens;

import org.a7fa7fa.httpserver.http.BadHttpVersionException;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public enum HttpVersion {

    HTTP_1_0("HTTP/1.0", 1, 0),
    HTTP_1_1("HTTP/1.1", 1, 1);

    public final String LITERAL;
    public final int MAJOR;
    public final int MINOR;

    HttpVersion(String LITERAL, int MAJOR, int MINOR) {
        this.LITERAL = LITERAL;
        this.MAJOR = MAJOR;
        this.MINOR = MINOR;
    }

    private static final Pattern httpVersonRegexPattern = Pattern.compile("^HTTP/(?<major>\\d+).(?<minor>\\d+)");

    public static HttpVersion getBestCompatibleVersion(String literalVersion) throws BadHttpVersionException {
        Matcher matcher = httpVersonRegexPattern.matcher(literalVersion);

        if (!matcher.find() || matcher.groupCount() != 2) {
            throw new BadHttpVersionException();
        }

        int major = Integer.parseInt(matcher.group("major"));
        int minor = Integer.parseInt(matcher.group("minor"));

        HttpVersion tempBestCompatibel = null;
        for (HttpVersion version: HttpVersion.values()) {
            if (version.LITERAL.equals(literalVersion)) {
                return version;
            }
            if (version.MAJOR == major) {
                if (version.MINOR < minor) {
                    tempBestCompatibel = version;
                }
            }

        }
        return tempBestCompatibel;
    }
}
