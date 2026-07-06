package org.example.service.impl;

import org.example.mapper.PermissionMapper;
import org.example.service.PermissionService;
import org.example.utils.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 权限服务实现类
 * 使用 Redis 缓存权限数据，减少数据库查询
 */
@Service
public class PermissionServiceImpl implements PermissionService {

    private static final String PERMS_CACHE_KEY_PREFIX = "perms:";

    @Autowired
    private PermissionMapper permissionMapper;

    @Autowired
    private RedisUtil redisUtil;

    @Override
    public List<String> selectPermissionsByEmpId(Integer empId) {
        String cacheKey = PERMS_CACHE_KEY_PREFIX + empId;

        // 1. 尝试从 Redis 读取缓存
        Object cached = redisUtil.get(cacheKey);
        if (cached instanceof List<?> list && !list.isEmpty()) {
            @SuppressWarnings("unchecked")
            List<String> result = (List<String>) cached;
            return result;
        }

        // 2. 缓存未命中，查询数据库
        List<String> permissions = permissionMapper.selectCodesByEmpId(empId);

        // 3. 写入缓存（TTL 30 分钟）
        redisUtil.set(cacheKey, permissions);

        return permissions;
    }

    /**
     * 清除指定员工的权限缓存
     */
    public void clearCache(Integer empId) {
        redisUtil.delete(PERMS_CACHE_KEY_PREFIX + empId);
    }
}
