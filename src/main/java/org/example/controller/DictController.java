package org.example.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.example.pojo.DictData;
import org.example.pojo.DictItem;
import org.example.pojo.Result;
import org.example.service.DictDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 数据字典控制器
 */
@Slf4j
@RestController
@RequestMapping("/dicts")
@Tag(name = "数据字典", description = "数据字典查询接口")
public class DictController {

    @Autowired
    private DictDataService dictDataService;

    /**
     * 根据字典类型查询字典列表
     *
     * @param type 字典类型，如 emp_job、gender
     * @return 字典项列表（label/value）
     */
    @Operation(summary = "按类型查询字典")
    @GetMapping
    public Result getByType(@RequestParam(required = false) String type) {
        log.info("根据类型查询字典: {}", type);
        if (type == null || type.isEmpty()) {
            return Result.success();
        }
        List<DictItem> list = dictDataService.getByType(type);
        return Result.success(list);
    }

    /**
     * 查询全部字典数据
     *
     * @return 全部字典数据
     */
    @Operation(summary = "查询全部字典")
    @GetMapping("/all")
    public Result getAll() {
        log.info("查询全部字典数据");
        List<DictData> list = dictDataService.getAll();
        return Result.success(list);
    }
}
