package com.whozm.yygh.hosp.controller.user;

import com.whozm.yygh.common.result.R;
import com.whozm.yygh.hosp.service.HospitalService;
import com.whozm.yygh.model.hosp.Hospital;
import com.whozm.yygh.vo.hosp.HospitalQueryVo;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author HZM
 * @date 2023/1/25
 */
@Api(tags = "医院显示接口")
@RestController
@RequestMapping("/user/hosp/hospital")
public class UserHospitalController {

    @Autowired
    private HospitalService hospitalService;

    @GetMapping("/list")
    public R getHospitalPage(HospitalQueryVo hospitalQueryVo){
        Page<Hospital> hosptialPage = hospitalService.getHosptialPage(1, 1001, hospitalQueryVo);
        return R.ok().data("list",hosptialPage.getContent());
    }

    @GetMapping("/{hosname}")
    public R findByName(@PathVariable String hosname){
        List<Hospital> hospitalList = hospitalService.findByName(hosname);
        return R.ok().data("list",hospitalList);
    }
    @GetMapping("/detail/{hoscode}")
    public R getHospitalDetail(@PathVariable String hoscode){
        Hospital hospital = hospitalService.getHospitalDetail(hoscode);
        return R.ok().data("hospital",hospital);
    }
}
