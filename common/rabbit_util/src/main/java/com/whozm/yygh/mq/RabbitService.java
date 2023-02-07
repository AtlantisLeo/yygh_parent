package com.whozm.yygh.mq;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author HZM
 * @date 2023/2/1
 */
@Component
public class RabbitService {
    @Autowired
    private RabbitTemplate rabbitTemplate;

    public boolean sendMessage(String exchange,String routingKey,Object message) {
        rabbitTemplate.convertAndSend(exchange,routingKey,message);
        return true;
    }
}
