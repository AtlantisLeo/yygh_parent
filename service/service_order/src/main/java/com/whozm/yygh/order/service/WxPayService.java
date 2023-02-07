package com.whozm.yygh.order.service;

import java.util.Map;

public interface WxPayService {
    /**
     * 根据订单号下单，生成支付链接
     */
    String createNative(Long orderId);

    Map<String, String> getPayStatus(Long orderId);

    void paySuccess(Long orderId,Map<String,String> map);

    boolean refund(Long orderId);
}