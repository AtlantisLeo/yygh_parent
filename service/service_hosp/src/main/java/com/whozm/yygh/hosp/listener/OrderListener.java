package com.whozm.yygh.hosp.listener;

import com.rabbitmq.client.Channel;
import com.whozm.yygh.hosp.service.ScheduleService;
import com.whozm.yygh.mq.MqConst;
import com.whozm.yygh.mq.RabbitService;
import com.whozm.yygh.vo.msm.MsmVo;
import com.whozm.yygh.vo.order.OrderMqVo;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author HZM
 * @date 2023/2/1
 */
@Component
public class OrderListener {

    @Autowired
    private ScheduleService scheduleService;

    @Autowired
    private RabbitService rabbitService;

    @RabbitListener(bindings = {
            @QueueBinding(
                    value = @Queue(name = MqConst.QUEUE_ORDER,durable = "true"),
                    exchange =  @Exchange(name = MqConst.EXCHANGE_DIRECT_ORDER),
                    key = MqConst.ROUTING_ORDER
            )
    })
    public void consume(OrderMqVo orderMqVo, Message message, Channel channel){
        String scheduleId = orderMqVo.getScheduleId();
        Integer availableNumber = orderMqVo.getAvailableNumber();
        MsmVo msmVo = orderMqVo.getMsmVo();
        if (availableNumber!=null){
            boolean flag = scheduleService.updateavailableNumber(scheduleId,availableNumber);

        }else {
            boolean flag = scheduleService.cancelSchedule(scheduleId);
        }
        if (msmVo!=null){
            rabbitService.sendMessage(MqConst.EXCHANGE_DIRECT_SMS,MqConst.ROUTING_SMS_ITEM,msmVo);
        }
    }
}
