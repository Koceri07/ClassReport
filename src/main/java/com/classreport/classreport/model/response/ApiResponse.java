package com.classreport.classreport.model.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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

    public void badRequest(Object data){
        code = "500";
        message = "bad request";
        this.data = data;

    }

}
