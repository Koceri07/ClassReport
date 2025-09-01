package com.classreport.classreport.repository;

import com.classreport.classreport.entity.GroupEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GroupRepository extends JpaRepository<GroupEntity,Long> {
    List<GroupEntity> findAll();


    @Modifying
    @Query(value = " UPDATE groups u SET u.is_active = false WHERE u.id =:id;", nativeQuery = true)
    void softDelete(@Param("id") Long id);


    List<GroupEntity> findByTeacher_Id(Long teacherId);




}
