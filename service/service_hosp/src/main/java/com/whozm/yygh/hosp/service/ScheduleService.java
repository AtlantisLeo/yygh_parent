package com.whozm.yygh.hosp.service;

import com.whozm.yygh.model.hosp.Schedule;
import com.whozm.yygh.vo.hosp.ScheduleOrderVo;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Map;

/**
 * @author HZM
 * @date 2023/1/23
 */
public interface ScheduleService {
    void saveSchedule(Map<String, Object> map);

    Page<Schedule> getScheduleList(Map<String, Object> map);

    void removeSchedule(Map<String, Object> map);

    Map<String, Object> getSchedulePage(Integer pageNum, Integer pageSize, String hoscode, String depcode);

    List<Schedule> detail(String hoscode, String depcode, String workdate);

    Map<String, Object> getSchedulePageByUser(String hoscode, String depcode, Integer pageNum, Integer pageSize);

    Schedule getScheduleInfo(String scheduleId);

    ScheduleOrderVo getScheduleById(String scheduleId);

    boolean updateavailableNumber(String scheduleId, Integer availableNumber);

    boolean cancelSchedule(String scheduleId);
}
