package com.classreport.classreport.model.response;

import com.classreport.classreport.entity.UserEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ParentResponse extends UserResponse {

    private List<StudentResponse> children;
}
