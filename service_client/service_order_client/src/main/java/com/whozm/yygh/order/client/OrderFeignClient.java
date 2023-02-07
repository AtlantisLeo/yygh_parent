package com.whozm.yygh.order.client;

import com.whozm.yygh.vo.order.OrderCountQueryVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;

/**
 * @author HZM
 * @date 2023/2/3
 */
@FeignClient(value = "service-order")
public interface OrderFeignClient {
    @PostMapping("/api/order/orderInfo/statistics")
    public Map<String,Object> statistics(@RequestBody OrderCountQueryVo orderCountQueryVo);
}
