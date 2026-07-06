package org.example.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.example.mapper.RefreshTokenMapper;
import org.example.pojo.LoginInfo;
import org.example.pojo.RefreshToken;
import org.example.service.AuthService;
import org.example.service.EmpService;
import org.example.service.PermissionService;
import org.example.service.RoleService;
import org.example.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 认证服务实现类
 * 负责 Token 刷新逻辑
 */
@Slf4j
@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    private RefreshTokenMapper refreshTokenMapper;

    @Autowired
    private EmpService empService;

    @Autowired
    private RoleService roleService;

    @Autowired
    private PermissionService permissionService;

    // accessToken 有效期：2 小时（秒）
    private static final long ACCESS_TOKEN_EXPIRES_IN = 2 * 60 * 60;

    // refreshToken 有效期：7 天（毫秒）
    private static final long REFRESH_TOKEN_EXPIRATION = 7L * 24 * 60 * 60 * 1000;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public LoginInfo refreshToken(String refreshToken) {
        // 1. 查询数据库验证 refreshToken
        RefreshToken tokenRecord = refreshTokenMapper.selectByToken(refreshToken);
        if (tokenRecord == null) {
            log.warn("refreshToken 无效");
            return null;
        }

        // 2. 检查是否已吊销
        if (tokenRecord.getRevoked() != null && tokenRecord.getRevoked() == 1) {
            log.warn("refreshToken 已吊销");
            return null;
        }

        // 3. 检查是否过期
        if (tokenRecord.getExpireTime() == null || tokenRecord.getExpireTime().isBefore(LocalDateTime.now())) {
            log.warn("refreshToken 已过期");
            return null;
        }

        Integer empId = tokenRecord.getEmpId();

        // 4. 原子性吊销旧 refreshToken（WHERE revoked = 0 确保只吊销一次）
        int affectedRows = refreshTokenMapper.revokeByToken(refreshToken);
        if (affectedRows == 0) {
            // 并发场景：另一个线程已经吊销了这个 token
            log.warn("refreshToken 已被并发吊销，empId: {}", empId);
            return null;
        }

        // 5. 查询用户信息
        LoginInfo.UserInfo userInfo = empService.getUserInfoById(empId);
        if (userInfo == null) {
            log.warn("用户不存在: {}", empId);
            return null;
        }

        // 6. 生成新的 accessToken
        Map<String, Object> claims = new HashMap<>();
        claims.put("id", empId);
        claims.put("username", userInfo.getUsername());
        String newAccessToken = JwtUtils.generateAccessToken(claims);

        // 7. 生成新的 refreshToken
        Map<String, Object> refreshClaims = new HashMap<>();
        refreshClaims.put("id", empId);
        String newRefreshToken = JwtUtils.generateRefreshToken(refreshClaims);

        // 8. 保存新 refreshToken 到数据库
        RefreshToken newTokenRecord = new RefreshToken();
        newTokenRecord.setEmpId(empId);
        newTokenRecord.setToken(newRefreshToken);
        newTokenRecord.setExpireTime(LocalDateTime.now().plusSeconds(REFRESH_TOKEN_EXPIRATION / 1000));
        newTokenRecord.setCreateTime(LocalDateTime.now());
        refreshTokenMapper.insert(newTokenRecord);

        // 9. 查询角色和权限
        List<String> roleCodes = roleService.selectRolesByEmpId(empId).stream()
                .map(role -> role.getCode())
                .collect(Collectors.toList());

        List<String> permissions = permissionService.selectPermissionsByEmpId(empId);

        // 如果是管理员角色，授予全部权限
        if (roleCodes.contains("admin")) {
            permissions = new ArrayList<>();
            permissions.add("*");
        }

        // 10. 组装返回结果
        LoginInfo loginInfo = new LoginInfo();
        loginInfo.setId(empId);
        loginInfo.setUsername(userInfo.getUsername());
        loginInfo.setName(userInfo.getName());
        loginInfo.setAccessToken(newAccessToken);
        loginInfo.setRefreshToken(newRefreshToken);
        loginInfo.setExpiresIn(ACCESS_TOKEN_EXPIRES_IN);
        loginInfo.setRoles(roleCodes);
        loginInfo.setPermissions(permissions);
        loginInfo.setUserInfo(userInfo);

        log.info("Token 刷新成功, empId: {}", empId);
        return loginInfo;
    }

    @Override
    public void logout(String refreshToken) {
        if (refreshToken == null || refreshToken.isEmpty()) {
            log.warn("登出失败：refreshToken 为空");
            return;
        }

        // 原子性吊销当前设备的 refreshToken
        int affectedRows = refreshTokenMapper.revokeByToken(refreshToken);
        if (affectedRows > 0) {
            log.info("登出成功，refreshToken 已吊销");
        } else {
            log.warn("登出失败：refreshToken 已不存在或已吊销");
        }
    }
}
