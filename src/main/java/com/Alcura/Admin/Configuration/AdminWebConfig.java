package com.Alcura.Admin.Configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.CacheControl;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class AdminWebConfig implements WebMvcConfigurer {
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/Customer/uploads/**")
                .addResourceLocations("file:src/main/resources/static/Customer/uploads/")
                .setCacheControl(CacheControl.noCache());
    }

}
