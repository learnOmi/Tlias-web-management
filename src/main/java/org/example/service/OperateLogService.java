package org.example.service;

import org.example.pojo.OperateLog;
import org.example.pojo.OperateLogQueryParam;
import org.example.pojo.PageResult;

/**
 * 操作日志服务接口
 */
public interface OperateLogService {

    /**
     * 分页查询操作日志
     *
     * @param param 查询参数
     * @return 分页结果
     */
    PageResult<OperateLog> pageByCondition(OperateLogQueryParam param);
}
