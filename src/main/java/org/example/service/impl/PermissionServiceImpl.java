package org.example.service.impl;

import org.example.mapper.PermissionMapper;
import org.example.service.PermissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 权限服务实现类
 */
@Service
public class PermissionServiceImpl implements PermissionService {

    @Autowired
    private PermissionMapper permissionMapper;

    @Override
    public List<String> selectPermissionsByEmpId(Integer empId) {
        return permissionMapper.selectCodesByEmpId(empId);
    }
}
