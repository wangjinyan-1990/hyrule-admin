package com.king.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
public class MyCorsConfig {

    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration config = new CorsConfiguration();

//      config.addAllowedOrigin("*");    //允许所有人访问，不安全不推荐
        config.addAllowedOrigin("http://localhost:7777/");   //这里写允许访问的前端服务器
        config.setAllowCredentials(true);    //传递cookie
        config.addAllowedMethod("*");    //允许哪些方法访问(*是全部方法)
        config.addAllowedHeader("*");   //允许的头信息

        //过滤资源
        UrlBasedCorsConfigurationSource configSource = new UrlBasedCorsConfigurationSource();
        configSource.registerCorsConfiguration("/**", config);
        
        return new CorsFilter(configSource);
    }
}