package com.whozm.yygh.task.job;

import com.whozm.yygh.mq.MqConst;
import com.whozm.yygh.mq.RabbitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * @author HZM
 * @date 2023/2/3
 */
@Component
public class PatientRemindJob {


    @Autowired
    private RabbitService rabbitService;

    @Scheduled(cron = "0 0 6 * * *")
    public void printTime(){
        rabbitService.sendMessage(MqConst.EXCHANGE_DIRECT_TASK,MqConst.ROUTING_TASK_8," ");
    }
}
