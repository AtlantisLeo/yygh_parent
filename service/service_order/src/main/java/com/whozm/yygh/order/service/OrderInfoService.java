package com.whozm.yygh.order.service;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.whozm.yygh.model.order.OrderInfo;
import com.whozm.yygh.vo.order.OrderCountQueryVo;
import com.whozm.yygh.vo.order.OrderQueryVo;

import java.text.ParseException;
import java.util.Map;

/**
 * <p>
 * 订单表 服务类
 * </p>
 *
 * @author HZM
 * @since 2023-01-31
 */
public interface OrderInfoService extends IService<OrderInfo> {

    Long saveOrder(String scheduleId, Long patientId);

    Page<OrderInfo> getOrderInfoPage(Integer pageNum, Integer pageSize, OrderQueryVo orderQueryVo);

    OrderInfo getOrderDetail(Long orderId);

    void cancelOrder(Long orderId);

    void printRemind();

    Map<String, Object> statistics(OrderCountQueryVo orderCountQueryVo);
}
