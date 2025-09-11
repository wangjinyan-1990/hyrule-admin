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

        // 允许前端来源（开发环境可放开为通配符，生产请精确配置）
        config.addAllowedOriginPattern("*");
        config.setAllowCredentials(true);    //传递cookie
        config.addAllowedMethod("*");    //允许哪些方法访问(*是全部方法)
        config.addAllowedHeader("*");   //允许的头信息
        // 暴露自定义头给前端（使前端能读取到 Authorization / X-Token）
        config.addExposedHeader("Authorization");
        config.addExposedHeader("X-Token");

        //过滤资源
        UrlBasedCorsConfigurationSource configSource = new UrlBasedCorsConfigurationSource();
        configSource.registerCorsConfiguration("/**", config);
        
        return new CorsFilter(configSource);
    }
}