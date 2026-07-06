package org.example.mapper;

import org.example.pojo.FrontendLog;
import org.example.pojo.FrontendLogQueryParam;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 前端日志 Mapper 接口
 */
@Mapper
public interface FrontendLogMapper {

    /**
     * 插入前端日志
     */
    @Insert("INSERT INTO frontend_log " +
            "(emp_id, type, level, message, url, user_agent, operate_ip, create_time) " +
            "VALUES (#{empId}, #{type}, #{level}, #{message}, #{url}, #{userAgent}, #{operateIp}, #{createTime})")
    void insert(FrontendLog log);

    /**
     * 批量插入前端日志
     */
    void batchInsert(@Param("list") List<FrontendLog> logs);

    /**
     * 分页查询前端日志
     */
    List<FrontendLog> selectByPage(@Param("param") FrontendLogQueryParam param);
}
