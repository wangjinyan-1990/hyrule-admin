package com.king.common;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Result<T> {
    private Integer code; // 响应码 20000-成功 20001-失败
    private String message; // 响应信息
    private T data; // 返回的数据

    public static <T> Result<T> success() {
        return new Result<>(20000, "success", null);
    }

    public static <T> Result<T> success(T data) {
        return new Result<>(20000, "success", data);
    }

    public static <T> Result<T> success(T data, String message) {
        return new Result<>(20000, message, data);
    }

    public static <T> Result<T> success(String message) {
        return new Result<>(20000, message, null);
    }

    public static <T> Result<T> error() {
        return new Result<>(20001, "fail", null);
    }

    public static <T> Result<T> error(Integer code) {
        return new Result<>(code, "fail", null);
    }

    public static <T> Result<T> error(Integer code, String message) {
        return new Result<>(code, message, null);
    }

    public static <T> Result<T> error(String message) {
        return new Result<>(20001, message, null);
    }
}