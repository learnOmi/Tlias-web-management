package org.example.service;

import org.example.pojo.FrontendLog;
import org.example.pojo.FrontendLogQueryParam;
import org.example.pojo.PageResult;

/**
 * 前端日志服务接口
 */
public interface FrontendLogService {

    /**
     * 保存前端日志
     *
     * @param log 前端日志实体
     */
    void save(FrontendLog log);

    /**
     * 分页查询前端日志
     *
     * @param param 查询参数
     * @return 分页结果
     */
    PageResult<FrontendLog> pageByCondition(FrontendLogQueryParam param);
}
