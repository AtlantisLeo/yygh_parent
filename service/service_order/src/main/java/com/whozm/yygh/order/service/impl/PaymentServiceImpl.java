package com.whozm.yygh.order.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.whozm.yygh.enums.PaymentStatusEnum;
import com.whozm.yygh.model.order.OrderInfo;
import com.whozm.yygh.model.order.PaymentInfo;
import com.whozm.yygh.order.mapper.PaymentMapper;
import com.whozm.yygh.order.service.PaymentService;
import org.joda.time.DateTime;
import org.springframework.stereotype.Service;

/**
 * @author HZM
 * @date 2023/2/1
 */
@Service
public class PaymentServiceImpl extends ServiceImpl<PaymentMapper,PaymentInfo> implements PaymentService {

    @Override
    public void savePaymentInfo(OrderInfo order, Integer paymentType) {
        QueryWrapper<PaymentInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("order_id",order.getId());
        queryWrapper.eq("payment_type",paymentType);
        PaymentInfo selectOne = baseMapper.selectOne(queryWrapper);
        if (selectOne != null){
           return;
        }
        PaymentInfo paymentInfo = new PaymentInfo();
        paymentInfo.setOutTradeNo(order.getOutTradeNo());
        paymentInfo.setOrderId(order.getId());
        paymentInfo.setPaymentType(paymentType);
        paymentInfo.setTotalAmount(order.getAmount());
        String subject = new DateTime(order.getReserveDate()).toString("yyyy-MM-dd")+"|"+order.getHosname()+"|"+order.getDepname()+"|"+order.getTitle();
        paymentInfo.setSubject(subject);
        paymentInfo.setPaymentStatus(PaymentStatusEnum.UNPAID.getStatus());
        baseMapper.insert(paymentInfo);
    }
}
