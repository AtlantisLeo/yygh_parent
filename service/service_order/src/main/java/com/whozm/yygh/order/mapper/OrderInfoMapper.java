package com.whozm.yygh.order.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.whozm.yygh.model.order.OrderInfo;
import com.whozm.yygh.vo.order.OrderCountQueryVo;
import com.whozm.yygh.vo.order.OrderCountVo;

import java.util.List;

/**
 * <p>
 * 订单表 Mapper 接口
 * </p>
 *
 * @author HZM
 * @since 2023-01-31
 */
public interface OrderInfoMapper extends BaseMapper<OrderInfo> {

    List<OrderCountVo> statistics(OrderCountQueryVo orderCountQueryVo);

}
