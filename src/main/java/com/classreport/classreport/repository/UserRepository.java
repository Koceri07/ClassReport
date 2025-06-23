package com.classreport.classreport.repository;

import com.classreport.classreport.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {
    List<UserEntity> findAll();


    @Modifying
    @Query(value = " UPDATE users u SET u.is_active = false WHERE u.id =:id;", nativeQuery = true)
    void softDelete(@Param("id") Long id);

}
