package com.whozm.yygh.order.controller;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.github.wxpay.sdk.WXPayUtil;
import com.whozm.yygh.common.result.R;
import com.whozm.yygh.enums.OrderStatusEnum;
import com.whozm.yygh.enums.PaymentStatusEnum;
import com.whozm.yygh.enums.PaymentTypeEnum;
import com.whozm.yygh.model.order.OrderInfo;
import com.whozm.yygh.model.order.PaymentInfo;
import com.whozm.yygh.order.prop.WxPayProperties;
import com.whozm.yygh.order.service.OrderInfoService;
import com.whozm.yygh.order.service.PaymentService;
import com.whozm.yygh.order.service.WxPayService;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.Map;

/**
 * @author HZM
 * @date 2023/2/1
 */
@RestController
@RequestMapping("/user/order/weixin")
public class WxPayController {

    @Autowired
    private WxPayService wxPayService;


    @GetMapping("/{orderId}")
    public R createNative(@PathVariable Long orderId){
      String url =   wxPayService.createNative(orderId);
      return R.ok().data("url",url);
    }

    @GetMapping("/status/{orderId}")
    public R getPayStatus(@PathVariable Long orderId){
        Map<String,String> map = wxPayService.getPayStatus(orderId);
        if (map == null){
            return R.error().message("查询失败");
        }
        if ("SUCCESS".equals(map.get("trade_state"))){
            //更改订单状态，处理支付结果
            wxPayService.paySuccess(orderId,map);
            return R.ok().message("支付成功");
        }
        return R.ok().message("支付中");
    }
}
