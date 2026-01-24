package com.medical.system.service;

import com.medical.system.dto.statistics.DiseaseStatVO;
import com.medical.system.dto.statistics.OverviewVO;
import com.medical.system.dto.statistics.TrendStatVO;

import java.util.List;

public interface StatisticsService {
    OverviewVO getOverview();
    List<DiseaseStatVO> getDiseaseDistribution();
    List<TrendStatVO> getAppointmentTrend();
}
