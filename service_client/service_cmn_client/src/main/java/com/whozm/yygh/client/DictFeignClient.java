package com.whozm.yygh.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * @author HZM
 * @date 2023/1/24
 */
@FeignClient(value = "service-cmn")
public interface DictFeignClient {
    @GetMapping("/admin/cmn/{value}")
    public String getNameByValue(@PathVariable("value") Long value);

    @GetMapping("/admin/cmn/{dictCode}/{value}")
    public String getNameByDictCode(@PathVariable("dictCode") String dictCode, @PathVariable("value") Long value);
}
