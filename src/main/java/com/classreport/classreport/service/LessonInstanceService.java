package com.classreport.classreport.service;

import com.classreport.classreport.entity.LessonInstanceEntity;
import com.classreport.classreport.entity.LessonScheduleEntity;
import com.classreport.classreport.mapper.LessonInstanceMapper;
import com.classreport.classreport.model.exception.NotFoundException;
import com.classreport.classreport.model.request.DateRequest;
import com.classreport.classreport.model.request.LessonInstanceRequest;
import com.classreport.classreport.model.response.ApiResponse;
import com.classreport.classreport.repository.LessonInstanceRepository;
import com.classreport.classreport.repository.LessonScheduleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
@RequiredArgsConstructor
public class LessonInstanceService {

    private final LessonInstanceRepository lessonInstanceRepository;
    private final LessonScheduleRepository lessonScheduleRepository;

    public void createInstance(LessonInstanceRequest request){
        log.info("Action.createInstance.start for id {}", request.getId());
        var entity = LessonInstanceMapper.INSTANCE.requestToEntity(request);
        lessonInstanceRepository.save(entity);
        log.info("Action.createInstance.end for id {}", request.getId());
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
    public void generateLessonInstancesForWeek(DateRequest dateRequest) {
        log.info("Action.generateLessonInstancesForWeek.start for date {}", dateRequest.getStartDate());
        List<LessonScheduleEntity> schedules = lessonScheduleRepository.findAll();
        LocalDate endDate = dateRequest.getStartDate().plusMonths(1);

        for (LessonScheduleEntity schedule : schedules) {
            for (LocalDate date = dateRequest.getStartDate(); !date.isAfter(endDate); date = date.plusDays(1)) {
                // Yalnız bu tarix bu dərsin həftə gününə uyğundursa və exception deyilsə
                if (schedule.getDaysOfWeek().contains(date.getDayOfWeek())
                        && (schedule.getExceptionDates() == null || !schedule.getExceptionDates().contains(date))) {

                    boolean alreadyExists = lessonInstanceRepository
                            .existsByLessonScheduleAndDate(schedule, date);

                    if (!alreadyExists) {
                        LessonInstanceEntity instance = new LessonInstanceEntity();
                        instance.setDate(date);
                        instance.setLessonSchedule(schedule);
                        lessonInstanceRepository.save(instance);
                    }
                }
            }
        }
        log.info("Action.generateLessonInstancesForWeek.end for date {}", dateRequest.getStartDate());
    }


}
