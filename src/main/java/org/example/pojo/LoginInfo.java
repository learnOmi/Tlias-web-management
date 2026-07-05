package org.example.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 封装登录结果
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginInfo {
    private Integer id;
    private String username;
    private String name;
    
    // 新增：访问令牌（2小时有效）
    private String accessToken;
    
    // 新增：刷新令牌（7天有效）
    private String refreshToken;
    
    // 新增：accessToken 过期时间（秒）
    private Long expiresIn;
    
    // 角色列表
    private List<String> roles;
    
    // 权限代码列表
    private List<String> permissions;
    
    // 用户完整信息
    private UserInfo userInfo;
    
    /**
     * 用户信息内部类
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserInfo {
        private Integer id;
        private String username;
        private String name;
        private Integer gender;
        private String phone;
        private Integer job;
        private Integer deptId;
        private String deptName;
        private String image;
    }
    
    /**
     * 兼容旧版构造方法（仅 accessToken）
     */
    public LoginInfo(Integer id, String username, String name, String token) {
        this.id = id;
        this.username = username;
        this.name = name;
        this.accessToken = token;
    }
}
