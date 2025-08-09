package com.classreport.classreport.model.request;

import com.classreport.classreport.entity.UserEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ParentRequest extends UserRequest {

    private List<StudentRequest> children;


}
