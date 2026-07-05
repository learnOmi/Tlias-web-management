package org.example.service;

import org.example.pojo.LoginInfo;

/**
 * 认证服务接口
 */
public interface AuthService {

    /**
     * 刷新 Token
     * @param refreshToken 刷新令牌
     * @return 新的双 Token 信息
     */
    LoginInfo refreshToken(String refreshToken);

    /**
     * 登出（吊销当前设备的 refreshToken）
     * @param refreshToken 当前设备的刷新令牌
     */
    void logout(String refreshToken);
}
