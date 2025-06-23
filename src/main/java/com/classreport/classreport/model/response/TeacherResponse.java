package com.classreport.classreport.model.response;

import com.classreport.classreport.entity.StudentEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TeacherResponse extends UserResponse{

    private List<StudentEntity> students;

}
