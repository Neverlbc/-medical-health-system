package com.medical.system.dto.statistics;

import lombok.Data;

@Data
public class OverviewVO {
    private Long totalPatients;
    private Long todayAppointments;
    private Long totalDiagnoses;
}
