package com.classreport.classreport.repository;

import com.classreport.classreport.entity.TemporaryGroupTransferEntity;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface TemporaryGroupTransferRepository extends JpaRepository<TemporaryGroupTransferEntity, Long> {


    List<TemporaryGroupTransferEntity> findAllByFromGroupId(Long fromGroupId);

    List<TemporaryGroupTransferEntity> findAllByToGroupId(Long toGroupId);

    @Query("SELECT t FROM TemporaryGroupTransferEntity t WHERE t.toGroup.id = :groupId AND t.endDate >= CURRENT_DATE")
    List<TemporaryGroupTransferEntity> findActiveTransfersByToGroupId(@Param("groupId") Long groupId);

    @EntityGraph(attributePaths = {"student"})
    List<TemporaryGroupTransferEntity> findAllByToGroupIdAndEndDateAfterOrEndDateIsNull(Long groupId, LocalDate now);


//    @Query("SELECT t FROM TemporaryGroupTransferEntity t JOIN FETCH t.toGroup g WHERE g.id = :groupId AND t.endDate >= :currentDate AND g.isActive = true")
//    List<TemporaryGroupTransferEntity> findActiveTransfersToGroup(@Param("groupId") Long groupId, @Param("currentDate") LocalDate currentDate);


    @Query("SELECT t FROM TemporaryGroupTransferEntity t " +
            "JOIN FETCH t.toGroup g " +
            "WHERE g.id = :groupId " +
            "AND t.isActive = true " +
            "AND t.endDate > :currentDate")  // Vaxtı bitənlər buraya düşmür
    List<TemporaryGroupTransferEntity> findActiveTransfersToGroup(
            @Param("groupId") Long groupId,
            @Param("currentDate") LocalDate currentDate);




    @Query("SELECT t FROM TemporaryGroupTransferEntity t WHERE t.endDate = :endDate")
    List<TemporaryGroupTransferEntity> findAllByEndDate(@Param("endDate") LocalDate endDate);

}
