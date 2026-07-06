package org.example.mapper;

import org.example.pojo.DictData;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 数据字典 Mapper 接口
 */
@Mapper
public interface DictDataMapper {

    /**
     * 根据字典类型查询字典列表
     */
    List<DictData> selectByType(@Param("type") String type);

    /**
     * 查询全部字典数据
     */
    List<DictData> selectAll();
}
