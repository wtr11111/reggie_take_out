package com.itheima.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.reggie.common.R;
import com.itheima.reggie.entity.Employee;
import com.itheima.reggie.service.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;

@Slf4j
@RestController
@RequestMapping("/employee")
public class EmployeeController {
    @Autowired
    private EmployeeService employeeService;

//    员工登录
    @PostMapping("/login")
    public R<Employee> login(HttpServletRequest request, @RequestBody Employee employee){
//        1、将页面提交的密码password进行md5加密处理
        String password=employee.getPassword();
        password= DigestUtils.md5DigestAsHex(password.getBytes());

//        2、根据页面提交的用户名username查询数据库
//        ①包装员工查询对象
        LambdaQueryWrapper<Employee> queryWrapper=new LambdaQueryWrapper<>();
//        ②添加查询条件--根据username等值查询
        queryWrapper.eq(Employee::getUsername,employee.getUsername());
//        ③查询数据  封装成Employee对象
        Employee emp=employeeService.getOne(queryWrapper);

//        3、如果没有查询到则返回登陆失败结果
        if(emp==null){
            return R.error("登陆失败");
        }

//        4、密码对比，如果不一致则返回登陆失败结果
        if (!emp.getPassword().equals(password)){
            return R.error("登录失败");
        }

//        5、查看员工状态，如果为已经禁用状态，则返回员工已禁用结束
        if (emp.getStatus()==0){
            return R.error("员工已禁用");
        }

//        6、登陆成功，将员工id存入Session并返回登录成果结果
        request.getSession().setAttribute("employee",emp.getId());
            return R.success(emp);
    }

//员工退出登录
    @PostMapping("/logout")
    public R<String> logout(HttpServletRequest request){
//        清理Session中保存的当前登陆员工id
        request.getSession().removeAttribute("employee");
        return R.success("退出成功");
    }


//    新增员工
    @PostMapping
    public R<String> save(HttpServletRequest request,@RequestBody Employee employee){
        log.info("添加员工，员工信息：{}",employee.toString());

//        设置初始密码123456，需要进行md5加密处理
        employee.setPassword(DigestUtils.md5DigestAsHex("123456".getBytes()));
//        employee.setCreateTime(LocalDateTime.now());
//        employee.setUpdateTime(LocalDateTime.now());

//        获得当前等用户的id
//        Long empId= (Long) request.getSession().getAttribute("employee");
//        employee.setCreateUser(empId);
//        employee.setUpdateUser(empId);

        employeeService.save(employee);
        return R.success("新增员工成功");
    }

//    员工分页查询
    @GetMapping("page")
    public R<Page> page(int page,int pageSize,String name){
        log.info("page={},pageSize={},name={}",page,pageSize,name);
//        构造分页构造器
        Page pageInfo=new Page(page,pageSize);
//        构造条件构造器
        LambdaQueryWrapper<Employee> queryWrapper=new LambdaQueryWrapper<>();
//        添加过滤条件
        queryWrapper.like(StringUtils.isNotEmpty(name),Employee::getName,name);
//        添加排序条件
        queryWrapper.orderByDesc(Employee::getUpdateTime);
//        执行查询
        employeeService.page(pageInfo,queryWrapper);
        return R.success(pageInfo);
    }

//    根据员工id修改信息
    @PutMapping
    public R<String> update(HttpServletRequest request,@RequestBody Employee employee){
        log.info(employee.toString());
        //获取当前线程id，获取登录用户id
        long id=Thread.currentThread().getId();
        log.info("线程id：{}",id);
//        Long empId= (Long) request.getSession().getAttribute("employee");
//        employee.setUpdateTime(LocalDateTime.now());
//        employee.setUpdateUser(empId);
        employeeService.updateById(employee);
        return R.success("员工信息修改成功");
    }

    @GetMapping("/{id}")
    public R<Employee> getById(@PathVariable Long id){
        log.info("根据id查询员工信息");
        Employee employee=employeeService.getById(id);
        if (employee!=null){
            return R.success(employee);
        }
        return R.error("没有查询到此员工信息");
    }
}
