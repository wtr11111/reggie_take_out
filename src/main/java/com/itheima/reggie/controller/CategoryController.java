package com.itheima.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.reggie.common.R;
import com.itheima.reggie.entity.Category;
import com.itheima.reggie.service.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

//分类管理
@RestController
@RequestMapping("/category")
@Slf4j
public class CategoryController {
    @Autowired
    private CategoryService categoryService;

//    新增分类
    @PostMapping
    public R<String> save(@RequestBody Category category){
        log.info("category:{}",category);
        categoryService.save(category);
        return R.success("新增分类成功");
    }

//    分页查询
    @GetMapping("/page")
    public R<Page> page(int page,int pageSize){
//        分页构造器
        Page<Category> pageInfo=new Page<>(page,pageSize);
//        条件构造器
        LambdaQueryWrapper<Category> queryWrapper=new LambdaQueryWrapper<>();
//        添加排序调价，根据sort进行排序
        queryWrapper.orderByAsc(Category::getSort);
//        进行分页查询
        categoryService.page(pageInfo,queryWrapper);
        return R.success(pageInfo);
    }

//    根据id删除分类
    @DeleteMapping
    public R<String> delete(Long id){
        log.info("删除分类，id为{}",id);
//        categoryService.removeById(id);
//        先判断当前要删除的分类是否连接到其他菜品
        categoryService.remove(id);
        return R.success("分类信息删除成功");
    }

//    根据id修改分类
    @PutMapping
    public R<String> update(@RequestBody Category category){
        log.info("修改分类信息：{}",category);
        categoryService.updateById(category);
        return R.success("修改分类信息成功");
    }

//    文件上传
    @PostMapping("/upload")
    public R<String> upload(MultipartFile file){
        System.out.println(file);
        return null;
    }

//    根据条件查询分类数据
    @GetMapping("/list")
    public R<List<Category>> list(Category category){
//        条件构造器
        LambdaQueryWrapper<Category> queryWrapper=new LambdaQueryWrapper<>();
//        添加条件
        queryWrapper.eq(category.getType()!=null,Category::getType,category.getType());
//        添加排序条件
        queryWrapper.orderByAsc(Category::getSort).orderByDesc(Category::getUpdateTime);

        List<Category> list=categoryService.list(queryWrapper);
        return R.success(list);
    }

}
