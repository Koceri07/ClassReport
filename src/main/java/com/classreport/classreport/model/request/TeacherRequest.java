package com.classreport.classreport.model.request;

import com.classreport.classreport.entity.StudentEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TeacherRequest extends UserRequest{

    private List<StudentEntity> students;

}
