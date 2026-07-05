package org.example.service;

import org.example.pojo.Emp;
import org.example.pojo.EmpQueryParam;
import org.example.pojo.LoginInfo;
import org.example.pojo.PageResult;

import java.util.List;

public interface EmpService {

    PageResult<Emp> getByPage(EmpQueryParam empQueryParam);

    void save(Emp emp) throws Exception;

    void deleteByIds(List<Integer> ids);

    Emp getInfo(Integer id);

    void update(Emp emp);

    /**
     * 员工登录
     */
    LoginInfo login(Emp emp);

    /**
     * 根据ID获取用户信息
     */
    LoginInfo.UserInfo getUserInfoById(Integer id);

}
