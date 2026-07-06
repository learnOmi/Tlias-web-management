package org.example.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import lombok.extern.slf4j.Slf4j;
import org.example.mapper.EmpExprMapper;
import org.example.mapper.EmpMapper;
import org.example.mapper.RefreshTokenMapper;
import org.example.pojo.*;
import org.example.service.EmpLogService;
import org.example.service.EmpService;
import org.example.service.PermissionService;
import org.example.service.RoleService;
import org.example.utils.JwtUtils;
import org.example.utils.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
public class EmpServiceImpl implements EmpService {

    @Autowired
    private EmpMapper empMapper;
    @Autowired
    private EmpExprMapper empExprMapper;
    @Autowired
    private EmpLogService empLogService;
    @Autowired
    private RoleService roleService;
    @Autowired
    private PermissionService permissionService;
    @Autowired
    private RefreshTokenMapper refreshTokenMapper;
    @Autowired
    private PermissionServiceImpl permissionServiceImpl;
    @Autowired
    private RoleServiceImpl roleServiceImpl;

    @Override
    public PageResult<Emp> getByPage(EmpQueryParam empQueryParam) {

        /*

        if (empQueryParam.getPage() == null || empQueryParam.getPage() <= 0) {
            empQueryParam.setPage(1);
        }
        if (empQueryParam.getPageSize() == null || empQueryParam.getPageSize() <= 0) {
            empQueryParam.setPageSize(10);
        }
        empQueryParam.setPage((empQueryParam.getPage() - 1) * empQueryParam.getPageSize());

        Long total = empMapper.selectCount();
        List<Emp> resultList = empMapper.selectByPage(empQueryParam);

         */

        // 使用PageHelper实现分页查询
        PageHelper.startPage(empQueryParam.getPage(), empQueryParam.getPageSize());
        List<Emp> resultList = empMapper.selectByPage(empQueryParam);
        Page<Emp> page = (Page<Emp>) resultList;

        return new PageResult<>(page.getTotal(), page.getResult());
    }

    @Transactional(rollbackFor = {Exception.class}) //事务管理 - 默认出现运行时异常RuntimeException才会回滚
    @Override
    public void save(Emp emp) throws Exception {
        try {
            //1. 保存员工基本信息
            emp.setCreateTime(LocalDateTime.now());
            emp.setUpdateTime(LocalDateTime.now());
            empMapper.insert(emp);

            //2. 保存员工工作经历信息
            List<EmpExpr> exprList = emp.getEmpExprs();
            if(!CollectionUtils.isEmpty(exprList)){
                //遍历集合, 为empId赋值
                exprList.forEach(empExpr -> {
                    empExpr.setEmpId(emp.getId());
                });
                empExprMapper.insertBatch(exprList);
            }
        } finally {
            //记录操作日志
            EmpLog empLog = new EmpLog(null, LocalDateTime.now(), "新增员工:" + emp);
            empLogService.insertLog(empLog);
        }
    }

    @Transactional(rollbackFor = {Exception.class})
    @Override
    public void deleteByIds(List<Integer> ids) {
        empMapper.deleteByIds(ids);
        empExprMapper.deleteByEmpIds(ids);
        // 清除缓存
        clearEmpCache(ids);
    }

    @Override
    public Emp getInfo(Integer id) {
        return empMapper.selectEmpExpById(id);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void update(Emp emp) {
        //1. 根据ID修改员工的基本信息
        emp.setUpdateTime(LocalDateTime.now());
        empMapper.updateById(emp);

        //2. 根据ID修改员工的工作经历信息
        //2.1 先根据员工ID删除原有的工作经历
        empExprMapper.deleteByEmpIds(Arrays.asList(emp.getId()));

        //2.2 再添加这个员工新的工作经历
        List<EmpExpr> exprList = emp.getEmpExprs();
        if(!CollectionUtils.isEmpty(exprList)){
            exprList.forEach(empExpr -> empExpr.setEmpId(emp.getId()));
            empExprMapper.insertBatch(exprList);
        }
        // 清除缓存
        clearEmpCache(List.of(emp.getId()));
    }

    @Override
    public LoginInfo login(Emp emp) {
        //1. 调用mapper接口, 根据用户名和密码查询员工信息
        Emp e = empMapper.selectByUsernameAndPassword(emp);

        //2. 判断: 判断是否存在这个员工, 如果存在, 组装登录成功信息
        if(e != null){
            log.info("登录成功, 员工信息: {}", e);

            // 查询角色和权限
            List<Role> roles = roleService.selectRolesByEmpId(e.getId());
            List<String> roleCodes = roles.stream()
                    .map(Role::getCode)
                    .collect(Collectors.toList());

            List<String> permissions = permissionService.selectPermissionsByEmpId(e.getId());

            // 如果是管理员角色，授予全部权限
            if (roleCodes.contains("admin")) {
                permissions = new ArrayList<>();
                permissions.add("*");
            }

            // 组装用户信息
            LoginInfo.UserInfo userInfo = new LoginInfo.UserInfo(
                    e.getId(),
                    e.getUsername(),
                    e.getName(),
                    e.getGender(),
                    e.getPhone(),
                    e.getJob(),
                    e.getDeptId(),
                    e.getDeptName(),
                    e.getImage()
            );

            // 生成 accessToken
            Map<String, Object> claims = new HashMap<>();
            claims.put("id", e.getId());
            claims.put("username", e.getUsername());
            String accessToken = JwtUtils.generateAccessToken(claims);

            // 生成 refreshToken
            Map<String, Object> refreshClaims = new HashMap<>();
            refreshClaims.put("id", e.getId());
            String refreshToken = JwtUtils.generateRefreshToken(refreshClaims);

            // 保存新 refreshToken 到数据库（多设备登录模式，每次登录生成独立的 refreshToken）
            RefreshToken tokenRecord = new RefreshToken();
            tokenRecord.setEmpId(e.getId());
            tokenRecord.setToken(refreshToken);
            tokenRecord.setExpireTime(LocalDateTime.now().plusDays(7));
            tokenRecord.setCreateTime(LocalDateTime.now());
            refreshTokenMapper.insert(tokenRecord);

            // 返回双 Token
            LoginInfo loginInfo = new LoginInfo();
            loginInfo.setId(e.getId());
            loginInfo.setUsername(e.getUsername());
            loginInfo.setName(e.getName());
            loginInfo.setAccessToken(accessToken);
            loginInfo.setRefreshToken(refreshToken);
            loginInfo.setExpiresIn(2 * 60 * 60L); // 2小时
            loginInfo.setRoles(roleCodes);
            loginInfo.setPermissions(permissions);
            loginInfo.setUserInfo(userInfo);

            return loginInfo;
        }

        //3. 不存在, 返回null
        return null;
    }

    @Override
    public LoginInfo.UserInfo getUserInfoById(Integer id) {
        Emp emp = empMapper.selectEmpExpById(id);
        if (emp == null) {
            return null;
        }
        return new LoginInfo.UserInfo(
                emp.getId(),
                emp.getUsername(),
                emp.getName(),
                emp.getGender(),
                emp.getPhone(),
                emp.getJob(),
                emp.getDeptId(),
                emp.getDeptName(),
                emp.getImage()
        );
    }

    /**
     * 清除指定员工 ID 列表的权限和角色缓存
     */
    private void clearEmpCache(List<Integer> empIds) {
        for (Integer empId : empIds) {
            permissionServiceImpl.clearCache(empId);
            roleServiceImpl.clearCache(empId);
        }
    }

}
