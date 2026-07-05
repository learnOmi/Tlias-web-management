package org.example.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.example.pojo.Role;

import java.util.List;

/**
 * 员工-角色关联Mapper接口
 */
@Mapper
public interface EmpRoleMapper {

    /**
     * 根据员工ID查询角色列表
     */
    List<Role> selectRolesByEmpId(@Param("empId") Integer empId);
}
