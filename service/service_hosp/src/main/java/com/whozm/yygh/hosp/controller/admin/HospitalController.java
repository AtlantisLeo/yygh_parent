package com.whozm.yygh.hosp.controller.admin;

import com.whozm.yygh.common.result.R;
import com.whozm.yygh.hosp.service.HospitalService;
import com.whozm.yygh.model.hosp.Hospital;
import com.whozm.yygh.vo.hosp.HospitalQueryVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

/**
 * @author HZM
 * @date 2023/1/23
 */
@RestController
@RequestMapping("/admin/hospital")
public class HospitalController {

    @Autowired
    private HospitalService hospitalService;

    @PutMapping("/{id}/{status}")
    public R updateStatus(@PathVariable String id,@PathVariable Integer status){
        hospitalService.updateStatus(id,status);
        return R.ok();
    }

    @GetMapping("/{pageNum}/{pageSize}")
    public R getHosptialPage(@PathVariable Integer pageNum,@PathVariable Integer pageSize,HospitalQueryVo hospitalQueryVo){
       Page<Hospital> page =  hospitalService.getHosptialPage(pageNum,pageSize,hospitalQueryVo);

       return R.ok().data("total",page.getTotalElements()).data("list",page.getContent());
    }

    @GetMapping("/detail/{id}")
    public R detail(@PathVariable String id){
        Hospital hospital = hospitalService.detail(id);
        return R.ok().data("hospital",hospital);
    }
}
