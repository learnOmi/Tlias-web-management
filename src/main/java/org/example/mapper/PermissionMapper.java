package org.example.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.example.pojo.Permission;

import java.util.List;

/**
 * 权限Mapper接口
 */
@Mapper
public interface PermissionMapper {

    /**
     * 根据员工ID查询所有权限代码
     */
    List<String> selectCodesByEmpId(@Param("empId") Integer empId);
}
