package com.whozm.yygh.hosp.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.whozm.yygh.hosp.repository.DepartmentRepository;
import com.whozm.yygh.hosp.service.DepartmentService;
import com.whozm.yygh.model.hosp.Department;
import com.whozm.yygh.vo.hosp.DepartmentVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author HZM
 * @date 2023/1/22
 */
@Service
public class DepartmentServiceImpl implements DepartmentService {
    @Autowired
    private DepartmentRepository departmentRepository;

    @Override
    public void saveDepartment(Map<String, Object> map) {
        Department department = JSONObject.parseObject(JSONObject.toJSONString(map), Department.class);
        String hoscode = department.getHoscode();
        String depcode = department.getDepcode();
        Department platformDepartment = departmentRepository.findByHoscodeAndDepcode(hoscode,depcode);

        if (platformDepartment == null){
            department.setCreateTime(new Date());
            department.setUpdateTime(new Date());
            department.setIsDeleted(0);
            departmentRepository.save(department);
        }else {
            department.setId(platformDepartment.getId());
            department.setCreateTime(platformDepartment.getCreateTime());
            department.setUpdateTime(new Date());
            department.setIsDeleted(platformDepartment.getIsDeleted());
            departmentRepository.save(department);
        }

    }

    @Override
    public Page<Department> getDepartmentList(Map<String, Object> map) {
        int page = Integer.parseInt((String)map.get("page"));
        int limit = Integer.parseInt((String)map.get("limit"));
        String hoscode = (String) map.get("hoscode");

        Department department = new Department();
        department.setHoscode(hoscode);
        Example<Department> example = Example.of(department);
        Pageable pageable = PageRequest.of(page-1, limit);
        Page<Department> all = departmentRepository.findAll(example, pageable);
        return all;
    }

    @Override
    public void removeDepartment(Map<String, Object> map) {
        String hoscode = (String) map.get("hoscode");
        String depcode = (String) map.get("depcode");

        Department department = departmentRepository.findByHoscodeAndDepcode(hoscode, depcode);
        if (department!=null){
            departmentRepository.deleteById(department.getId());
        }

    }

    @Override
    public List<DepartmentVo> getDepartmentList(String hoscode) {
        List<Department> departmentList = departmentRepository.findByHoscode(hoscode);
        Map<String, List<Department>> collect = departmentList.stream().collect(Collectors.groupingBy(Department::getBigcode));
        ArrayList<DepartmentVo> bigDepartmentList = new ArrayList<>();
        for (Map.Entry<String, List<Department>> entry : collect.entrySet()) {
            String bigCode = entry.getKey();
            List<Department> value = entry.getValue();
            DepartmentVo bigDepartmentVo = new DepartmentVo();
            ArrayList<DepartmentVo> childDepartmentList = new ArrayList<>();
            for (Department childDepartment : value) {
                DepartmentVo childDepartmentVo = new DepartmentVo();
                String depcode = childDepartment.getDepcode();
                String depname = childDepartment.getDepname();
                childDepartmentVo.setDepcode(depcode);
                childDepartmentVo.setDepname(depname);
                childDepartmentList.add(childDepartmentVo);
            }
            bigDepartmentVo.setDepcode(bigCode);
            bigDepartmentVo.setDepname(value.get(0).getBigname());
            bigDepartmentVo.setChildren(childDepartmentList);
            bigDepartmentList.add(bigDepartmentVo);
        }

        return bigDepartmentList;
    }

    @Override
    public String getDepName(String hoscode, String depcode) {
        Department department = departmentRepository.findByHoscodeAndDepcode(hoscode, depcode);
        if (department!=null){
            return department.getDepname();
        }
        return "";
    }

    @Override
    public Department getDepartment(String hoscode, String depcode) {
        Department department = departmentRepository.findByHoscodeAndDepcode(hoscode,depcode);
        return department;
    }
}
