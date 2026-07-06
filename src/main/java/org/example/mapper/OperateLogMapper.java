package org.example.mapper;

import org.example.pojo.OperateLog;
import org.example.pojo.OperateLogQueryParam;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 操作日志 Mapper 接口
 */
@Mapper
public interface OperateLogMapper {

    /**
     * 插入操作日志
     */
    void insert(OperateLog log);

    /**
     * 分页查询操作日志
     */
    List<OperateLog> selectByPage(@Param("param") OperateLogQueryParam param);
}
