package com.whozm.yygh.order.controller;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.whozm.yygh.common.result.R;
import com.whozm.yygh.common.utils.JwtHelper;
import com.whozm.yygh.enums.OrderStatusEnum;
import com.whozm.yygh.model.order.OrderInfo;
import com.whozm.yygh.order.service.OrderInfoService;
import com.whozm.yygh.vo.order.OrderCountQueryVo;
import com.whozm.yygh.vo.order.OrderQueryVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.security.PermitAll;
import java.text.ParseException;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 订单表 前端控制器
 * </p>
 *
 * @author HZM
 * @since 2023-01-31
 */
@RestController
@RequestMapping("/api/order/orderInfo")
public class OrderInfoController {

    @Autowired
    private OrderInfoService orderInfoService;

    @PostMapping("/{scheduleId}/{patientId}")
    public R saveOrder(@PathVariable String scheduleId, @PathVariable Long patientId){
        Long orderId = orderInfoService.saveOrder(scheduleId,patientId);
        return R.ok().data("orderId",orderId);
    }

    @GetMapping("/{pageNum}/{pageSize}")
    public R getOrderInfoPage(@PathVariable Integer pageNum,
                              @PathVariable Integer pageSize,
                              OrderQueryVo orderQueryVo,
                              @RequestHeader String token){

        Long userId = JwtHelper.getUserId(token);
        orderQueryVo.setUserId(userId);
        Page<OrderInfo> page = orderInfoService.getOrderInfoPage(pageNum,pageSize,orderQueryVo);
        return R.ok().data("page",page);
    }

    @GetMapping("/list")
    public R getOrderList(){
        List<Map<String, Object>> statusList = OrderStatusEnum.getStatusList();
        return R.ok().data("list",statusList);
    }

    @GetMapping("/{orderId}")
    public R getOrderDetail(@PathVariable Long orderId){
        OrderInfo orderInfo = orderInfoService.getOrderDetail(orderId);

        return R.ok().data("orderInfo",orderInfo);
    }

    @GetMapping("/cancel/{orderId}")
    public R cancelOrder(@PathVariable Long orderId){
        orderInfoService.cancelOrder(orderId);
        return R.ok();
    }

    @PostMapping("/statistics")
    public Map<String,Object> statistics(@RequestBody OrderCountQueryVo orderCountQueryVo){
        return orderInfoService.statistics(orderCountQueryVo);
    }
}

