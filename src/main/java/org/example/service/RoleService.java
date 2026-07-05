package org.example.service;

import org.example.pojo.Role;

import java.util.List;

/**
 * 角色服务接口
 */
public interface RoleService {

    /**
     * 根据员工ID查询角色列表
     */
    List<Role> selectRolesByEmpId(Integer empId);
}
