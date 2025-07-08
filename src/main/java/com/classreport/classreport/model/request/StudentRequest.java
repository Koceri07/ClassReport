package com.classreport.classreport.model.request;


import com.classreport.classreport.entity.GroupEntity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StudentRequest extends UserRequest{

    private List<GroupEntity> groups;

}

