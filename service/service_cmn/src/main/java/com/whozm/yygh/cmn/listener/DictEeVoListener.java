package com.whozm.yygh.cmn.listener;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.whozm.yygh.cmn.mapper.DictMapper;
import com.whozm.yygh.model.cmn.Dict;
import com.whozm.yygh.vo.cmn.DictEeVo;
import org.springframework.beans.BeanUtils;

/**
 * @author HZM
 * @date 2023/1/19
 */
public class DictEeVoListener extends AnalysisEventListener<DictEeVo> {

    private DictMapper dictMapper;

    public DictEeVoListener(DictMapper mapper){
        this.dictMapper = mapper;
    }

    @Override
    public void invoke(DictEeVo dictEeVo, AnalysisContext analysisContext) {
        Dict dict = new Dict();
        BeanUtils.copyProperties(dictEeVo,dict);
        QueryWrapper<Dict> wrapper = new QueryWrapper<>();
        wrapper.eq("id",dictEeVo.getId());
        Integer count = dictMapper.selectCount(wrapper);
        if (count>0){
            dictMapper.updateById(dict);
        }else {
            dictMapper.insert(dict);
        }
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext analysisContext) {

    }
}
