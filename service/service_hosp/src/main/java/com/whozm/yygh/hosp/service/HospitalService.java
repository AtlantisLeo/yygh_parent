package com.whozm.yygh.hosp.service;

import com.whozm.yygh.model.hosp.Hospital;
import com.whozm.yygh.vo.hosp.HospitalQueryVo;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Map;

/**
 * @author HZM
 * @date 2023/1/22
 */
public interface HospitalService {
    void saveHospital(Map<String, Object> result);


    String getSignkeyWithHoscode(String requestHoscode);

    Hospital getHospitalHoscode(String requestHoscode);

    Page<Hospital> getHosptialPage(Integer pageNum, Integer pageSize, HospitalQueryVo hospitalQueryVo);

    void updateStatus(String id, Integer status);

    Hospital detail(String id);

    List<Hospital> findByName(String hosname);

    Hospital getHospitalDetail(String hoscode);
}
