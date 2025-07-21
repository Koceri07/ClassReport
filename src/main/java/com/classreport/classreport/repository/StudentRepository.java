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

    @Query(value = """
            SELECT u.id, u.name, u.surname, u.password, u.role, u.is_active
                                        FROM students s
                                        JOIN users u ON s.id = u.id
                                        JOIN student_group sg ON s.id = sg.student_id
                                        WHERE sg.group_id = :groupId
                                        
                                                    """,nativeQuery = true)
    List<StudentEntity> getAllByGroup(@Param("groupId") Long id);
}
