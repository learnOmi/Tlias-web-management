package org.example.service.impl;

import org.example.mapper.EmpRoleMapper;
import org.example.pojo.Role;
import org.example.service.RoleService;
import org.example.utils.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 角色服务实现类
 * 使用 Redis 缓存角色数据，减少数据库查询
 */
@Service
public class RoleServiceImpl implements RoleService {

    private static final String ROLES_CACHE_KEY_PREFIX = "roles:";

    @Autowired
    private EmpRoleMapper empRoleMapper;

    @Autowired
    private RedisUtil redisUtil;

    @Override
    public List<Role> selectRolesByEmpId(Integer empId) {
        String cacheKey = ROLES_CACHE_KEY_PREFIX + empId;

        // 1. 尝试从 Redis 读取缓存
        Object cached = redisUtil.get(cacheKey);
        if (cached instanceof List) {
            @SuppressWarnings("unchecked")
            List<Role> result = (List<Role>) cached;
            return result;
        }

        // 2. 缓存未命中，查询数据库
        List<Role> roles = empRoleMapper.selectRolesByEmpId(empId);

        // 3. 写入缓存（TTL 30 分钟）
        redisUtil.set(cacheKey, roles);

        return roles;
    }

    /**
     * 清除指定员工的角色缓存
     */
    public void clearCache(Integer empId) {
        redisUtil.delete(ROLES_CACHE_KEY_PREFIX + empId);
    }
}
