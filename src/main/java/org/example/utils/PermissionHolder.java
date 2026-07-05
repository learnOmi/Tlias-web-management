package org.example.utils;

import java.util.List;

/**
 * 当前用户信息持有类
 * 用于在请求上下文中存储当前登录用户的权限信息
 */
public class PermissionHolder {

    private static final ThreadLocal<List<String>> PERMISSIONS_LOCAL = new ThreadLocal<>();

    public static void setPermissions(List<String> permissions) {
        PERMISSIONS_LOCAL.set(permissions);
    }

    public static List<String> getPermissions() {
        return PERMISSIONS_LOCAL.get();
    }

    public static boolean hasPermission(String permissionCode) {
        List<String> permissions = PERMISSIONS_LOCAL.get();
        // 如果没有权限信息，返回false
        if (permissions == null || permissions.isEmpty()) {
            return false;
        }
        // admin角色拥有全部权限
        if (permissions.contains("*")) {
            return true;
        }
        // 检查是否包含指定权限
        return permissions.contains(permissionCode);
    }

    public static void remove() {
        PERMISSIONS_LOCAL.remove();
    }
}
