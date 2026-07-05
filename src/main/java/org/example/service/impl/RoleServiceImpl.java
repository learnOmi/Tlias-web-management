package org.example.service.impl;

import org.example.mapper.EmpRoleMapper;
import org.example.pojo.Role;
import org.example.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 角色服务实现类
 */
@Service
public class RoleServiceImpl implements RoleService {

    @Autowired
    private EmpRoleMapper empRoleMapper;

    @Override
    public List<Role> selectRolesByEmpId(Integer empId) {
        return empRoleMapper.selectRolesByEmpId(empId);
    }
}
