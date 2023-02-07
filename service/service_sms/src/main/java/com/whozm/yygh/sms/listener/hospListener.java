package com.whozm.yygh.sms.listener;

import com.rabbitmq.client.Channel;
import com.whozm.yygh.mq.MqConst;
import com.whozm.yygh.sms.service.SmsService;
import com.whozm.yygh.vo.msm.MsmVo;
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
public class hospListener {


    @Autowired
    private SmsService smsService;

    @RabbitListener(bindings = {
            @QueueBinding(
                   value = @Queue(name = MqConst.QUEUE_SMS_ITEM,durable = "true"),
                    exchange = @Exchange(name = MqConst.EXCHANGE_DIRECT_SMS),
                    key = MqConst.ROUTING_SMS_ITEM
            )

    })
    public void consumer(MsmVo msmVo, Message message, Channel channel){
        smsService.sendMessage(msmVo);

    }
}
