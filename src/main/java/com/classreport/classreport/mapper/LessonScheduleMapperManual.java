package com.classreport.classreport.mapper;


import com.classreport.classreport.entity.LessonScheduleEntity;
import com.classreport.classreport.entity.TeacherEntity;
import com.classreport.classreport.model.request.LessonScheduleRequest;
import com.classreport.classreport.model.response.LessonScheduleResponse;
import com.classreport.classreport.model.response.TeacherResponse;
import org.springframework.stereotype.Component;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.LinkedHashSet;
import java.util.Set;

@Component
public class LessonScheduleMapperManual {

    private final TeacherMapper teacherMapper;

    public LessonScheduleMapperManual(TeacherMapper teacherMapper) {
        this.teacherMapper = teacherMapper;
    }

    public LessonScheduleEntity requestToEntity(LessonScheduleRequest lessonScheduleRequest) {
        if (lessonScheduleRequest == null) {
            return null;
        }

        LessonScheduleEntity lessonScheduleEntity = new LessonScheduleEntity();
        lessonScheduleEntity.setId(lessonScheduleRequest.getId());

        Set<DayOfWeek> daysOfWeek = lessonScheduleRequest.getDaysOfWeek();
        if (daysOfWeek != null) {
            lessonScheduleEntity.setDaysOfWeek(new LinkedHashSet<>(daysOfWeek));
        }

        lessonScheduleEntity.setStartTime(lessonScheduleRequest.getStartTime());
        lessonScheduleEntity.setEndTime(lessonScheduleRequest.getEndTime());

        return lessonScheduleEntity;
    }

    public LessonScheduleRequest entityToRequest(LessonScheduleEntity entity) {
        if (entity == null) {
            return null;
        }

        LessonScheduleRequest lessonScheduleRequest = new LessonScheduleRequest();
        lessonScheduleRequest.setId(entity.getId());

        Set<DayOfWeek> daysOfWeek = entity.getDaysOfWeek();
        if (daysOfWeek != null) {
            lessonScheduleRequest.setDaysOfWeek(new LinkedHashSet<>(daysOfWeek));
        }

        lessonScheduleRequest.setStartTime(entity.getStartTime());
        lessonScheduleRequest.setEndTime(entity.getEndTime());

        return lessonScheduleRequest;
    }

    public LessonScheduleResponse entityToResponse(LessonScheduleEntity entity) {
        if (entity == null) {
            return null;
        }

        LessonScheduleResponse lessonScheduleResponse = new LessonScheduleResponse();
        lessonScheduleResponse.setId(entity.getId());

        Set<DayOfWeek> daysOfWeek = entity.getDaysOfWeek();
        if (daysOfWeek != null) {
            lessonScheduleResponse.setDaysOfWeek(new LinkedHashSet<>(daysOfWeek));
        }

        lessonScheduleResponse.setStartTime(entity.getStartTime());
        lessonScheduleResponse.setEndTime(entity.getEndTime());

        // Teacher mapping
        TeacherEntity teacher = entity.getTeacher();
        if (teacher != null) {
            TeacherResponse teacherResponse = teacherMapper.entityToResponse(teacher);
            lessonScheduleResponse.setTeacher(teacherResponse);
        }

        Set<LocalDate> exceptionDates = entity.getExceptionDates();
        if (exceptionDates != null) {
            lessonScheduleResponse.setExceptionDates(new LinkedHashSet<>(exceptionDates));
        }

        return lessonScheduleResponse;
    }

    public TeacherResponse teacherEntityToTeacherResponse(TeacherEntity teacherEntity) {
        return teacherMapper.entityToResponse(teacherEntity);
    }
}