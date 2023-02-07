package com.whozm.yygh.cmn.service.impl;


import com.alibaba.excel.EasyExcel;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.whozm.yygh.cmn.listener.DictEeVoListener;
import com.whozm.yygh.cmn.mapper.DictMapper;
import com.whozm.yygh.cmn.service.DictService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.whozm.yygh.model.cmn.Dict;
import com.whozm.yygh.vo.cmn.DictEeVo;
import org.springframework.beans.BeanUtils;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 组织架构表 服务实现类
 * </p>
 *
 * @author HZM
 * @since 2023-01-18
 */
@Service
public class DictServiceImpl extends ServiceImpl<DictMapper, Dict> implements DictService {

    @Override
    @Cacheable(value = "dict", key = "'selectIndexList'+#pid")
    public List<Dict> getChildListByPid(Long pid) {
        QueryWrapper<Dict> wrapper = new QueryWrapper<>();
        wrapper.eq("parent_id",pid);
        List<Dict> dicts = baseMapper.selectList(wrapper);
        for (Dict dict : dicts) {
            dict.setHasChildren(isHasChildren(dict.getId()));
        }
        return dicts;
    }

    @Override
    public void download(HttpServletResponse response) throws IOException {
        List<Dict> list = baseMapper.selectList(null);
        List<DictEeVo> dictEeVoList = new ArrayList<>(list.size());
        for (Dict dict : list) {
            DictEeVo dictEeVo = new DictEeVo();
            BeanUtils.copyProperties(dict,dictEeVo);
            dictEeVoList.add(dictEeVo);
        }
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("utf-8");
        // 这里URLEncoder.encode可以防止中文乱码 当然和easyexcel没有关系
        String fileName = URLEncoder.encode("数据字典", "UTF-8").replaceAll("\\+", "%20");
        response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + fileName + ".xlsx");

        EasyExcel.write(response.getOutputStream(),DictEeVo.class).sheet("字典列表").doWrite(dictEeVoList);
    }

    @Override
    @CacheEvict(value = "dict",allEntries = true)
    public void upload(MultipartFile file) throws IOException {

        EasyExcel.read(file.getInputStream(), DictEeVo.class,new DictEeVoListener(baseMapper)).sheet(0).doRead();
    }

    @Override
    public String getNameByValue(Long value) {
        QueryWrapper<Dict> wrapper = new QueryWrapper<>();
        wrapper.eq("value",value);
        Dict dict = baseMapper.selectOne(wrapper);
        if (dict!=null){
            return dict.getName();
        }
        return null;
    }

    @Override
    public String getNameByDictCode(String dictCode, Long value) {

        QueryWrapper<Dict> wrapper = new QueryWrapper<Dict>();
        wrapper.eq("dict_code",dictCode);
        Dict dict1 = baseMapper.selectOne(wrapper);

            QueryWrapper<Dict> queryWrapper = new QueryWrapper<Dict>();
            queryWrapper.eq("parent_id",dict1.getId());
            queryWrapper.eq("value",value);
            Dict dict2 = baseMapper.selectOne(queryWrapper);
            return dict2.getName();

    }

    private boolean isHasChildren(Long pid) {
        QueryWrapper<Dict> wrapper = new QueryWrapper<>();
        wrapper.eq("parent_id",pid);
        Integer count = baseMapper.selectCount(wrapper);
        return count>0;
    }
}
