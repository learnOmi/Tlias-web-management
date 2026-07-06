package org.example.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.example.mapper.OperateLogMapper;
import org.example.pojo.OperateLog;
import org.example.pojo.OperateLogQueryParam;
import org.example.pojo.PageResult;
import org.example.service.OperateLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 操作日志服务实现
 */
@Service
public class OperateLogServiceImpl implements OperateLogService {

    @Autowired
    private OperateLogMapper operateLogMapper;

    @Override
    public PageResult<OperateLog> pageByCondition(OperateLogQueryParam param) {
        PageHelper.startPage(param.getPage(), param.getPageSize());
        List<OperateLog> list = operateLogMapper.selectByPage(param);
        Page<OperateLog> page = (Page<OperateLog>) list;
        return new PageResult<>(page.getTotal(), page.getResult());
    }
}
