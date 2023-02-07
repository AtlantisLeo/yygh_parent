package com.whozm.yygh.hosp.controller.admin;

import com.whozm.yygh.common.result.R;
import com.whozm.yygh.hosp.service.ScheduleService;
import com.whozm.yygh.model.hosp.Schedule;
import lombok.experimental.Accessors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * @author HZM
 * @date 2023/1/25
 */
@RestController
@RequestMapping("/admin/hosp/schedule")
public class ScheduleController {

    @Autowired
    private ScheduleService scheduleService;

    @GetMapping("/{pageNum}/{pageSize}/{hoscode}/{depcode}")
    public R getSchedulePage(@PathVariable Integer pageNum, @PathVariable Integer pageSize,
                             @PathVariable String hoscode, @PathVariable String depcode){
        Map<String,Object> schedules = scheduleService.getSchedulePage(pageNum,pageSize,hoscode,depcode);

        return R.ok().data(schedules);
    }

    @GetMapping("/{hoscode}/{depcode}/{workdate}")
    public R detail(@PathVariable String hoscode, @PathVariable String depcode,
                             @PathVariable String workdate){
        if ("null".equals(workdate)){
            return R.ok().data("list",null);
        }
        List<Schedule> list = scheduleService.detail(hoscode,depcode,workdate);
        return R.ok().data("list",list);
    }
}
