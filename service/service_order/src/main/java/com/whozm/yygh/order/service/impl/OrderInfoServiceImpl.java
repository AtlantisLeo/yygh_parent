package com.whozm.yygh.order.service.impl;


import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.whozm.yygh.common.exception.YyghException;
import com.whozm.yygh.enums.OrderStatusEnum;
import com.whozm.yygh.enums.PaymentStatusEnum;
import com.whozm.yygh.hosp.client.ScheduleFeignClient;
import com.whozm.yygh.model.order.OrderInfo;
import com.whozm.yygh.model.order.PaymentInfo;
import com.whozm.yygh.model.user.Patient;

import com.whozm.yygh.mq.MqConst;
import com.whozm.yygh.mq.RabbitService;
import com.whozm.yygh.order.mapper.OrderInfoMapper;
import com.whozm.yygh.order.service.OrderInfoService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.whozm.yygh.order.service.PaymentService;
import com.whozm.yygh.order.service.WxPayService;
import com.whozm.yygh.order.utils.HttpClient;
import com.whozm.yygh.order.utils.HttpRequestHelper;
import com.whozm.yygh.user.client.PatientFeignClient;
import com.whozm.yygh.vo.hosp.ScheduleOrderVo;
import com.whozm.yygh.vo.msm.MsmVo;
import com.whozm.yygh.vo.order.OrderCountQueryVo;
import com.whozm.yygh.vo.order.OrderCountVo;
import com.whozm.yygh.vo.order.OrderMqVo;
import com.whozm.yygh.vo.order.OrderQueryVo;
import org.joda.time.DateTime;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * <p>
 * 订单表 服务实现类
 * </p>
 *
 * @author HZM
 * @since 2023-01-31
 */
@Service
public class OrderInfoServiceImpl extends ServiceImpl<OrderInfoMapper, OrderInfo> implements OrderInfoService {

    @Autowired
    private ScheduleFeignClient scheduleFeignClient;
    @Autowired
    private PatientFeignClient patientFeignClient;
    @Autowired
    private RabbitService rabbitService;
    @Autowired
    private WxPayService wxPayService;
    @Autowired
    private PaymentService paymentService;

