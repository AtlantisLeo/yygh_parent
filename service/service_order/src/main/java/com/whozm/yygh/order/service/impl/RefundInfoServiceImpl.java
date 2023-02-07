package com.whozm.yygh.order.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.whozm.yygh.enums.PaymentStatusEnum;
import com.whozm.yygh.enums.PaymentTypeEnum;
import com.whozm.yygh.enums.RefundStatusEnum;
import com.whozm.yygh.model.order.PaymentInfo;
import com.whozm.yygh.model.order.RefundInfo;
import com.whozm.yygh.order.mapper.RefundInfoMapper;
import com.whozm.yygh.order.service.RefundInfoService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

/**
 * @author HZM
 * @date 2023/2/2
 */
@Service
public class RefundInfoServiceImpl extends ServiceImpl<RefundInfoMapper, RefundInfo> implements RefundInfoService {
    @Override
    public RefundInfo saveRefundInfo(PaymentInfo paymentInfo) {
        QueryWrapper<RefundInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("order_id",paymentInfo.getOrderId());
        RefundInfo one = baseMapper.selectOne(queryWrapper);
        if (one != null){
            return one;
        }
        RefundInfo refundInfo = new RefundInfo();
        refundInfo.setOutTradeNo(paymentInfo.getOutTradeNo());
        refundInfo.setOrderId(paymentInfo.getOrderId());
        refundInfo.setPaymentType(PaymentTypeEnum.WEIXIN.getStatus());
        refundInfo.setTotalAmount(paymentInfo.getTotalAmount());
        refundInfo.setSubject(paymentInfo.getSubject());
        refundInfo.setRefundStatus(RefundStatusEnum.UNREFUND.getStatus());

        baseMapper.insert(refundInfo);
        return refundInfo;
    }
}
