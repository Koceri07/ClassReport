package com.classreport.classreport.service;

import com.classreport.classreport.entity.GroupEntity;
import com.classreport.classreport.entity.LessonInstanceEntity;
import com.classreport.classreport.entity.LessonScheduleEntity;
import com.classreport.classreport.mapper.GroupMapper;
import com.classreport.classreport.mapper.LessonInstanceMapper;
import com.classreport.classreport.mapper.LessonScheduleMapper;
import com.classreport.classreport.model.exception.AlreadyExistsException;
import com.classreport.classreport.model.exception.NotFoundException;
import com.classreport.classreport.model.exception.TodayHaventLessonException;
import com.classreport.classreport.model.request.DateRequest;
import com.classreport.classreport.model.request.LessonAddRequest;
import com.classreport.classreport.model.request.LessonInstanceRequest;
import com.classreport.classreport.model.request.LessonScheduleRequest;
import com.classreport.classreport.model.response.ApiResponse;
import com.classreport.classreport.model.response.LessonInstanceResponse;
import com.classreport.classreport.model.response.LessonPreviewResponse;
import com.classreport.classreport.repository.GroupRepository;
import com.classreport.classreport.repository.LessonInstanceRepository;
import com.classreport.classreport.repository.LessonScheduleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
@RequiredArgsConstructor
public class LessonInstanceService {

    private final LessonInstanceRepository lessonInstanceRepository;
    private final LessonScheduleRepository lessonScheduleRepository;
    private final GroupRepository groupRepository;

    public void createInstance(LessonInstanceRequest request){
        log.info("Action.createInstance.start for id {}", request.getId());
        var entity = LessonInstanceMapper.INSTANCE.requestToEntity(request);
        lessonInstanceRepository.save(entity);
        log.info("Action.createInstance.end for id {}", request.getId());
    }

    public void addExtraLesson(LessonAddRequest request){
        log.info("Action.createExtraLesson.start for id {}", request.getGroupId());
        var group = groupRepository.findById(request.getGroupId())
                        .orElseThrow(() -> new NotFoundException("Id Not Found"));

        boolean exists = lessonInstanceRepository.existsByGroupAndDateAndIsExtraTrue(group, request.getDate());

        if (exists){
            throw new AlreadyExistsException("Lesson Already Exists");
        }

        LessonInstanceEntity lesson = new LessonInstanceEntity();
        log.info("Group Entity: id={}, name={}", group.getId(), group.getGroupName());
        lesson.setGroup(group);
        lesson.setDate(request.getDate());
        lesson.setExtra(true);
//        lesson.setLessonSchedule(null);

        if (request.getLessonSchedule() == null){
            LessonScheduleEntity schedule = lessonScheduleRepository.findByGroupId(request.getGroupId());
            lesson.setLessonSchedule(schedule);
        }
        else {
            lesson.setLessonSchedule(null);
        }

        lessonInstanceRepository.save(lesson);
        log.info("Action.createExtraLesson.end for id {}", request.getGroupId());
    }

    public ApiResponse getLessonsByGroupId(Long id){
        log.info("Action.getLessonsByGroupId.start for id {}", id);
        var lessonsEntity = lessonInstanceRepository.findByGroupId(id);

        var lessons = lessonsEntity.stream()
                .map(LessonInstanceMapper.INSTANCE::entityToResponse)
                .toList();

        ApiResponse apiResponse = new ApiResponse(lessons);
        log.info("Action.getLessonsByGroupId.end for id {}", id);
        return apiResponse;
    }

    public ApiResponse getInstanceById(Long id){
        log.info("Action.getInstanceById.start for id {}", id);
        var entity = lessonInstanceRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Id Not Found"));
        var response = LessonInstanceMapper.INSTANCE.entityToResponse(entity);
        ApiResponse apiResponse = new ApiResponse(response);
        log.info("Action.getInstanceById.end for id {}", id);
        return apiResponse;
    }

    public ApiResponse getAllInstance(){
        log.info("Action.getAllInstance.start");
        var instances = lessonInstanceRepository.findAll().stream()
                        .map(LessonInstanceMapper.INSTANCE::entityToResponse)
                                .toList();
        ApiResponse apiResponse = new ApiResponse(instances);
        log.info("Action.getAllInstance.end");
        return apiResponse;
    }

    public void softDeleteById(Long id){
        log.info("Action.softDeleteById.start for id {}", id);
        lessonInstanceRepository.softDelete(id);
        log.info("Action.softDeleteById.end for id {}", id);
    }

