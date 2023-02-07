package com.whozm.yygh.hosp.controller.admin;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.whozm.yygh.common.result.R;
import com.whozm.yygh.hosp.service.HospitalSetService;
import com.whozm.yygh.common.utils.MD5;
import com.whozm.yygh.model.hosp.HospitalSet;
import com.whozm.yygh.vo.hosp.HospitalSetQueryVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Random;

/**
 * <p>
 * 医院设置表 前端控制器
 * </p>
 *
 * @author HZM
 * @since 2023-01-13
 */
@RestController
@RequestMapping("/admin/hosp/hospitalSet")
@Api(tags = "医院设置信息")
public class HospitalSetController {


    @Autowired
   private HospitalSetService hospitalSetService;

    @GetMapping("/findAll")
    @ApiOperation(value = "查询所有的医院设置信息")
    public R fingAll(){
        List<HospitalSet> list = hospitalSetService.list();
        return R.ok().data("items",list);
    }

    @DeleteMapping("/deleteById/{id}")
    @ApiOperation(value = "根据ID删除对应的医院设置信息")
    public R deleteById(@ApiParam(name = "id",value = "医院设置id",required = true) @PathVariable Integer id){
        hospitalSetService.removeById(id);
        return R.ok();
    }

    @PostMapping("/page/{pageNum}/{size}")
    @ApiOperation(value = "带查询条件的分页")
    public R getPageInfo(@ApiParam(name = "pageNum",value = "当前页")@PathVariable Integer pageNum,
                         @ApiParam(name = "size",value = "每页大小")@PathVariable Integer size,
                         @ApiParam(name = "hospitalSetQueryVo",value = "查询条件")@RequestBody HospitalSetQueryVo hospitalSetQueryVo){

        Page<HospitalSet> page = new Page<>(pageNum,size);
        QueryWrapper<HospitalSet> wrapper = new QueryWrapper<HospitalSet>();
        if (!StringUtils.isEmpty(hospitalSetQueryVo.getHosname())){
            wrapper.like("hosname",hospitalSetQueryVo.getHosname());
        }
        if (!StringUtils.isEmpty(hospitalSetQueryVo.getHoscode())){
            wrapper.like("hoscode",hospitalSetQueryVo.getHoscode());
        }
        hospitalSetService.page(page, wrapper);
        return R.ok().data("total",page.getTotal()).data("rows",page.getRecords());
    }

    @PostMapping("/save")
    @ApiOperation(value = "新增医院信息")
    public R save(@ApiParam(name = "hospitalSet",value = "医院信息") @RequestBody HospitalSet hospitalSet){
        hospitalSet.setStatus(1);
        //设置第三方医院对接密钥
        Random random = new Random();
        hospitalSet.setSignKey(MD5.encrypt(System.currentTimeMillis()+""+random.nextInt(1000)));
        hospitalSetService.save(hospitalSet);
        return R.ok();
    }

    @GetMapping("/detail/{id}")
    @ApiOperation(value = "根据id查询医院信息")
    public R getById(@ApiParam(name = "id",value = "医院信息id")@PathVariable Integer id){
        return R.ok().data("item",hospitalSetService.getById(id));
    }

    @PutMapping("/update")
    @ApiOperation(value = "根据id修改医院信息")
    public R update(@ApiParam(name = "hospitalSet",value = "修改的医院信息") @RequestBody HospitalSet hospitalSet){
        hospitalSetService.updateById(hospitalSet);
        return R.ok();
    }

    @DeleteMapping("/delete")
    @ApiOperation(value = "批量删除医院信息")
    public R batchDelete(@ApiParam(name = "ids",value = "批量删除医院信息id") @RequestBody List<Long> ids){
        hospitalSetService.removeByIds(ids);
        return R.ok();
    }

    @PutMapping("/status/{id}/{status}")
    @ApiOperation(value = "根据id修改医院状态")
    public R updateStatus(@ApiParam(name = "id",value = "修改的医院id") @PathVariable Long id,
                          @ApiParam(name = "status",value = "修改的医院状态") @PathVariable Integer status){
        HospitalSet hospitalSet = hospitalSetService.getById(id);
        hospitalSet.setStatus(status);
        hospitalSetService.updateById(hospitalSet);
        return R.ok();
    }
}

