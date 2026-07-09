package org.example.mapper;

import org.apache.ibatis.annotations.*;
import org.example.pojo.Emp;
import org.example.pojo.EmpQueryParam;
import org.example.pojo.PageResult;

import java.util.List;
import java.util.Map;

/**
 * 员工DAO
 */
@Mapper
public interface EmpMapper {

    /**
     * 分页查询员工
     */
    List<Emp> selectByPage(EmpQueryParam empQueryParam);

    /**
     * 查询员工信息和工作经历信息
     */
    Emp selectEmpExpById(Integer id);

    /**
     * 查询员工总记录数
     */
    @Select("select count(*) from emp left join dept on emp.dept_id = dept.id")
    Long selectCount();

    /**
     * 新增员工基本信息
     */
    @Options(useGeneratedKeys = true, keyProperty = "id") //获取到生成的主键 -- 主键返回
    @Insert("insert into emp(username, name, gender, phone, job, salary, image, entry_date, dept_id, create_time, update_time)" +
            " values (#{username}, #{name}, #{gender},#{phone},#{job},#{salary},#{image},#{entryDate},#{deptId},#{createTime},#{updateTime})")
    void insert(Emp emp);

    /**
     * 删除员工
     */
    void deleteByIds(List<Integer> ids);

    /**
     * 修改员工信息
     */
    int updateById(Emp emp);

    /**
     * 统计员工职位人数
     */
    List<Map<String, Object>> countEmpJobData();

    /**
     * 统计员工性别人数
     */
    List<Map<String, Object>> countEmpGenderData();

    /**
     * 根据用户名和密码查询员工信息
     */
    @Select("select id, username, name from emp where username = #{username} and password = #{password}")
    Emp selectByUsernameAndPassword(Emp emp);

}
