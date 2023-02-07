package com.whozm.yygh.order.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.whozm.yygh.model.order.OrderInfo;
import com.whozm.yygh.model.order.PaymentInfo;

public interface PaymentService extends IService<PaymentInfo> {
    /**
     * 保存交易记录
     */
    void savePaymentInfo(OrderInfo order, Integer paymentType);
}