package com.whozm.yygh.statistics.service.impl;

import com.whozm.yygh.order.client.OrderFeignClient;
import com.whozm.yygh.statistics.service.StatisticsService;
import com.whozm.yygh.vo.order.OrderCountQueryVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * @author HZM
 * @date 2023/2/3
 */
@Service
public class StatisticsServiceImpl implements StatisticsService {

    @Autowired
    private OrderFeignClient orderFeignClient;

    @Override
    public Map<String, Object> getCountMap(OrderCountQueryVo orderCountQueryVo) {
        return orderFeignClient.statistics(orderCountQueryVo);
    }
}
