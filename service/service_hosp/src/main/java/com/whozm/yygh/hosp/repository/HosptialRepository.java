package com.whozm.yygh.hosp.repository;

import com.whozm.yygh.model.hosp.Hospital;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

/**
 * @author HZM
 * @date 2023/1/22
 */
public interface HosptialRepository extends MongoRepository<Hospital,String> {
    Hospital findByHoscode(String hoscode);

    List<Hospital> findByHosnameLike(String hosname);
}
