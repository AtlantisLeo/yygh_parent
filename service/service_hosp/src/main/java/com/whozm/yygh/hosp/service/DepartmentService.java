package com.whozm.yygh.hosp.service;

import com.whozm.yygh.model.hosp.Department;
import com.whozm.yygh.vo.hosp.DepartmentVo;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Map;

/**
 * @author HZM
 * @date 2023/1/22
 */
public interface DepartmentService {
    void saveDepartment(Map<String, Object> map);

    Page<Department> getDepartmentList(Map<String, Object> map);

    void removeDepartment(Map<String, Object> map);

    List<DepartmentVo> getDepartmentList(String hoscode);

    String getDepName(String hoscode, String depcode);

    Department getDepartment(String hoscode, String depcode);
}
