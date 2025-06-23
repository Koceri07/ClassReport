package com.classreport.classreport.model.response;

import com.classreport.classreport.entity.GroupEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StudentResponse extends UserResponse{

    private List<GroupEntity> groups;

}
