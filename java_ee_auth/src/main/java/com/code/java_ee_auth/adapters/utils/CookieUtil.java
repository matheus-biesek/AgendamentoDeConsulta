package com.code.java_ee_auth.adapters.utils;

public class CookieUtil {

    public static String createCookie(String name, String value, String path, int maxAgeSeconds, boolean httpOnly, boolean includeSameSiteStrict) {
        StringBuilder cookie = new StringBuilder();
        cookie.append(name).append("=").append(value)
              .append("; Path=").append(path)
              .append("; Max-Age=").append(maxAgeSeconds);

        if (httpOnly) {
            cookie.append("; HttpOnly");
        }

        if (includeSameSiteStrict) {
            cookie.append("; SameSite=Strict");
        }

        return cookie.toString();
    }
}

