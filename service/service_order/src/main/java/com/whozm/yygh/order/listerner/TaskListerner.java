package com.whozm.yygh.order.listerner;

import com.whozm.yygh.mq.MqConst;
import com.whozm.yygh.order.service.OrderInfoService;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.nio.channels.Channel;

/**
 * @author HZM
 * @date 2023/2/3
 */
@Component
public class TaskListerner {

    @Autowired
    private OrderInfoService orderInfoService;

    @RabbitListener(bindings = @QueueBinding(
           value = @Queue(value = MqConst.QUEUE_TASK_8),
           exchange = @Exchange(value = MqConst.EXCHANGE_DIRECT_TASK),
            key = MqConst.ROUTING_TASK_8
    ))
    public  void printRemind(Message message, Channel channel){
        orderInfoService.printRemind();
    }
}
