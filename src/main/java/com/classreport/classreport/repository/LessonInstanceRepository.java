package com.classreport.classreport.repository;

import com.classreport.classreport.entity.LessonInstanceEntity;
import com.classreport.classreport.entity.LessonScheduleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface LessonInstanceRepository extends JpaRepository<LessonInstanceEntity, Long> {
    List<LessonInstanceEntity> findAll();

    boolean existsByLessonScheduleAndDate(LessonScheduleEntity lessonSchedule, LocalDate date);


    @Modifying
    @Query(value = " UPDATE lesson_instances u SET u.is_active = false WHERE u.id =:id;", nativeQuery = true)
    void softDelete(@Param("id") Long id);


    Optional findByDate(LocalDate date);



}
