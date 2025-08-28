package com.classreport.classreport.repository;

import com.classreport.classreport.entity.ReportEntity;
import com.classreport.classreport.entity.StudentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface ReportRepository extends JpaRepository<ReportEntity, Long> {
    List<ReportEntity> findAll();

    List<ReportEntity> findByStudent_Id(Long studentId);

    List<ReportEntity> findByTeacher_Id(Long teacherId);

    List<ReportEntity> findByStudentIdAndTeacherId(Long studentId, Long teacherId);

    Optional<ReportEntity> findByStudentAndReportDate(StudentEntity student, LocalDate reportDate);

}
