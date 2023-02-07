package com.whozm.yygh.hosp.controller.api;

import com.whozm.yygh.common.exception.YyghException;
import com.whozm.yygh.common.utils.MD5;
import com.whozm.yygh.hosp.result.Result;
import com.whozm.yygh.hosp.service.HospitalService;
import com.whozm.yygh.hosp.utils.HttpRequestHelper;
import com.whozm.yygh.model.hosp.Hospital;
import org.springframework.beans.factory.annotation.Autowired;
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
public class ApiHospitalController {

    @Autowired
    private HospitalService hospitalService;

    @PostMapping("/saveHospital")
    public Result saveHospital(HttpServletRequest request){
        Map<String,Object> map = HttpRequestHelper.switchMap(request.getParameterMap());
        boolean isTrue = signIsTrue(map);
        if (isTrue){
            String logoData = (String) map.get("logoData");
            String s = logoData.replaceAll(" ", "+");
            map.put("logoData",s);
            hospitalService.saveHospital(map);
            return Result.ok();
        }else {
            throw new YyghException(20001,"保存失败");
        }

    }
    
    @PostMapping("/hospital/show")
    public Result<Hospital> getHospitalInfo(HttpServletRequest request){
        Map<String, Object> map = HttpRequestHelper.switchMap(request.getParameterMap());
            String requestHoscode = (String) map.get("hoscode");
            Hospital hospital = hospitalService.getHospitalHoscode(requestHoscode);
            return Result.ok(hospital);
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
