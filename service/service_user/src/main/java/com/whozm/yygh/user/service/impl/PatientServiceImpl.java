package com.whozm.yygh.user.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.whozm.yygh.client.DictFeignClient;
import com.whozm.yygh.common.utils.JwtHelper;
import com.whozm.yygh.model.user.Patient;
import com.whozm.yygh.user.mapper.PatientMapper;
import com.whozm.yygh.user.service.PatientService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 就诊人表 服务实现类
 * </p>
 *
 * @author HZM
 * @since 2023-01-28
 */
@Service
public class PatientServiceImpl extends ServiceImpl<PatientMapper, Patient> implements PatientService {


    @Autowired
    private DictFeignClient dictFeignClient;

    @Override
    public List<Patient> findAll(String token) {

        Long userId = JwtHelper.getUserId(token);
        QueryWrapper<Patient> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id",userId);
        List<Patient> patientList = baseMapper.selectList(wrapper);
        patientList.stream().forEach(item->{
            this.packagePatient(item);
        });
        return patientList;
    }

    @Override
    public Patient detail(Long id) {
        Patient patient = baseMapper.selectById(id);
        this.packagePatient(patient);

        return patient;
    }

    @Override
    public List<Patient> selectList(Long id) {
        QueryWrapper<Patient> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id",id);
        List<Patient> patients = baseMapper.selectList(wrapper);
        patients.stream().forEach(item->{
            this.packagePatient(item);
        });
        return patients;
    }

    private void packagePatient(Patient item) {
        item.getParam().put("certificatesTypeString",dictFeignClient.getNameByValue(Long.parseLong(item.getCertificatesType())));
        String provinceString = dictFeignClient.getNameByValue(Long.parseLong(item.getProvinceCode()));
        String cityString = dictFeignClient.getNameByValue(Long.parseLong(item.getCityCode()));
        String districtString = dictFeignClient.getNameByValue(Long.parseLong(item.getDistrictCode()));
        item.getParam().put("provinceString",provinceString);
        item.getParam().put("cityString",cityString);
        item.getParam().put("districtString",districtString);
        item.getParam().put("fullAddress",provinceString+cityString+districtString+item.getAddress());
    }
}
