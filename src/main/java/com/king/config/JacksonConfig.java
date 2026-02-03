package com.king.config;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;

/**
 * Jackson配置类
 * 统一配置LocalDateTime的序列化和反序列化格式
 * 支持多种日期格式：ISO格式（如 2026-02-03T06:50:50.400Z）和自定义格式（yyyy-MM-dd HH:mm:ss）
 */
@Configuration
public class JacksonConfig {

    /**
     * 日期时间格式
     */
    private static final String DATE_TIME_PATTERN = "yyyy-MM-dd HH:mm:ss";

    /**
     * 配置ObjectMapper，统一格式化LocalDateTime
     * 支持ISO格式和自定义格式的反序列化
     */
    @Bean
    @Primary
    public ObjectMapper objectMapper(Jackson2ObjectMapperBuilder builder) {
        JavaTimeModule javaTimeModule = new JavaTimeModule();
        
        // 配置LocalDateTime的序列化格式（输出使用自定义格式）
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(DATE_TIME_PATTERN);
        javaTimeModule.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer(dateTimeFormatter));
        
        // 配置LocalDateTime的反序列化，支持多种格式
        // 1. ISO格式：2026-02-03T06:50:50.400Z 或 2026-02-03T06:50:50.400
        // 2. 自定义格式：yyyy-MM-dd HH:mm:ss
        DateTimeFormatterBuilder formatterBuilder = new DateTimeFormatterBuilder()
                .appendOptional(DateTimeFormatter.ISO_DATE_TIME)  // ISO格式，支持带时区的格式
                .appendOptional(DateTimeFormatter.ISO_LOCAL_DATE_TIME)  // ISO本地格式
                .appendOptional(dateTimeFormatter);  // 自定义格式
        
        LocalDateTimeDeserializer deserializer = new LocalDateTimeDeserializer(formatterBuilder.toFormatter());
        javaTimeModule.addDeserializer(LocalDateTime.class, deserializer);
        
        return builder
                .modules(javaTimeModule)
                .featuresToDisable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                // 忽略未知属性，避免JSON字段不匹配导致400错误
                .featuresToEnable(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_AS_NULL)
                .featuresToDisable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .build();
    }
}
