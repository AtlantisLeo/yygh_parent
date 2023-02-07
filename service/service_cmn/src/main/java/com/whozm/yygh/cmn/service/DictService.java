package com.whozm.yygh.cmn.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.whozm.yygh.model.cmn.Dict;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;

/**
 * <p>
 * 组织架构表 服务类
 * </p>
 *
 * @author HZM
 * @since 2023-01-18
 */
public interface DictService extends IService<Dict> {

    List<Dict> getChildListByPid(Long pid);

    void download(HttpServletResponse response) throws IOException;

    void upload(MultipartFile file) throws IOException;

    String getNameByValue(Long value);

    String getNameByDictCode(String dictCode, Long value);
}
