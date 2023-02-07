package com.whozm.yygh.user.service;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.whozm.yygh.model.user.Patient;

import java.util.List;

/**
 * <p>
 * 就诊人表 服务类
 * </p>
 *
 * @author HZM
 * @since 2023-01-28
 */
public interface PatientService extends IService<Patient> {


    List<Patient> findAll(String token);

    Patient detail(Long id);

    List<Patient> selectList(Long id);
}
