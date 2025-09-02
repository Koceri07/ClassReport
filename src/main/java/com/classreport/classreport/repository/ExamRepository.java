package com.classreport.classreport.repository;

import com.classreport.classreport.entity.ExamEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExamRepository extends JpaRepository<ExamEntity, Long> {

    List<ExamEntity> findAll();

    @Query("SELECT e FROM ExamEntity e WHERE e.student.id = :studentId")
    List<ExamEntity> findByStudentId(@Param("studentId") Long studentId);

}
