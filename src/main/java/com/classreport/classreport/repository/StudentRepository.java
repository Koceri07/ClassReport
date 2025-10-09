package com.classreport.classreport.repository;

import com.classreport.classreport.entity.StudentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StudentRepository extends JpaRepository<StudentEntity, Long> {
    List<StudentEntity> findAll();


    @Modifying
    @Query(value = " UPDATE users u SET u.is_active = false WHERE u.id =:id;", nativeQuery = true)
    void softDelete(@Param("id") Long id);




    @Query("SELECT s FROM StudentEntity s " +
            "JOIN s.groups u " +
            "WHERE u.id = :groupId " +
            "AND s.isTransfer = false")
    List<StudentEntity> getAllByGroup(@Param("groupId") Long id);


    @Query("SELECT s FROM StudentEntity s WHERE s.parentInvadeCode = :parentInvadeCode AND s.isActive = true")
    StudentEntity findByParentInvadeCodeAndActiveTrue(@Param("parentInvadeCode") String parentInvadeCode);

//    StudentEntity findByParentInvadeCodeAndActiveTrue(String parentInvadeCode);


    @Query("SELECT g.id FROM StudentEntity s JOIN s.groups g WHERE s.id = :studentId")
    List<Long> findGroupIdsByStudentId(@Param("studentId") Long studentId);

    StudentEntity findByEmail(String email);

    boolean existsByEmail(String email);

}
