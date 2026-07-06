package org.example.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.example.pojo.LoginInfo;
import org.example.pojo.Result;
import org.example.service.PermissionService;
import org.example.service.RoleService;
import org.example.utils.CurrentHolder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 用户信息Controller
 * 提供获取当前用户信息的接口
 */
@Slf4j
@RestController
@RequestMapping("/user")
@Tag(name = "用户信息", description = "获取当前用户信息接口")
public class UserController {

    @Autowired
    private RoleService roleService;

    @Autowired
    private PermissionService permissionService;

    /**
     * 获取当前登录用户的信息（角色、权限）
     * 用于页面刷新后重新获取权限数据
     */
    @Operation(summary = "获取当前用户信息")
    @GetMapping("/info")
    public Result getUserInfo() {
        Integer empId = CurrentHolder.getCurrentId();
        if (empId == null) {
            return Result.error("未登录");
        }

        // 查询角色列表
        List<String> roleCodes = roleService.selectRolesByEmpId(empId).stream()
                .map(role -> role.getCode())
                .collect(Collectors.toList());

        // 查询权限列表
        List<String> permissions = permissionService.selectPermissionsByEmpId(empId);

        // 如果是管理员角色，授予全部权限
        if (roleCodes.contains("admin")) {
            permissions = List.of("*");
        }

        // 组装返回数据
        LoginInfo loginInfo = new LoginInfo();
        loginInfo.setId(empId);
        loginInfo.setRoles(roleCodes);
        loginInfo.setPermissions(permissions);

        log.info("获取用户信息成功, empId: {}, roles: {}, permissions: {}", empId, roleCodes, permissions);
        return Result.success(loginInfo);
    }
}
