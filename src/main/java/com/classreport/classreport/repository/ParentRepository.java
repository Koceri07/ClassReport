package com.classreport.classreport.repository;

import com.classreport.classreport.entity.ParentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ParentRepository extends JpaRepository<ParentEntity, Long> {

    List<ParentEntity> findAll();

    ParentEntity findByEmail(String email);

    boolean existsByEmail(String email);
}
