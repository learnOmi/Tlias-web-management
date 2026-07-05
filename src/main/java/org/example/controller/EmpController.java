package org.example.controller;

import lombok.extern.slf4j.Slf4j;
import org.example.anno.PreAuthorize;
import org.example.pojo.Emp;
import org.example.pojo.EmpQueryParam;
import org.example.pojo.PageResult;
import org.example.pojo.Result;
import org.example.service.EmpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 员工管理Controller
 */
@Slf4j
@RestController
@RequestMapping("/emps")
public class EmpController {

    @Autowired
    private EmpService empService;

    /**
     * 分页查询
     */
    @PreAuthorize("system:emp:list")
    @GetMapping
    public Result getByPage(EmpQueryParam empQueryParam){
        log.info("分页查询: {}", empQueryParam);
        PageResult<Emp> pageResult = empService.getByPage(empQueryParam);
        return Result.success(pageResult);
    }

    /**
     * 根据ID查询员工信息
     */
    @PreAuthorize("system:emp:list")
    @GetMapping("/{id}")
    public Result getInfo(@PathVariable Integer id){
        log.info("根据ID查询员工信息: {}", id);
        Emp emp = empService.getInfo(id);
        return Result.success(emp);
    }

    /**
     * 新增员工
     */
    @PreAuthorize("system:emp:add")
    @PostMapping
    public Result save(@RequestBody Emp emp) throws Exception {
        log.info("新增员工: {}", emp);
        empService.save(emp);
        return Result.success();
    }

    /**
     * 删除员工
     */
    @PreAuthorize("system:emp:delete")
    @DeleteMapping
    public Result delete(@RequestParam List<Integer> ids){
        log.info("删除员工: {}", ids);
        empService.deleteByIds(ids);
        return Result.success();
    }

    /**
     * 修改员工
     */
    @PreAuthorize("system:emp:edit")
    @PutMapping
    public Result update(@RequestBody Emp emp){
        log.info("修改员工: {}", emp);
        empService.update(emp);
        return Result.success();
    }

}
