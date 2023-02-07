package com.whozm.yygh.hosp.controller.api;

import com.whozm.yygh.common.exception.YyghException;
import com.whozm.yygh.common.utils.MD5;
import com.whozm.yygh.hosp.repository.ScheduleRepository;
import com.whozm.yygh.hosp.result.Result;
import com.whozm.yygh.hosp.service.HospitalService;
import com.whozm.yygh.hosp.service.ScheduleService;
import com.whozm.yygh.hosp.utils.HttpRequestHelper;
import com.whozm.yygh.model.hosp.Schedule;
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
 * @date 2023/1/23
 */
@RestController
@RequestMapping("/api/hosp")
public class ApiScheduleController {

    @Autowired
    private HospitalService hospitalService;
    @Autowired
    private ScheduleService scheduleService;


    @PostMapping("/saveSchedule")
    public Result saveSchedule(HttpServletRequest request){
        Map<String,Object> map = HttpRequestHelper.switchMap(request.getParameterMap());
        boolean isTrue = signIsTrue(map);
        if (isTrue){
            scheduleService.saveSchedule(map);
            return Result.ok();
        }else {
            throw new YyghException(20001,"保存失败");
        }
    }

    @PostMapping("/schedule/list")
    public Result<Page> getScheduleList(HttpServletRequest request){
        Map<String,Object> map = HttpRequestHelper.switchMap(request.getParameterMap());
        boolean isTrue = signIsTrue(map);
        if (isTrue){
            Page<Schedule> page = scheduleService.getScheduleList(map);
            return Result.ok(page);
        }else {
            throw new YyghException(20001,"查询失败");
        }
    }

    @PostMapping("/schedule/remove")
    public Result removeSchedule(HttpServletRequest request){
        Map<String,Object> map = HttpRequestHelper.switchMap(request.getParameterMap());
        boolean isTrue = signIsTrue(map);
        if (isTrue){
            scheduleService.removeSchedule(map);
            return Result.ok();
        }else {
            throw new YyghException(20001,"查询失败");
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
