package com.classreport.classreport.model.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DateRequest {
    private LocalDate startDate;
    private LocalDate endDate;

}
