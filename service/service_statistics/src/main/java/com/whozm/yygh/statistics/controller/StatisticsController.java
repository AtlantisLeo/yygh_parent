package com.whozm.yygh.statistics.controller;

import com.whozm.yygh.common.result.R;
import com.whozm.yygh.statistics.service.StatisticsService;
import com.whozm.yygh.vo.order.OrderCountQueryVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@Api(tags = "统计管理接口")
@RestController
@RequestMapping("/admin/sta")
public class StatisticsController {

    @Autowired
    private StatisticsService statisticsService;

    @ApiOperation(value = "获取订单统计数据")
    @GetMapping("/getCountMap")
    public R getCountMap(@ApiParam(name = "orderCountQueryVo", value = "查询对象", required = false) OrderCountQueryVo orderCountQueryVo) {
        Map<String, Object> map = statisticsService.getCountMap(orderCountQueryVo);
        return R.ok().data(map);
    }
}