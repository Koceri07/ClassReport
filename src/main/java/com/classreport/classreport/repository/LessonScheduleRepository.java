package com.classreport.classreport.repository;

import com.classreport.classreport.entity.LessonScheduleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LessonScheduleRepository extends JpaRepository<LessonScheduleEntity,Long> {
    List<LessonScheduleEntity> findAll();


    @Modifying
    @Query(value = " UPDATE lesson_schedules u SET u.is_active = false WHERE u.id =:id;", nativeQuery = true)
    void softDelete(@Param("id") Long id);


    LessonScheduleEntity findByGroupId(Long groupId);
}
