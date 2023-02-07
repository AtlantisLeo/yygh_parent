package com.whozm.yygh.hosp.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.whozm.yygh.common.exception.YyghException;
import com.whozm.yygh.hosp.repository.HosptialRepository;
import com.whozm.yygh.hosp.repository.ScheduleRepository;
import com.whozm.yygh.hosp.service.DepartmentService;
import com.whozm.yygh.hosp.service.HospitalService;
import com.whozm.yygh.hosp.service.ScheduleService;
import com.whozm.yygh.model.hosp.BookingRule;
import com.whozm.yygh.model.hosp.Department;
import com.whozm.yygh.model.hosp.Hospital;
import com.whozm.yygh.model.hosp.Schedule;
import com.whozm.yygh.vo.hosp.BookingScheduleRuleVo;
import com.whozm.yygh.vo.hosp.ScheduleOrderVo;
import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;
import org.joda.time.format.DateTimeFormat;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;

import java.awt.print.Pageable;
import java.util.*;
import java.util.stream.Collector;
import java.util.stream.Collectors;

/**
 * @author HZM
 * @date 2023/1/23
 */
@Service
public class ScheduleServiceImpl implements ScheduleService {

    @Autowired
    private ScheduleRepository scheduleRepository;
    @Autowired
    private MongoTemplate mongoTemplate;
    @Autowired
    private HospitalService hospitalService;
    @Autowired
    private DepartmentService departmentService;

    @Override
    public void saveSchedule(Map<String, Object> map) {
        Schedule schedule = JSONObject.parseObject(JSONObject.toJSONString(map),Schedule.class);
        String hoscode = schedule.getHoscode();
        String depcode = schedule.getDepcode();
        String hosScheduleId = schedule.getHosScheduleId();
        Schedule platfromSchedule = scheduleRepository.findByHoscodeAndDepcodeAndHosScheduleId(hoscode,depcode,hosScheduleId);
        if (platfromSchedule == null){
            schedule.setCreateTime(new Date());
            schedule.setUpdateTime(new Date());
            schedule.setIsDeleted(0);
            scheduleRepository.save(schedule);
        }else {
            schedule.setCreateTime(platfromSchedule.getCreateTime());
            schedule.setUpdateTime(new Date());
            schedule.setIsDeleted(platfromSchedule.getIsDeleted());
            schedule.setId(platfromSchedule.getId());
            scheduleRepository.save(schedule);
        }

    }

    @Override
    public Page<Schedule> getScheduleList(Map<String, Object> map) {
        String hoscode = (String) map.get("hoscode");
        Schedule schedule = new Schedule();
        schedule.setHoscode(hoscode);
        Example<Schedule> scheduleExample = Example.of(schedule);
        int page = Integer.parseInt((String) map.get("page"));
        int limit = Integer.parseInt((String) map.get("limit"));
        PageRequest of = PageRequest.of(page-1, limit, Sort.by("createTime").ascending());
        Page<Schedule> all = scheduleRepository.findAll(scheduleExample, of);

        return all;
    }

    @Override
    public void removeSchedule(Map<String, Object> map) {
        String hoscode = (String) map.get("hoscode");
        String hosScheduleId = (String) map.get("hosScheduleId");
        Schedule schedule = scheduleRepository.findByHoscodeAndHosScheduleId(hoscode,hosScheduleId);
        if (schedule != null){
            scheduleRepository.deleteById(schedule.getId());
        }else {
            throw new YyghException(201,"删除失败");
        }

    }

    @Override
    public Map<String, Object> getSchedulePage(Integer pageNum, Integer pageSize, String hoscode, String depcode) {
        Criteria criteria = Criteria.where("hoscode").is(hoscode).and("depcode").is(depcode);

        Aggregation aggregation=Aggregation.newAggregation(
                Aggregation.match(criteria),
                Aggregation.group("workDate")
                .first("workDate").as("workDate")
                .count().as("docCount")
                .sum("reservedNumber").as("reservedNumber")
                .sum("availableNumber").as("availableNumber"),
                Aggregation.sort(Sort.Direction.ASC,"workDate"),
                Aggregation.skip((pageNum-1)*pageSize),
                Aggregation.limit(pageSize)
        );

        AggregationResults<BookingScheduleRuleVo> aggregate = mongoTemplate.aggregate(aggregation, Schedule.class, BookingScheduleRuleVo.class);
        List<BookingScheduleRuleVo> mappedResults = aggregate.getMappedResults();
        for (BookingScheduleRuleVo bookingScheduleRuleVo : mappedResults) {
            Date workDate = bookingScheduleRuleVo.getWorkDate();
            String dayOfWeek = this.getDayOfWeek(new DateTime(workDate));
            bookingScheduleRuleVo.setDayOfWeek(dayOfWeek);
        }

        Aggregation aggregation2=Aggregation.newAggregation(
                Aggregation.match(criteria),
                Aggregation.group("workDate")
        );

        AggregationResults<BookingScheduleRuleVo> aggregate2 = mongoTemplate.aggregate(aggregation2, Schedule.class, BookingScheduleRuleVo.class);
        int size = aggregate2.getMappedResults().size();

        Map<String, Object> map = new HashMap<String,Object>();
        map.put("list",mappedResults);
        map.put("total",size);

        //获取医院名称
        Hospital hospital = hospitalService.getHospitalHoscode(hoscode);
        //其他基础数据
        Map<String, String> baseMap = new HashMap<>();
        baseMap.put("hosname",hospital.getHosname());
        map.put("baseMap",baseMap);

        return map;
    }

