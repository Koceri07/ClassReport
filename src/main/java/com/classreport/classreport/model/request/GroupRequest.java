package com.classreport.classreport.model.request;

import com.classreport.classreport.entity.LessonScheduleEntity;
import com.classreport.classreport.entity.StudentEntity;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GroupRequest {

    private Long id;
    @NotBlank
    @Size(max = 15)
    private String groupName;
    @NotNull
    private boolean isActive;
    private LessonScheduleRequest lessonSchedule;
    private TeacherRequest teacherRequest;

}
