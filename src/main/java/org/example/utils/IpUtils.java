package org.example.utils;

import jakarta.servlet.http.HttpServletRequest;

/**
 * IP 工具类
 */
public class IpUtils {

    private IpUtils() {
        // 工具类禁止实例化
    }

    /**
     * 获取客户端真实IP地址
     * 支持多级代理场景（X-Forwarded-For / Proxy-Client-IP / WL-Proxy-Client-IP）
     *
     * @param request HTTP 请求
     * @return 客户端 IP 地址
     */
    public static String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        // 多级代理情况下，取第一个IP
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        return ip;
    }
}
