package com.whozm.yygh.user.prop;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author HZM
 * @date 2023/1/27
 */
@Data
@ConfigurationProperties(prefix = "weixin")
@Component
public class WeiXinProperties {
    private String appid;
    private String appsecret;
    private String redirecturl;
}
