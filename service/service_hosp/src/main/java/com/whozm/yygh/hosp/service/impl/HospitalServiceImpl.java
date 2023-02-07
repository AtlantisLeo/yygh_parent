package com.whozm.yygh.hosp.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.whozm.yygh.client.DictFeignClient;
import com.whozm.yygh.common.exception.YyghException;
import com.whozm.yygh.enums.DictEnum;
import com.whozm.yygh.hosp.repository.HosptialRepository;
import com.whozm.yygh.hosp.service.HospitalService;
import com.whozm.yygh.hosp.service.HospitalSetService;
import com.whozm.yygh.model.hosp.Hospital;
import com.whozm.yygh.model.hosp.HospitalSet;
import com.whozm.yygh.vo.hosp.HospitalQueryVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import springfox.documentation.spring.web.json.Json;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * @author HZM
 * @date 2023/1/22
 */
@Service
public class HospitalServiceImpl implements HospitalService {

    @Autowired
    private HosptialRepository hosptialRepository;

    @Autowired
    private HospitalSetService hospitalSetService;

    @Autowired
    private DictFeignClient dictFeignClient;

    @Override
    public void saveHospital(Map<String, Object> result) {

        Hospital hospital = JSONObject.parseObject(JSONObject.toJSONString(result), Hospital.class);
        String hoscode = hospital.getHoscode();
        Hospital byHoscode = hosptialRepository.findByHoscode(hoscode);
        if (byHoscode == null){
            hospital.setStatus(0);
            hospital.setCreateTime(new Date());
            hospital.setUpdateTime(new Date());
            hospital.setIsDeleted(0);
            hosptialRepository.save(hospital);
        }else {
            hospital.setId(byHoscode.getId());
            hospital.setCreateTime(byHoscode.getCreateTime());
            hospital.setCreateTime(new Date());
            hospital.setStatus(byHoscode.getStatus());
            hospital.setIsDeleted(byHoscode.getIsDeleted());
            hosptialRepository.save(hospital);
        }


    }

    @Override
    public String getSignkeyWithHoscode(String requestHoscode) {
        QueryWrapper<HospitalSet> wrapper = new QueryWrapper<>();
        wrapper.eq("hoscode",requestHoscode);
        HospitalSet hospitalSet = hospitalSetService.getOne(wrapper);
        if (hospitalSet == null){
            throw new YyghException(20001,"医院信息不存在");
        }

        return hospitalSet.getSignKey();
    }

    @Override
    public Hospital getHospitalHoscode(String hoscode) {
        return hosptialRepository.findByHoscode(hoscode);
    }

    @Override
    public Page<Hospital> getHosptialPage(Integer pageNum, Integer pageSize, HospitalQueryVo hospitalQueryVo) {
        Hospital hospital = new Hospital();
        BeanUtils.copyProperties(hospitalQueryVo,hospital);

        //创建匹配器，即如何使用查询条件
        ExampleMatcher matcher = ExampleMatcher.matching() //构建对象
                .withMatcher("hosname",ExampleMatcher.GenericPropertyMatchers.contains()) //改变默认字符串匹配方式：模糊查询
                .withIgnoreCase(true); //改变默认大小写忽略方式：忽略大小写

        Example<Hospital> example = Example.of(hospital,matcher);
        PageRequest pageRequest = PageRequest.of(pageNum - 1, pageSize,Sort.by("createTime").ascending());
        Page<Hospital> page = hosptialRepository.findAll(example, pageRequest);

        page.getContent().stream().forEach(item -> {
            this.packageHospital(item);
        });

        return page;
    }

    @Override
    public void updateStatus(String id, Integer status) {
        if (status == 0 || status == 1){
            Hospital hospital = hosptialRepository.findById(id).get();
            hospital.setStatus(status);
            hospital.setUpdateTime(new Date());
            hosptialRepository.save(hospital);
        }
    }

    @Override
    public Hospital detail(String id) {
        Hospital hospital = hosptialRepository.findById(id).get();
        this.packageHospital(hospital);
        return hospital;
    }

    @Override
    public List<Hospital> findByName(String hosname) {
        return hosptialRepository.findByHosnameLike(hosname);
    }

    @Override
    public Hospital getHospitalDetail(String hoscode) {
        Hospital hospital = hosptialRepository.findByHoscode(hoscode);
        this.packageHospital(hospital);
        return hospital;
    }

    private void packageHospital(Hospital hospital){
        String hostype = hospital.getHostype();

        String provinceCode = hospital.getProvinceCode();
        String cityCode = hospital.getCityCode();
        String districtCode = hospital.getDistrictCode();

        String provinceAddress = dictFeignClient.getNameByValue(Long.parseLong(provinceCode));
        String cityAddress = dictFeignClient.getNameByValue(Long.parseLong(cityCode));
        String districtAddress = dictFeignClient.getNameByValue(Long.parseLong(districtCode));

        String level = dictFeignClient.getNameByDictCode(DictEnum.HOSTYPE.getDictCode(), Long.parseLong(hostype));

        hospital.getParam().put("hostypeString",level);
        hospital.getParam().put("fullAddress",provinceAddress+cityAddress+districtAddress+hospital.getAddress());
    }
}
