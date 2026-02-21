package com.origin;
import lombok.Data;

/**
 * 通用返回体
 */
@Data
public class Response <T>{
    private T data;
    private boolean success;
    private String errorMsg;
    public static <K> Response<K> newSuccess(K data){
        Response<K> response = new Response<>();
        response.setData(data);
        response.setSuccess(true);
        return response;
    }

    public static <K> Response<K> newFail(String errorMsg){
        Response<K> response = new Response<>();
        response.setSuccess(false);
        response.setErrorMsg(errorMsg);
        return response;
    }
}