    @Override
    public List<Schedule> detail(String hoscode, String depcode, String workdate) {
        Date date = new DateTime(workdate).toDate();
        List<Schedule> scheduleList = scheduleRepository.findByHoscodeAndDepcodeAndWorkDate(hoscode,depcode,date);
        scheduleList.stream().forEach(schedule->{
            this.packageSchedule(schedule);
        });
        return scheduleList;
    }

    @Override
    public Map<String, Object> getSchedulePageByUser(String hoscode, String depcode, Integer pageNum, Integer pageSize) {
        Hospital hospital = hospitalService.getHospitalHoscode(hoscode);
        if (hospital == null){
            throw new YyghException(20001,"该医院不存在");
        }
        BookingRule bookingRule = hospital.getBookingRule();
        IPage<Date> page = this.getListDate(pageNum,pageSize,bookingRule);
        List<Date> records = page.getRecords();
        Criteria criteria = Criteria.where("hoscode").is(hoscode).and("depcode").is(depcode).and("workDate").in(records);
        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.match(criteria),
                Aggregation.group("workDate").first("workDate").as("workDate")
                            .count().as("docCount")
                            .sum("reservedNumber").as("reservedNumber")
                            .sum("availableNumber").as("availableNumber"),
                Aggregation.sort(Sort.Direction.ASC,"workDate")
        );
        AggregationResults<BookingScheduleRuleVo> aggregate = mongoTemplate.aggregate(aggregation, Schedule.class, BookingScheduleRuleVo.class);
        List<BookingScheduleRuleVo> mappedResults = aggregate.getMappedResults();
        Map<Date, BookingScheduleRuleVo> collect = mappedResults.stream().collect(Collectors.toMap(BookingScheduleRuleVo::getWorkDate, BookingScheduleRuleVo -> BookingScheduleRuleVo));
        int size = records.size();
        List<BookingScheduleRuleVo> bookingScheduleRuleVoList = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            Date date = records.get(i);
            BookingScheduleRuleVo bookingScheduleRuleVo = collect.get(date);
            if (bookingScheduleRuleVo == null){
                bookingScheduleRuleVo = new BookingScheduleRuleVo();
                bookingScheduleRuleVo.setWorkDate(date);
                bookingScheduleRuleVo.setDocCount(0);
                bookingScheduleRuleVo.setReservedNumber(0);
                bookingScheduleRuleVo.setAvailableNumber(-1);
            }
                bookingScheduleRuleVo.setWorkDateMd(date);
                bookingScheduleRuleVo.setDayOfWeek(this.getDayOfWeek(new DateTime(date)));
                bookingScheduleRuleVo.setStatus(0);
            if (i == 0 && pageNum == 1){
                DateTime dateTime = this.getDateTime(new Date(), bookingRule.getStopTime());
                if (dateTime.isBeforeNow()){
                    bookingScheduleRuleVo.setStatus(-1);
                }
            }
            if (pageNum==page.getPages() && i == (size-1)){
                bookingScheduleRuleVo.setStatus(1);
            }
            bookingScheduleRuleVoList.add(bookingScheduleRuleVo);
        }
        Map<String,Object> map = new HashMap<>();
        map.put("total",page.getTotal());
        map.put("list",bookingScheduleRuleVoList);
        Map<String, String> baseMap = new HashMap<>();
        //医院名称
        baseMap.put("hosname", hospital.getHosname());
        //科室
        Department department =departmentService.getDepartment(hoscode, depcode);
        //大科室名称
        baseMap.put("bigname", department.getBigname());
        //科室名称
        baseMap.put("depname", department.getDepname());
        //月
        baseMap.put("workDateString", new DateTime().toString("yyyy年MM月"));
        //放号时间
        baseMap.put("releaseTime", bookingRule.getReleaseTime());
        //停号时间
        baseMap.put("stopTime", bookingRule.getStopTime());

        map.put("baseMap", baseMap);

        return map;
    }

    @Override
    public Schedule getScheduleInfo(String scheduleId) {
        Schedule schedule = scheduleRepository.findById(scheduleId).get();
        this.packageSchedule(schedule);
        return schedule;
    }

    @Override
    public ScheduleOrderVo getScheduleById(String scheduleId) {
        Schedule schedule = scheduleRepository.findById(scheduleId).get();
        ScheduleOrderVo scheduleOrderVo = new ScheduleOrderVo();
        BeanUtils.copyProperties(schedule,scheduleOrderVo);
        Hospital hospital = hospitalService.getHospitalHoscode(scheduleOrderVo.getHoscode());
        scheduleOrderVo.setHosname(hospital.getHosname());
        Department department = departmentService.getDepartment(scheduleOrderVo.getHoscode(), scheduleOrderVo.getDepcode());
        scheduleOrderVo.setDepname(department.getDepname());
        scheduleOrderVo.setReserveDate(schedule.getWorkDate());
        scheduleOrderVo.setReserveTime(schedule.getWorkTime());
        DateTime dateTime = this.getDateTime(new DateTime(schedule.getWorkDate()).plusDays(hospital.getBookingRule().getQuitDay()).toDate(), hospital.getBookingRule().getQuitTime());
        scheduleOrderVo.setQuitTime(dateTime.toDate());
        Date date = this.getDateTime(schedule.getWorkDate(), hospital.getBookingRule().getStopTime()).toDate();
        scheduleOrderVo.setStopTime(date);
        return scheduleOrderVo;
    }

    @Override
    public boolean updateavailableNumber(String scheduleId, Integer availableNumber) {
        Schedule schedule = scheduleRepository.findById(scheduleId).get();
        schedule.setAvailableNumber(availableNumber);
        schedule.setUpdateTime(new Date());
        scheduleRepository.save(schedule);
        return true;
    }

    @Override
    public boolean cancelSchedule(String scheduleId) {
        Schedule schedule = scheduleRepository.findByHosScheduleId(scheduleId);
        schedule.setAvailableNumber(schedule.getAvailableNumber()+1);
        scheduleRepository.save(schedule);
        return true;
    }

    private IPage getListDate(Integer pageNum, Integer pageSize, BookingRule bookingRule) {
        Integer cycle = bookingRule.getCycle();
        String releaseTime = bookingRule.getReleaseTime();
        DateTime dateTime = this.getDateTime(new Date(), releaseTime);
        if (dateTime.isBeforeNow()){
            cycle = cycle+1;
        }
        List<Date> list = new ArrayList<>();
        for (Integer i = 0; i < cycle; i++) {
            list.add(new DateTime(new DateTime().plusDays(i).toString("yyyy-MM-dd")).toDate());
        }

        int start = (pageNum-1)*pageSize;
        int end = start+pageSize;
        if (end>list.size()){
            end=list.size();
        }
        List<Date> currentListPage = new ArrayList<>();
        for (int j=start;j<end;j++){
            Date date = list.get(j);
            currentListPage.add(date);
        }

        com.baomidou.mybatisplus.extension.plugins.pagination.Page<Date> page = new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>(pageNum,pageSize,list.size());
        page.setRecords(currentListPage);
        return page;
    }

    /**
     * 将Date日期（yyyy-MM-dd HH:mm）转换为DateTime
     */
    private DateTime getDateTime(Date date, String timeString) {
        String dateTimeString = new DateTime(date).toString("yyyy-MM-dd") + " "+ timeString;
        DateTime dateTime = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm").parseDateTime(dateTimeString);
        return dateTime;
    }

    private void packageSchedule(Schedule schedule) {
        schedule.getParam().put("hosname",hospitalService.getHospitalHoscode(schedule.getHoscode()).getHosname());
        //设置科室名称
        schedule.getParam().put("depname",
                departmentService.getDepName(schedule.getHoscode(),schedule.getDepcode()));
        //设置日期对应星期
        schedule.getParam().put("dayOfWeek",this.getDayOfWeek(new DateTime(schedule.getWorkDate())));
    }

    private String getDayOfWeek(DateTime dateTime) {
        String dayOfWeek = "";
        switch (dateTime.getDayOfWeek()) {
            case DateTimeConstants.SUNDAY:
                dayOfWeek = "周日";
                break;
            case DateTimeConstants.MONDAY:
                dayOfWeek = "周一";
                break;
            case DateTimeConstants.TUESDAY:
                dayOfWeek = "周二";
                break;
            case DateTimeConstants.WEDNESDAY:
                dayOfWeek = "周三";
                break;
            case DateTimeConstants.THURSDAY:
                dayOfWeek = "周四";
                break;
            case DateTimeConstants.FRIDAY:
                dayOfWeek = "周五";
                break;
            case DateTimeConstants.SATURDAY:
                dayOfWeek = "周六";
            default:
                break;
        }
        return dayOfWeek;
    }
}
