package com.classreport.classreport.model.request;

import com.classreport.classreport.entity.LessonScheduleEntity;
import com.classreport.classreport.entity.StudentEntity;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GroupRequest {

    private Long id;
    private String groupName;
    private boolean isActive;
    private List<StudentEntity> students;
    private List<LessonScheduleEntity> lessonSchedules;

}
