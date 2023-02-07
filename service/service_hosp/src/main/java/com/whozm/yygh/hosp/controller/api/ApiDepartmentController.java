package com.whozm.yygh.hosp.controller.api;

import com.whozm.yygh.common.exception.YyghException;
import com.whozm.yygh.common.utils.MD5;
import com.whozm.yygh.hosp.result.Result;
import com.whozm.yygh.hosp.service.DepartmentService;
import com.whozm.yygh.hosp.service.HospitalService;
import com.whozm.yygh.hosp.service.HospitalSetService;
import com.whozm.yygh.hosp.utils.HttpRequestHelper;
import com.whozm.yygh.model.hosp.Department;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * @author HZM
 * @date 2023/1/22
 */
@RestController
@RequestMapping("/api/hosp")
public class ApiDepartmentController {

    @Autowired
    private DepartmentService departmentService;
    @Autowired
    private HospitalService hospitalService;

    @PostMapping("/saveDepartment")
    public Result saveDepartment(HttpServletRequest request){
        Map<String,Object> map = HttpRequestHelper.switchMap(request.getParameterMap());
        boolean isTrue = signIsTrue(map);
        if (isTrue){
            departmentService.saveDepartment(map);
            return Result.ok();
        }else {
            throw new YyghException(20001,"保存失败");
        }
    }
    @PostMapping("/department/list")
    public Result<Page> findDepartment(HttpServletRequest request){
        Map<String,Object> map = HttpRequestHelper.switchMap(request.getParameterMap());
        boolean isTrue = signIsTrue(map);
        if (isTrue){
            Page<Department> page = departmentService.getDepartmentList(map);
            return Result.ok(page);
        }else {
            throw new YyghException(20001,"保存失败");
        }
    }
    @PostMapping("/department/remove")
    public Result removeDepartment(HttpServletRequest request){
        Map<String,Object> map = HttpRequestHelper.switchMap(request.getParameterMap());
        boolean isTrue = signIsTrue(map);
        if (isTrue){
            departmentService.removeDepartment(map);
            return Result.ok();
        }else {
            throw new YyghException(20001,"删除失败");
        }
    }


    private boolean signIsTrue(Map<String, Object> map) {
        String requestSign = (String) map.get("sign");
        String requestHoscode = (String) map.get("hoscode");
        String platSign = hospitalService.getSignkeyWithHoscode(requestHoscode);
        String encrypt = MD5.encrypt(platSign);
        if (!StringUtils.isEmpty(requestSign) && !StringUtils.isEmpty(encrypt) && encrypt.equals(requestSign)){
            return true;
        }else {
            return false;
        }
    }
}
