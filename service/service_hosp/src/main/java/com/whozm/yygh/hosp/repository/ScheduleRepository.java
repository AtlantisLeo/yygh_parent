package com.whozm.yygh.hosp.repository;

import com.whozm.yygh.model.hosp.Schedule;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Date;
import java.util.List;

/**
 * @author HZM
 * @date 2023/1/23
 */
public interface ScheduleRepository extends MongoRepository<Schedule,String> {


    Schedule findByHoscodeAndDepcodeAndHosScheduleId(String hoscode, String depcode, String hosScheduleId);

    Schedule findByHoscodeAndHosScheduleId(String hoscode, String hosScheduleId);

    List<Schedule> findByHoscodeAndDepcodeAndWorkDate(String hoscode, String depcode, Date workdate);

    Schedule findByHosScheduleId(String scheduleId);
}
