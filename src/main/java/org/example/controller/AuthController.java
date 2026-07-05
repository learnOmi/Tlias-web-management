package org.example.controller;

import org.example.pojo.LoginInfo;
import org.example.pojo.Result;
import org.example.service.AuthService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 认证Controller
 * 提供 Token 刷新接口
 */
@Slf4j
@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    /**
     * 刷新 Token
     * 使用 refreshToken 换取新的双 Token 对
     */
    @PostMapping("/refresh")
    public Result refreshToken(@RequestBody String refreshTokenJson) {
        // 解析 refreshToken（前端直接传入 refreshToken 字符串）
        String refreshToken = refreshTokenJson.replace("\"", "").trim();
        log.info("Token 刷新请求");

        LoginInfo loginInfo = authService.refreshToken(refreshToken);
        if (loginInfo != null) {
            return Result.success(loginInfo);
        }

        return Result.error("refreshToken已失效");
    }
}
