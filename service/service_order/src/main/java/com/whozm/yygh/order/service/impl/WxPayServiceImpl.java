package com.whozm.yygh.order.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.github.wxpay.sdk.WXPayUtil;
import com.sun.scenario.effect.impl.sw.sse.SSEBlend_SRC_OUTPeer;
import com.whozm.yygh.common.exception.YyghException;
import com.whozm.yygh.enums.OrderStatusEnum;
import com.whozm.yygh.enums.PaymentStatusEnum;
import com.whozm.yygh.enums.PaymentTypeEnum;
import com.whozm.yygh.enums.RefundStatusEnum;
import com.whozm.yygh.model.order.OrderInfo;
import com.whozm.yygh.model.order.PaymentInfo;
import com.whozm.yygh.model.order.RefundInfo;
import com.whozm.yygh.order.prop.WxPayProperties;
import com.whozm.yygh.order.service.OrderInfoService;
import com.whozm.yygh.order.service.PaymentService;
import com.whozm.yygh.order.service.RefundInfoService;
import com.whozm.yygh.order.service.WxPayService;
import com.whozm.yygh.order.utils.HttpClient;
import com.whozm.yygh.order.utils.HttpRequestHelper;
import org.joda.time.DateTime;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author HZM
 * @date 2023/2/2
 */
@Service
public class WxPayServiceImpl implements WxPayService {

    @Autowired
    private OrderInfoService orderInfoService;

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private WxPayProperties wxPayProperties;

    @Autowired
    private RefundInfoService refundInfoService;

    @Override
    public String createNative(Long orderId) {
        OrderInfo orderInfo = orderInfoService.getById(orderId);

        paymentService.savePaymentInfo(orderInfo, PaymentTypeEnum.WEIXIN.getStatus());
        HttpClient httpClient = new HttpClient("https://api.mch.weixin.qq.com/pay/unifiedorder");
        HashMap<String, String> map = new HashMap<>();
        map.put("appid",wxPayProperties.getAppid());
        map.put("mch_id",wxPayProperties.getPartner());
        map.put("nonce_str",WXPayUtil.generateNonceStr());
        Date reserveDate = orderInfo.getReserveDate();
        String reserveDateString = new DateTime(reserveDate).toString("yyyy/MM/dd");
        String body = reserveDateString + "就诊"+ orderInfo.getDepname();
        map.put("body", body);
        map.put("out_trade_no",orderInfo.getOutTradeNo());
        //订单金额 1分
        map.put("total_fee", "1");
        map.put("spbill_create_ip", "127.0.0.1");
        map.put("notify_url", "http://guli.shop/api/order/weixinPay/weixinNotify");
        map.put("trade_type", "NATIVE");
        try {
            httpClient.setXmlParam(WXPayUtil.generateSignedXml(map, wxPayProperties.getPartnerkey()));
            httpClient.setHttps(true);
            httpClient.post();
            Map<String, String> result = WXPayUtil.xmlToMap(httpClient.getContent());
            System.out.println(result);
            String code_url = result.get("code_url");
            return code_url;

        } catch (Exception e) {
            return "";
        }



    }

    @Override
    public Map<String, String> getPayStatus(Long orderId) {
        HttpClient httpClient = new HttpClient("https://api.mch.weixin.qq.com/pay/orderquery");
        HashMap<String, String> paramMap = new HashMap<>();
        paramMap.put("appid", wxPayProperties.getAppid());
        paramMap.put("mch_id", wxPayProperties.getPartner());
        OrderInfo orderInfo = orderInfoService.getById(orderId);
        paramMap.put("out_trade_no", orderInfo.getOutTradeNo());
        paramMap.put("nonce_str", WXPayUtil.generateNonceStr());
        try {
            httpClient.setXmlParam(WXPayUtil.generateSignedXml(paramMap, wxPayProperties.getPartnerkey()));
            httpClient.setHttps(true);
            httpClient.post();
            String content = httpClient.getContent();
            Map<String, String> stringStringMap = WXPayUtil.xmlToMap(content);
            return stringStringMap;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }

    @Override
    @Transactional
    public void paySuccess(Long orderId,Map<String,String> map) {
        OrderInfo orderInfo = new OrderInfo();
        orderInfo.setId(orderId);
        orderInfo.setOrderStatus(OrderStatusEnum.PAID.getStatus());
        orderInfoService.updateById(orderInfo);
        UpdateWrapper<PaymentInfo> updateWrapper = new UpdateWrapper();
        updateWrapper.eq("order_id",orderId);
        updateWrapper.set("trade_no",map.get("transaction_id"));
        updateWrapper.set("callback_time",new Date());
        updateWrapper.set("payment_status", PaymentStatusEnum.PAID.getStatus());
        updateWrapper.set("callback_content", JSONObject.toJSONString(map));
        paymentService.update(updateWrapper);
    }

    @Override
    public boolean refund(Long orderId) {
        QueryWrapper<PaymentInfo> wrapper = new QueryWrapper<>();
        wrapper.eq("order_id",orderId);
        PaymentInfo paymentInfo = paymentService.getOne(wrapper);
        RefundInfo refundInfo = refundInfoService.saveRefundInfo(paymentInfo);
        if (refundInfo.getRefundStatus() == RefundStatusEnum.REFUND.getStatus().intValue()){
            throw new YyghException(20001,"已退款");
        }
        HttpClient httpClient = new HttpClient("https://api.mch.weixin.qq.com/secapi/pay/refund");
        Map<String,String> paramMap = new HashMap<>(8);
        paramMap.put("appid", wxPayProperties.getAppid());       //公众账号ID
        paramMap.put("mch_id", wxPayProperties.getPartner());   //商户编号
        paramMap.put("nonce_str",WXPayUtil.generateNonceStr());
        paramMap.put("transaction_id",paymentInfo.getTradeNo()); //微信订单号
        paramMap.put("out_trade_no",paymentInfo.getOutTradeNo()); //商户订单编号
        paramMap.put("out_refund_no","tk"+paymentInfo.getOutTradeNo()); //商户退款单号
        //  paramMap.put("total_fee",paymentInfoQuery.getTotalAmount().multiply(new BigDecimal("100")).longValue()+"");
        //  paramMap.put("refund_fee",paymentInfoQuery.getTotalAmount().multiply(new BigDecimal("100")).longValue()+"");
        paramMap.put("total_fee","1");
        paramMap.put("refund_fee","1");
        try {
            String xml = WXPayUtil.generateSignedXml(paramMap, wxPayProperties.getPartnerkey());
            httpClient.setXmlParam(xml);
            httpClient.setHttps(true);
            httpClient.setCert(true);
            httpClient.setCertPassword(wxPayProperties.getPartner());
            httpClient.post();
            String content = httpClient.getContent();
            Map<String, String> resultMap = WXPayUtil.xmlToMap(content);
            if ("SUCCESS".equals(resultMap.get("result_code"))){
                refundInfo.setCallbackTime(new Date());
                refundInfo.setTradeNo(resultMap.get("refund_id"));
                refundInfo.setRefundStatus(RefundStatusEnum.REFUND.getStatus());
                refundInfo.setCallbackContent(JSONObject.toJSONString(resultMap));
                refundInfoService.updateById(refundInfo);
                return true;
            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

    }
}
