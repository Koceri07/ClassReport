package com.classreport.classreport.repository;

import com.classreport.classreport.entity.AttendanceEntity;
import com.classreport.classreport.entity.LessonInstanceEntity;
import com.classreport.classreport.entity.StudentEntity;
import com.classreport.classreport.entity.TeacherEntity;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface AttendanceRepository extends JpaRepository<AttendanceEntity, Long> {

    @EntityGraph(attributePaths = {
            "student",
            "lessonInstance",
            "lessonInstance.lessonSchedule",
            "lessonInstance.lessonSchedule.group",
            "lessonInstance.lessonSchedule.teacher"
    })
    List<AttendanceEntity> findAll();

    @Transactional
    @Modifying
    @Query(value = """
    UPDATE attendances a
    SET 
        a.date = COALESCE(:date, a.date),
        a.is_absent = COALESCE(:isAbsent, a.is_absent),
        a.late_time = COALESCE(:lateTime, a.late_time),
        a.note = COALESCE(:note, a.note),
        a.student_id = COALESCE(:#{#student.id}, a.student_id),
        a.teacher_id = COALESCE(:#{#teacher.id}, a.teacher_id),
        a.lesson_instance_id = COALESCE(:#{#lessonInstance.id}, a.lesson_instance_id)
    WHERE a.id = :id
    """, nativeQuery = true)
    void updateAttendances(
            @Param("id") Long id,
            @Param("date") LocalDateTime date,
            @Param("isAbsent") Boolean isAbsent,
            @Param("lateTime") String lateTime,
            @Param("note") String note,
            @Param("student") StudentEntity student,
            @Param("teacher") TeacherEntity teacher,
            @Param("lessonInstance") LessonInstanceEntity lessonInstance
    );

    @Modifying
    @Query(value = " UPDATE attendances u SET u.is_active = false WHERE u.id =:id;", nativeQuery = true)
    void softDelete(@Param("id") Long id);

//    Optional findByStudentAndLessonInstance(StudentEntity student, LessonInstanceEntity lessonInstance);

    Optional<AttendanceEntity> findByStudentAndLessonInstance(StudentEntity student, LessonInstanceEntity lessonInstance);


    List<AttendanceEntity> findByStudentId(Long studentId);

    List<AttendanceEntity> findByGroupId(Long groupId);

    @Query("SELECT a FROM AttendanceEntity a WHERE a.student.id = :studentId AND a.isAbsent = :isAbsent")
    List<AttendanceEntity> findByStudentIdAndIsAbsent(@Param("studentId") Long studentId,@Param("isAbsent") Boolean isAbsent);


}