    @Override
    public Long saveOrder(String scheduleId, Long patientId) {
        ScheduleOrderVo scheduleOrderVo = scheduleFeignClient.getScheduleById(scheduleId);
        Patient patient = patientFeignClient.getPatientById(patientId);
        DateTime stopTime = new DateTime(scheduleOrderVo.getStopTime());
        if (stopTime.isBeforeNow()){
            throw new YyghException(20001,"挂号截至时间已过");
        }

        HashMap<String, Object> paramMap = new HashMap<>();
        paramMap.put("hoscode",scheduleOrderVo.getHoscode());
        paramMap.put("depcode",scheduleOrderVo.getDepcode());
        paramMap.put("hosScheduleId",scheduleOrderVo.getHosScheduleId());
        paramMap.put("reserveDate",scheduleOrderVo.getReserveDate());
        paramMap.put("reserveTime",scheduleOrderVo.getReserveTime());
        paramMap.put("amount",scheduleOrderVo.getAmount());

        JSONObject response = HttpRequestHelper.sendRequest(paramMap, "http://localhost:9998/order/submitOrder");
        if (response != null && response.getInteger("code") == 200){
            JSONObject data = response.getJSONObject("data");
            OrderInfo orderInfo = new OrderInfo();
            orderInfo.setOutTradeNo(System.currentTimeMillis()+""+new Random().nextInt(1000));
            BeanUtils.copyProperties(scheduleOrderVo,orderInfo);
            orderInfo.setUserId(patient.getUserId());
            orderInfo.setScheduleId(scheduleOrderVo.getHosScheduleId());
            orderInfo.setPatientId(patient.getId());
            orderInfo.setPatientName(patient.getName());
            orderInfo.setPatientPhone(patient.getPhone());
            orderInfo.setHosRecordId(data.getString("hosRecordId"));
            orderInfo.setNumber(data.getInteger("number"));
            SimpleDateFormat sdf2 = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyyHH:mm", Locale.US);
            Date data1 = null;
            try {
                data1 = sdf2.parse(data.getString("fetchTime"));
                String formatDate = new SimpleDateFormat("yyyy-MM-dd HH:mm").format(data1);
                orderInfo.setFetchTime(formatDate);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            orderInfo.setFetchAddress(data.getString("fetchAddress"));
            orderInfo.setOrderStatus(OrderStatusEnum.UNPAID.getStatus());
            baseMapper.insert(orderInfo);
            //通过mq更新可预约数
            OrderMqVo orderMqVo = new OrderMqVo();
            orderMqVo.setScheduleId(scheduleId);
            int availableNumber = data.getIntValue("availableNumber");
            orderMqVo.setAvailableNumber(availableNumber);
            MsmVo msmVo = new MsmVo();
            msmVo.setPhone(patient.getPhone());
            msmVo.setTemplateCode("已成功预约${time}${name}医生的号，请及时支付");
            HashMap<String, Object> map = new HashMap<>();
            map.put("time",scheduleOrderVo.getReserveDate()+""+scheduleOrderVo.getReserveTime());
            map.put("name","");
            msmVo.setParam(map);
            orderMqVo.setMsmVo(msmVo);
            rabbitService.sendMessage(MqConst.EXCHANGE_DIRECT_ORDER,MqConst.ROUTING_ORDER,orderMqVo);

            return orderInfo.getId();
        }else {
            throw new YyghException(20001,"挂号失败");
        }
    }

    @Override
    public Page<OrderInfo> getOrderInfoPage(Integer pageNum, Integer pageSize, OrderQueryVo orderQueryVo) {

        Page<OrderInfo> page = new Page<>(pageNum,pageSize);
        QueryWrapper<OrderInfo> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id",orderQueryVo.getUserId());
        String outTradeNo = orderQueryVo.getOutTradeNo();
        Long patientId = orderQueryVo.getPatientId();
        String keyword = orderQueryVo.getKeyword();
        String orderStatus = orderQueryVo.getOrderStatus();
        String reserveDate = orderQueryVo.getReserveDate();
        String createTimeBegin = orderQueryVo.getCreateTimeBegin();
        String createTimeEnd = orderQueryVo.getCreateTimeEnd();
        if (!StringUtils.isEmpty(outTradeNo)){
            wrapper.eq("out_trade_no",outTradeNo);
        }
        if (!StringUtils.isEmpty(keyword)){
            wrapper.like("hosname",keyword);
        }
        if (!StringUtils.isEmpty(patientId)){
            wrapper.eq("patient_id",patientId);
        }
        if (!StringUtils.isEmpty(orderStatus)){
            wrapper.eq("order_status",orderStatus);
        }
        if (!StringUtils.isEmpty(reserveDate)){
            wrapper.ge("reserve_date",reserveDate);
        }
        if (!StringUtils.isEmpty(createTimeBegin)){
            wrapper.ge("create_time",createTimeBegin);
        }
        if (!StringUtils.isEmpty(createTimeEnd)){
            wrapper.le("create_time",createTimeEnd);
        }
        Page<OrderInfo> orderInfoPage = baseMapper.selectPage(page, wrapper);
        orderInfoPage.getRecords().parallelStream().forEach(item->{
            this.packageOrderInfo(item);
        });
        return orderInfoPage;
    }

    @Override
    public OrderInfo getOrderDetail(Long orderId) {

        OrderInfo orderInfo = baseMapper.selectById(orderId);
        this.packageOrderInfo(orderInfo);
        return orderInfo;
    }

    @Override
    public void cancelOrder(Long orderId) {
        OrderInfo orderInfo = baseMapper.selectById(orderId);
        DateTime quitTime = new DateTime(orderInfo.getQuitTime());
        if (quitTime.isBeforeNow()){
            throw new YyghException(20001,"超过了截至时间");
        }
        HashMap<String, Object> hospitalMap = new HashMap<>();
        hospitalMap.put("hoscode",orderInfo.getHoscode());
        hospitalMap.put("hosRecordId",orderInfo.getHosRecordId());
        JSONObject jsonObject = HttpRequestHelper.sendRequest(hospitalMap, "http://localhost:9998/order/updateCancelStatus");
        if (jsonObject == null || jsonObject.getIntValue("code") != 200){
            throw new YyghException(20001,"医院取消预约失败");
        }
        //判断用户是否支付
        if (orderInfo.getOrderStatus() == OrderStatusEnum.PAID.getStatus()){
            boolean flag = wxPayService.refund(orderId);
            if (!flag){
                throw  new YyghException(20001,"退款失败");
            }
        }
        orderInfo.setOrderStatus(OrderStatusEnum.CANCLE.getStatus());
        baseMapper.updateById(orderInfo);
        UpdateWrapper<PaymentInfo> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("order_id",orderInfo.getId());
        updateWrapper.set("payment_status", PaymentStatusEnum.REFUND.getStatus());
        paymentService.update(updateWrapper);
        OrderMqVo orderMqVo = new OrderMqVo();
        orderMqVo.setScheduleId(orderInfo.getScheduleId());
        MsmVo msmVo = new MsmVo();
        msmVo.setPhone(orderInfo.getPatientPhone());
        orderMqVo.setMsmVo(msmVo);
        rabbitService.sendMessage(MqConst.EXCHANGE_DIRECT_ORDER,MqConst.ROUTING_ORDER,orderMqVo);
    }

    @Override
    public void printRemind() {
        QueryWrapper<OrderInfo> wrapper = new QueryWrapper<>();
        wrapper.eq("reserve_date",new DateTime().toString("yyyy-MM-dd"));
        wrapper.ne("order_status",OrderStatusEnum.CANCLE.getStatus());

        List<OrderInfo> orderInfos = baseMapper.selectList(wrapper);
        for (OrderInfo orderInfo : orderInfos) {
            MsmVo msmVo = new MsmVo();
            msmVo.setPhone(orderInfo.getPatientPhone());
            String reserveDate = new DateTime(orderInfo.getReserveDate()).toString("yyyy-MM-dd") + (orderInfo.getReserveTime()==0 ? "上午": "下午");
            Map<String,Object> param = new HashMap<String,Object>(){{
                put("title", orderInfo.getHosname()+"|"+orderInfo.getDepname()+"|"+orderInfo.getTitle());
                put("reserveDate", reserveDate);
                put("name", orderInfo.getPatientName());
            }};
            msmVo.setParam(param);
            rabbitService.sendMessage(MqConst.EXCHANGE_DIRECT_ORDER,MqConst.ROUTING_ORDER,msmVo);
        }
    }

    @Override
    public Map<String, Object> statistics(OrderCountQueryVo orderCountQueryVo) {
        List<OrderCountVo> countVoList= baseMapper.statistics(orderCountQueryVo);
        List<String> dateList = new ArrayList<>();
        List<Integer> countList = new ArrayList<>();
        for (OrderCountVo orderCountVo : countVoList) {
            String reserveDate = orderCountVo.getReserveDate();
            Integer count = orderCountVo.getCount();
            dateList.add(reserveDate);
            countList.add(count);
        }
        Map<String, Object> map =new HashMap<>();
        map.put("dateList",dateList);
        map.put("countList",countList);
        return map;
    }

    private void packageOrderInfo(OrderInfo item) {
        item.getParam().put("orderStatusString",OrderStatusEnum.getStatusNameByStatus(item.getOrderStatus()));
    }
}
