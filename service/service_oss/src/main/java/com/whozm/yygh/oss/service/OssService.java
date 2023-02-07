package com.whozm.yygh.oss.service;

import org.springframework.web.multipart.MultipartFile;

/**
 * @author HZM
 * @date 2023/1/28
 */
public interface OssService {
    String upload(MultipartFile file);

}
