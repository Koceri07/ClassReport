package com.classreport.classreport.repository;
import com.classreport.classreport.entity.MailEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MailRepository extends CrudRepository<MailEntity, Long> {
    List<MailEntity> findAll();

}
