package org.example.utils;

import java.util.List;

/**
 * 当前用户权限信息持有类
 * 使用 ThreadLocal 存储当前请求的权限列表，避免在各层之间传递参数
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

    /**
     * 移除当前线程的权限信息，防止 ThreadLocal 内存泄漏
     */
    public static void remove() {
        PERMISSIONS_LOCAL.remove();
    }
}
