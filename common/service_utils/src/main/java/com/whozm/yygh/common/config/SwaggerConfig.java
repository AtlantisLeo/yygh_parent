package com.whozm.yygh.common.config;

import com.google.common.base.Predicates;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;


@SpringBootApplication
@EnableSwagger2
public class SwaggerConfig {

    @Bean
    public Docket getAdminDocket(){
        return new Docket(DocumentationType.SWAGGER_2)
                .groupName("admin")
                .apiInfo(getAdminApiInfo())
                .select()
                .paths(Predicates.and(PathSelectors.regex("/admin/.*")))
                .build();
    }

    public ApiInfo getAdminApiInfo(){
        return new ApiInfoBuilder()
                .title("管理员系统接口")
                .description("预约挂号平台-管理员系统")
                .version("1.0")
                .build();
    }

    @Bean
    public Docket getUserDocket(){
        return new Docket(DocumentationType.SWAGGER_2)
                .groupName("user")
                .apiInfo(getUserApiInfo())
                .select()
                .paths(Predicates.and(PathSelectors.regex("/user/.*")))
                .build();
    }

    public ApiInfo getUserApiInfo(){
        return new ApiInfoBuilder()
                .title("用户系统接口")
                .description("预约挂号平台-用户系统")
                .version("1.0")
                .build();
    }

    @Bean
    public Docket getApiDocket(){
        return new Docket(DocumentationType.SWAGGER_2)
                .groupName("Api")
                .apiInfo(getApiInfo())
                .select()
                .paths(Predicates.and(PathSelectors.regex("/api/.*")))
                .build();
    }


    public ApiInfo getApiInfo(){
        return new ApiInfoBuilder()
                .title("第三方对接系统接口")
                .description("预约挂号平台-第三方对接系统")
                .version("1.0")
                .build();
    }
}
