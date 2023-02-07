package com.whozm.yygh.cmn.controller;


import com.alibaba.excel.EasyExcel;
import com.whozm.yygh.cmn.service.DictService;
import com.whozm.yygh.common.result.R;
import com.whozm.yygh.model.cmn.Dict;
import com.whozm.yygh.vo.cmn.DictEeVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * <p>
 * 组织架构表 前端控制器
 * </p>
 *
 * @author HZM
 * @since 2023-01-18
 */
@RestController
@RequestMapping("/admin/cmn")
public class DictController {

    @Autowired
    private DictService dictService;

    @PostMapping("/upload")
    public R upload(MultipartFile file) throws IOException {
        dictService.upload(file);

        return R.ok();
    }
    @GetMapping("/download")
    public void download(HttpServletResponse response) throws IOException {
        dictService.download(response);
    }
    @GetMapping("/childList/{pid}")
    public R getChildListByPid(@PathVariable Long pid){
        List<Dict> list = dictService.getChildListByPid(pid);
        return R.ok().data("items",list);
    }

    @GetMapping("/{value}")
    public String getNameByValue(@PathVariable("value") Long value){
        return dictService.getNameByValue(value);
    }

    @GetMapping("/{dictCode}/{value}")
    public String getNameByDictCode(@PathVariable("dictCode") String dictCode,
                                            @PathVariable("value") Long value){
        return dictService.getNameByDictCode(dictCode,value);
    }
}

