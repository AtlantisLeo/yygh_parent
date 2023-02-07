package com.whozm.yygh.oss.prop;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

/**
 * @author HZM
 * @date 2023/1/28
 */
@ConfigurationProperties(prefix = "oss.file")
@Component
@Data
@PropertySource(value = {"classpath:oss.properties"})
public class Ossporperties {
    private String endpoint;
    private String keyid;
    private String keysecret;
    private String bucketname;
}
