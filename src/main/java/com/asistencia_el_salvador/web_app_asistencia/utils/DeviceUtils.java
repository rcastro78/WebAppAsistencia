package com.asistencia_el_salvador.web_app_asistencia.utils;

import jakarta.servlet.http.HttpServletRequest;

public class DeviceUtils {

    public static String getIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        return ip != null ? ip.split(",")[0] : request.getRemoteAddr();
    }

    public static String getUserAgent(HttpServletRequest request) {
        return request.getHeader("User-Agent");
    }

    public static String getOS(String userAgent) {
        if (userAgent.contains("Windows")) return "Windows";
        if (userAgent.contains("iPhone")) return "iOS";
        if (userAgent.contains("Mac")) return "macOS";
        if (userAgent.contains("Android")) return "Android";
        return "Otro";
    }

    public static String getDevice(String userAgent) {
        if (userAgent.contains("Windows")) return "PC";
        if (userAgent.contains("iPhone")) return "iPhone";
        if (userAgent.contains("Mac")) return "Macintosh";
        if (userAgent.contains("Android")) return "Android Phone";
        return "Otro";
    }


}
