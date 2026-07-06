package org.example.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.example.anno.PreAuthorize;
import org.example.pojo.PageResult;
import org.example.pojo.Result;
import org.example.pojo.Student;
import org.example.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/students")
@Tag(name = "学员管理", description = "学员 CRUD 接口")
public class StudentController {

    @Autowired
    private StudentService studentService;

    /**
     * 添加学生
     */
    @Operation(summary = "新增学员")
    @PreAuthorize("stu:stu:add")
    @PostMapping
    public Result save(@RequestBody Student student){
        studentService.save(student);
        return Result.success();
    }

    /**
     * 条件分页查询
     */
    @Operation(summary = "分页查询学员")
    @PreAuthorize("stu:stu:list")
    @GetMapping
    public Result page(String name ,
                       Integer degree,
                       Integer clazzId,
                       @RequestParam(defaultValue = "1") Integer page ,
                       @RequestParam(defaultValue = "10") Integer pageSize){
        PageResult pageResult = studentService.page(name,degree,clazzId,page,pageSize);
        return Result.success(pageResult);
    }

    /**
     * 根据ID查询学生信息
     */
    @Operation(summary = "根据ID查询学员")
    @PreAuthorize("stu:stu:list")
    @GetMapping("/{id}")
    public Result getInfo(@PathVariable Integer id){
        Student student = studentService.getInfo(id);
        return Result.success(student);
    }

    /**
     * 修改学生信息
     */
    @Operation(summary = "修改学员")
    @PreAuthorize("stu:stu:edit")
    @PutMapping
    public Result update(@RequestBody Student student){
        studentService.update(student);
        return Result.success();
    }

    /**
     * 删除学生信息
     */
    @Operation(summary = "删除学员")
    @PreAuthorize("stu:stu:delete")
    @DeleteMapping("/{ids}")
    public Result delete(@PathVariable List<Integer> ids){
        studentService.delete(ids);
        return Result.success();
    }

    /**
     * 违纪处理
     */
    @Operation(summary = "学员违纪处理")
    @PreAuthorize("stu:stu:edit")
    @PutMapping("/violation/{id}/{score}")
    public Result violationHandle(@PathVariable Integer id , @PathVariable Integer score){
        studentService.violationHandle(id, score);
        return Result.success();
    }

}
