package com.whozm.yygh.order.prop;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

/**
 * @author HZM
 * @date 2023/2/1
 */
@ConfigurationProperties(prefix = "wxpay")
@Component
@PropertySource(value = "classpath:wxpay.properties")
@Data
public class WxPayProperties {
    private String appid;
    private String partner;
    private String partnerkey;

}
