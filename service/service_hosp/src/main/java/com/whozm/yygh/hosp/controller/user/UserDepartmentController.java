package com.whozm.yygh.hosp.controller.user;

import com.whozm.yygh.common.result.R;
import com.whozm.yygh.hosp.service.DepartmentService;
import com.whozm.yygh.vo.hosp.DepartmentVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author HZM
 * @date 2023/1/26
 */
@RestController
@RequestMapping("/user/hosp/department")
public class UserDepartmentController {

    @Autowired
    private DepartmentService departmentService;

    @GetMapping("/all/{hoscode}")
    public R findAll(@PathVariable String hoscode){
        List<DepartmentVo> departmentList = departmentService.getDepartmentList(hoscode);
        return R.ok().data("list",departmentList);
    }
}
