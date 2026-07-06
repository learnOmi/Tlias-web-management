package org.example.service.impl;

import org.example.mapper.DictDataMapper;
import org.example.pojo.DictItem;
import org.example.pojo.DictData;
import org.example.service.DictDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 数据字典服务实现
 */
@Service
public class DictDataServiceImpl implements DictDataService {

    @Autowired
    private DictDataMapper dictDataMapper;

    @Override
    public List<DictItem> getByType(String type) {
        List<DictData> list = dictDataMapper.selectByType(type);
        return list.stream()
                .map(item -> new DictItem(item.getLabel(), item.getValue()))
                .collect(Collectors.toList());
    }

    @Override
    public List<DictData> getAll() {
        return dictDataMapper.selectAll();
    }
}
