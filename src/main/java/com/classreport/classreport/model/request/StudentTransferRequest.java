package com.classreport.classreport.model.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StudentTransferRequest {

    @NotNull
    public Long studentId;
    @NotNull
    public Long targetGroupId;


}
