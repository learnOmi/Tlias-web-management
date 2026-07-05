package org.example.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.example.pojo.Role;

import java.util.List;

/**
 * 角色Mapper接口
 */
@Mapper
public interface RoleMapper {

    /**
     * 根据员工ID查询角色列表
     */
    List<Role> selectByEmpId(@Param("empId") Integer empId);

    /**
     * 根据角色ID查询权限列表
     */
    List<String> selectPermissionCodesByRoleId(@Param("roleId") Integer roleId);
}
