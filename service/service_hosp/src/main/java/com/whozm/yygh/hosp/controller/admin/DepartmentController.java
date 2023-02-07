package com.whozm.yygh.hosp.controller.admin;

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
 * @date 2023/1/25
 */
@RestController
@RequestMapping("/admin/hosp/department")
public class DepartmentController {

    @Autowired
    private DepartmentService departmentService;

    @GetMapping("/{hoscode}")
    public R getDepartmentList(@PathVariable String hoscode){
        List<DepartmentVo> departmentVoList = departmentService.getDepartmentList(hoscode);
        return R.ok().data("list",departmentVoList);
    }
}
