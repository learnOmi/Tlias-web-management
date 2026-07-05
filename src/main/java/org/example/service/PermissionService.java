package org.example.service;

import java.util.List;

/**
 * 权限服务接口
 */
public interface PermissionService {

    /**
     * 根据员工ID查询所有权限代码
     */
    List<String> selectPermissionsByEmpId(Integer empId);
}
