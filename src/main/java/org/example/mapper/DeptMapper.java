package org.example.mapper;

import org.apache.ibatis.annotations.*;
import org.example.pojo.Dept;

import java.util.List;

/**
 * 部门管理Dao
 */
@Mapper
public interface DeptMapper {

    /**
     * 查询所有部门
     */
    @Select("select id,name,create_time,update_time from dept")
    List<Dept> selectAll();

    /**
     * 删除部门
     */
    @Delete("delete from dept where id=#{id}")
    void deleteById(Integer id);

    /**
     * 添加部门
     */
    @Insert("insert into dept(name,create_time,update_time) values(#{name},#{createTime},#{updateTime})")
    void insert(Dept dept);

    /**
     * 根据ID查询部门
     */
    @Select("select id,name,create_time,update_time from dept where id=#{id}")
    Dept selectById(Integer id);

    /**
     * 修改部门
     */
    @Update("update dept set name=#{name}, update_time=#{updateTime}, version=version+1 where id=#{id} and version=#{version}")
    int update(Dept dept);
}
