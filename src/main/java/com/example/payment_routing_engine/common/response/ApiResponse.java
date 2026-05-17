package com.example.payment_routing_engine.common.response;



import java.time.Instant;

public record ApiResponse<T>(
        boolean success,
        T data,
        String message,
        Instant timestamp
) {
    public static <T> ApiResponse<T> ok(T data){
        return new ApiResponse<>(true,data,"ok",Instant.now());
    }
    public static <T> ApiResponse<T> ok(T date,String message){
        return new ApiResponse<>(true,date, message,Instant.now());
    }
    public static <T> ApiResponse<T> error(String message) {
        return new ApiResponse<>(false, null, message, Instant.now());
    }
}
