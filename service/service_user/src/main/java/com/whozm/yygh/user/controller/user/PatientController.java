package com.whozm.yygh.user.controller.user;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.whozm.yygh.common.result.R;
import com.whozm.yygh.common.utils.JwtHelper;
import com.whozm.yygh.model.user.Patient;
import com.whozm.yygh.user.service.PatientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

/**
 * <p>
 * 就诊人表 前端控制器
 * </p>
 *
 * @author HZM
 * @since 2023-01-28
 */
@RestController
@RequestMapping("/user/userinfo/patient")
public class PatientController {

    @Autowired
    private PatientService patientService;

    @PostMapping("/save")
    public R  save(@RequestBody Patient patient, @RequestHeader String token){
        Long userId = JwtHelper.getUserId(token);
        patient.setUserId(userId);
        patientService.save(patient);
        return R.ok();
    }
    @DeleteMapping("/delete/{id}")
    public R delete(@PathVariable Long id){
        patientService.removeById(id);
        return R.ok();
    }

    @GetMapping("/detail/{id}")
    public R detail(@PathVariable Long id){
        Patient patient = patientService.detail(id);
        return R.ok().data("patient",patient);
    }
    @PutMapping("/update")
    public R update(@RequestBody Patient patient){
        patient.setUpdateTime(new Date());
        patientService.updateById(patient);
        return R.ok();
    }

    @GetMapping("/all")
    public R findAll(@RequestHeader String token){
        List<Patient> list = patientService.findAll(token);
        return R.ok().data("list",list);
    }

    @GetMapping("/{patientId}")
    public Patient getPatientById(@PathVariable(value = "patientId") Long patientId){
        return patientService.getById(patientId);
    }

}

