import request from '@/api/http';

export interface OverviewVO {
    totalPatients: number;
    todayAppointments: number;
    totalDiagnoses: number;
}

export interface DiseaseStatVO {
    name: string;
    value: number;
}

export interface TrendStatVO {
    date: string;
    count: number;
}

export const getOverview = () => {
    return request.get<OverviewVO>('/statistics/overview');
};

export const getDiseaseDistribution = () => {
    return request.get<DiseaseStatVO[]>('/statistics/disease');
};

export const getAppointmentTrend = () => {
    return request.get<TrendStatVO[]>('/statistics/trend');
};