    public void hardDeleteById(Long id){
        log.info("Action.hardDeleteById.start for id {}", id);
        lessonInstanceRepository.deleteById(id);
        log.info("Action.hardDeleteById.end for id {}", id);
    }
    public ApiResponse getAllDates(){
        log.info("Action.getAllDates.start");
        var dates = lessonInstanceRepository.findAll().stream()
                        .map(LessonInstanceEntity::getDate)
                                .distinct()
                                        .sorted()
                                                .toList();
        ApiResponse apiResponse = new ApiResponse(dates);
        log.info("Action.getAllDates.end");
        return apiResponse;
    }


//    @Scheduled(fixedDelay = 30000)
//    public void generateLessonInstancesForWeek(DateRequest dateRequest) {
//        log.info("Action.generateLessonInstancesForWeek.start for date {}", dateRequest.getStartDate());
//        List<LessonScheduleEntity> schedules = lessonScheduleRepository.findByGroupId(dateRequest.getGroupId());
//        LocalDate endDate = dateRequest.getStartDate().plusMonths(1);
//
//        for (LessonScheduleEntity schedule : schedules) {
//            for (LocalDate date = dateRequest.getStartDate(); !date.isAfter(endDate); date = date.plusDays(1)) {
//                // Yalnız bu tarix bu dərsin həftə gününə uyğundursa və exception deyilsə
//                if (schedule.getDaysOfWeek().contains(date.getDayOfWeek())
//                        && (schedule.getExceptionDates() == null || !schedule.getExceptionDates().contains(date))) {
//
//                    boolean alreadyExists = lessonInstanceRepository
//                            .existsByLessonScheduleAndDate(schedule, date);
//
//                    if (!alreadyExists) {
//                        LessonInstanceEntity instance = new LessonInstanceEntity();
//                        instance.setDate(date);
//                        instance.setLessonSchedule(schedule);
//                        lessonInstanceRepository.save(instance);
//                    }
//                }
//            }
//        }
//        log.info("Action.generateLessonInstancesForWeek.end for date {}", dateRequest.getStartDate());
//    }



    @Transactional
    public void generateLessonInstancesForWeek(DateRequest dateRequest) {
        log.info("Action.generateLessonInstancesForWeek.start for date {}", dateRequest.getStartDate());

        LocalDate startDate = dateRequest.getStartDate();
        LocalDate endDate = dateRequest.getEndDate() != null
                ? dateRequest.getEndDate()
                : startDate.plusMonths(1);

        List<LessonScheduleEntity> schedules = lessonInstanceRepository.findByGroupId(dateRequest.getGroupId()).stream()
                .map(LessonInstanceEntity::getLessonSchedule)
                .toList();


        for (LessonScheduleEntity schedule : schedules) {

            Set<DayOfWeek> daysOfWeek = schedule.getDaysOfWeek();
            Set<LocalDate> exceptionDates = schedule.getExceptionDates() != null
                    ? schedule.getExceptionDates()
                    : Collections.emptySet();

            for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
                if (!daysOfWeek.contains(date.getDayOfWeek())) continue;
                if (exceptionDates.contains(date)) continue;

                boolean exists = lessonInstanceRepository.existsByLessonScheduleAndDate(schedule, date);
                if (exists) continue;

                LessonInstanceEntity instance = new LessonInstanceEntity();
                instance.setDate(date);
                instance.setLessonSchedule(schedule);
//                instance.setGroup(schedule.getGroup());
                instance.setExtra(false);
                lessonInstanceRepository.save(instance);
            }
        }


        log.info("Action.generateLessonInstancesForWeek.end for date {}", startDate);
    }


    public ApiResponse previewTodayLesson(Long id){
//        var groupEntity = groupRepository.findById(id)
//                .orElseThrow(() -> new NotFoundException("Id Not Found"));

        var lessonSchedule = lessonScheduleRepository.findByGroupId(id);
        LocalDate today = LocalDate.now();
//        DayOfWeek todaDay = today.getDayOfWeek();

        String start = lessonSchedule.getStartTime() != null ? lessonSchedule.getStartTime().toString() : null;
        String end = lessonSchedule.getEndTime() != null ? lessonSchedule.getEndTime().toString() : null;

        LessonPreviewResponse dto = new LessonPreviewResponse(start, end, today);
        return new ApiResponse(dto);
    }


    @Transactional
    public ApiResponse confirmLesson(Long id){
        log.info("Action.confirmLesson.start for id {}", id);
        GroupEntity groupEntity = groupRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Id Not Found"));

        var lessonSchedule = lessonScheduleRepository.findByGroupId(id);
//        var lessonScheduleResponse = LessonScheduleMapper.INSTANCE.entityToResponse(lessonSchedule);

        LocalDate today = LocalDate.now();
        DayOfWeek todayDay = today.getDayOfWeek();

        if (!lessonSchedule.getDaysOfWeek().contains(todayDay)){
            throw new TodayHaventLessonException("Today Haven't Lesson");
        }

        LessonInstanceEntity instance = new LessonInstanceEntity();
        instance.setDate(today);
        instance.setLessonSchedule(lessonSchedule);
        instance.setGroup(groupEntity);
        instance.setExtra(false);

        log.info("Saving lesson instance {}", instance);
        LessonInstanceEntity save = lessonInstanceRepository.save(instance);
        lessonScheduleRepository.flush();
        log.info("Saved lesson instance id {}", save.getId());
        ApiResponse apiResponse = new ApiResponse("success");
        log.info("Action.confirmLesson.end for id {}", id);
        return apiResponse;
    }



}
