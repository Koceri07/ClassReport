package com.classreport.classreport.repository;

import com.classreport.classreport.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {
    List<UserEntity> findAll();


    @Modifying
    @Query(value = " UPDATE users u SET u.is_active = false WHERE u.id =:id;", nativeQuery = true)
    void softDelete(@Param("id") Long id);

    Optional<UserEntity> findByEmail(String email);
    // Complex join sorğusu əvəzinə sadə sorğu
    @Query("SELECT u FROM UserEntity u WHERE u.email = :email")
    Optional<UserEntity> findSimpleByEmail(@Param("email") String email);

    // Orijinal metod
    boolean existsByEmail(String email);

//    // Əlavə metod
//    @Query("SELECT u FROM UserEntity u LEFT JOIN FETCH u.teacher WHERE u.email = :email")
//    Optional<UserEntity> findByEmailWithTeacher(@Param("email") String email);
//
//    @Query("SELECT u FROM UserEntity u LEFT JOIN FETCH u.student WHERE u.email = :email")
//    Optional<UserEntity> findByEmailWithStudent(@Param("email") String email);
//
//    @Query("SELECT u FROM UserEntity u LEFT JOIN FETCH u.parent WHERE u.email = :email")
//    Optional<UserEntity> findByEmailWithParent(@Param("email") String email);
}
