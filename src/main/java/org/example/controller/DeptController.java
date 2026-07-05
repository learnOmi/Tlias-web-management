package org.example.controller;

import lombok.extern.slf4j.Slf4j;
import org.example.anno.Log;
import org.example.anno.PreAuthorize;
import org.example.pojo.Dept;
import org.example.pojo.Result;
import org.example.service.DeptService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RequestMapping("/depts")
@RestController
public class DeptController {

    @Autowired
    private DeptService deptService;

    /**
     * 查询部门列表
     */
    @PreAuthorize("system:dept:list")
    @GetMapping
    public Result list(){
        //System.out.println("查询全部部门数据");
        log.info("查询全部部门数据");
        List<Dept> deptList = deptService.findAll();
        return Result.success(deptList);
    }

    /**
     * 删除部门 - 省略@RequestParam (前端传递的请求参数名与服务端方法形参名一致) [推荐]
     */
    @Log
    @PreAuthorize("system:dept:delete")
    @DeleteMapping
    public Result delete(Integer id){
        //System.out.println("根据ID删除部门: " + id);
        log.info("根据ID删除部门: {}", id);
        deptService.deleteById(id);
        return Result.success();
    }

    /**
     * 新增部门
     */
    @Log
    @PreAuthorize("system:dept:add")
    @PostMapping
    public Result add(@RequestBody Dept dept){
        //System.out.println("新增部门: " + dept);
        log.info("新增部门:{}", dept);
        deptService.add(dept);
        return Result.success();
    }

    /**
     * 根据ID查询部门
     */
    @PreAuthorize("system:dept:list")
    @GetMapping("/{id}")
    public Result getInfo(@PathVariable Integer id){
        //System.out.println("根据ID查询部门 : " + id);
        log.info("根据ID查询部门: {}", id);
        Dept dept = deptService.getById(id);
        return Result.success(dept);
    }

    /**
     * 修改部门
     */
    @Log
    @PreAuthorize("system:dept:edit")
    @PutMapping
    public Result update(@RequestBody Dept dept){
        //System.out.println("修改部门: " + dept);
        log.info("修改部门:{}", dept);
        deptService.update(dept);
        return Result.success();
    }

}
