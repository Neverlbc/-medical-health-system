import http from '@/api/http';

export interface HealthData {
    id?: number;
    patientId?: number;
    dataType: string;
    systolicPressure?: number;
    diastolicPressure?: number;
    bloodSugar?: number;
    heartRate?: number;
    temperature?: number;
    weight?: number;
    remark?: string;
    measureTime: string;
    status?: number;
    createTime?: string;
}

export interface HealthDataQuery {
    current?: number;
    size?: number;
    dataType?: string;
    patientId?: number;
}

export interface HealthDataTrendQuery {
    dataType: string;
    days?: number;
    patientId?: number;
}

export interface PageResult<T> {
    records: T[];
    total: number;
    size: number;
    current: number;
}

/**
 * 创建健康数据记录
 */
export function createHealthData(data: HealthData) {
    return http({
        url: '/health-data',
        method: 'post',
        data
    });
}

/**
 * 分页查询健康数据
 */
export function getHealthDataPage(params: HealthDataQuery) {
    return http<any, PageResult<HealthData>>({
        url: '/health-data/list',
        method: 'get',
        params
    });
}

/**
 * 获取健康数据详情
 */
export function getHealthDataById(id: number) {
    return http<any, HealthData>({
        url: `/health-data/${id}`,
        method: 'get'
    });
}

/**
 * 删除健康数据
 */
export function deleteHealthData(id: number) {
    return http({
        url: `/health-data/${id}`,
        method: 'delete'
    });
}

/**
 * 获取健康数据趋势（用于图表）
 */
export function getHealthDataTrend(params: HealthDataTrendQuery) {
    return http<any, HealthData[]>({
        url: '/health-data/trend',
        method: 'get',
        params
    });
}

/**
 * 获取健康数据统计信息
 */
export function getHealthDataStatistics(patientId?: number) {
    return http<any, any>({
        url: '/health-data/statistics',
        method: 'get',
        params: { patientId }
    });
}
