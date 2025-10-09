package com.classreport.classreport.model.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
//@AllArgsConstructor
@NoArgsConstructor
public class ApiResponse {

    private String code;
    private  String message;
    private Object data;

    public ApiResponse(Object data){
        code = "200";
        message = "successfully";
        this.data = data;
    }

    public ApiResponse(Object data, String code){
        this.code = code;
        this.data = data;
        message = "successfully";
    }

    public ApiResponse(String message, String code) {
        this.code = code;
        this.message = message;
    }

    public void badRequest(Object data){
        code = "500";
        message = "bad request";
        this.data = data;

    }

}
