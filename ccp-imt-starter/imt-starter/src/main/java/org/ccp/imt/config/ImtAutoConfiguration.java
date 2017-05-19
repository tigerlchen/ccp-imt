package org.ccp.imt.config;

import org.ccp.imt.ImtEndpoint;
import org.ccp.imt.mvc.ImtController;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * schedulerx auto configuration
 *
 * @author leijuan
 */
@Configuration
@ComponentScan(basePackageClasses = {ImtController.class})
public class ImtAutoConfiguration {

    @Bean
    public ImtEndpoint dtsEndpoint() {
        return new ImtEndpoint();
    }

    @RequestMapping(value = "/", produces = MediaType.TEXT_HTML_VALUE)
    public String browse() {
        return "forward:/index.html";
    }

}
