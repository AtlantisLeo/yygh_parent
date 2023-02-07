package com.whozm.yygh.hosp.repository;

import com.whozm.yygh.model.hosp.Department;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

/**
 * @author HZM
 * @date 2023/1/22
 */
public interface DepartmentRepository extends MongoRepository<Department,String> {
    Department findByHoscodeAndDepcode(String hoscode, String depcode);

    List<Department> findByHoscode(String hoscode);

}
