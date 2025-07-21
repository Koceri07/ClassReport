package com.classreport.classreport.model.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GroupDetailsResponse {

    private Long id;
    private String groupName;
//    private Integer totalLessons;
//    private Integer extraLessons;

    private List<LessonInstanceResponse> lessons;
    private List<LessonScheduleResponse> lessonTime;

}
