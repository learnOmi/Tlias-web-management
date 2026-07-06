package org.example.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.example.mapper.FrontendLogMapper;
import org.example.pojo.FrontendLog;
import org.example.pojo.FrontendLogQueryParam;
import org.example.pojo.PageResult;
import org.example.service.FrontendLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * 前端日志服务实现
 */
@Service
public class FrontendLogServiceImpl implements FrontendLogService {

    @Autowired
    private FrontendLogMapper frontendLogMapper;

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void save(FrontendLog log) {
        log.setCreateTime(LocalDateTime.now());
        frontendLogMapper.insert(log);
    }

    @Override
    public PageResult<FrontendLog> pageByCondition(FrontendLogQueryParam param) {
        PageHelper.startPage(param.getPage(), param.getPageSize());
        java.util.List<FrontendLog> list = frontendLogMapper.selectByPage(param);
        Page<FrontendLog> page = (Page<FrontendLog>) list;
        return new PageResult<>(page.getTotal(), page.getResult());
    }
}
