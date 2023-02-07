package com.whozm.yygh.statistics.service;

import com.whozm.yygh.vo.order.OrderCountQueryVo;

import java.util.Map;

/**
 * @author HZM
 * @date 2023/2/3
 */
public interface StatisticsService {

    Map<String, Object> getCountMap(OrderCountQueryVo orderCountQueryVo);

}
