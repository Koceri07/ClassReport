package com.classreport.classreport.repository;

import com.classreport.classreport.entity.AttendanceEntity;
import com.classreport.classreport.entity.StudentAttendancesStatsEntity;
import com.classreport.classreport.model.response.StudentAttendancesStatsResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface StudentAttendancesStatsRepository extends JpaRepository<AttendanceEntity,Long> {


    @Query("""
        SELECT 
            s.id as studentId,
            CONCAT(s.name, ' ', s.surname) as fullName,
            COUNT(a) as totalLessons,
            SUM(CASE WHEN a.isAbsent = false THEN 1 ELSE 0 END) as attendedLessons,
            SUM(CASE WHEN a.isAbsent = true THEN 1 ELSE 0 END) as absentLessons
        FROM AttendanceEntity a 
        JOIN a.student s
        WHERE s.id = :studentId
        GROUP BY s.id, s.name, s.surname
    """)
    StudentStatsProjection getStudentAttendanceStats(@Param("studentId") Long studentId);


    interface StudentStatsProjection {
        Long getStudentId();

        String getFullName();

        Long getTotalLessons();

        Long getAttendedLessons();

        Long getAbsentLessons();

    }
}
