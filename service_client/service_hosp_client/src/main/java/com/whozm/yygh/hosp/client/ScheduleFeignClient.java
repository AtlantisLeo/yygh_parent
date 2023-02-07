package com.whozm.yygh.hosp.client;

import com.whozm.yygh.vo.hosp.ScheduleOrderVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * @author HZM
 * @date 2023/1/31
 */

@FeignClient(value = "service-hosp")
public interface ScheduleFeignClient {

    @GetMapping("/user/hosp/schedule/{scheduleId}")
    public ScheduleOrderVo getScheduleById(@PathVariable(value = "scheduleId") String scheduleId);
}
