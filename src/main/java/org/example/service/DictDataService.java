package org.example.service;

import org.example.pojo.DictItem;
import org.example.pojo.DictData;

import java.util.List;

/**
 * 数据字典服务接口
 */
public interface DictDataService {

    /**
     * 根据字典类型查询字典列表
     *
     * @param type 字典类型
     * @return 字典项列表（仅含 label 和 value）
     */
    List<DictItem> getByType(String type);

    /**
     * 查询全部字典数据
     *
     * @return 全部字典数据
     */
    List<DictData> getAll();
}
