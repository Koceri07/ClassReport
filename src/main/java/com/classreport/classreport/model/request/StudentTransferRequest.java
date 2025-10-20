package com.classreport.classreport.model.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StudentTransferRequest {

    public Long studentId;
    public Long targetGroupId;


}
