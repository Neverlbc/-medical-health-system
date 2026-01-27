import http from '@/api/http';

/**
 * 医生排班信息
 */
export interface DoctorSchedule {
    id: number;
    doctorId: number;
    scheduleDate: string;
    timePeriod: string;
    maxPatients: number;
    bookedPatients: number;
    status: number;
    doctorName?: string;
    department?: string;
    title?: string;
    availableSlots?: number;
    createTime?: string;
    updateTime?: string;
}

/**
 * 排班检查结果
 */
export interface ScheduleCheckResult {
    available: boolean;
    message: string;
    remainingSlots: number;
    schedule?: DoctorSchedule;
}

/**
 * 批量创建排班请求
 */
export interface BatchCreateRequest {
    doctorId: number;
    startDate: string;
    endDate: string;
    timePeriods?: string[];
    maxPatients?: number;
}

/**
 * 排班查询参数
 */
export interface ScheduleQueryParams {
    doctorId?: number;
    department?: string;
    startDate?: string;
    endDate?: string;
    status?: number;
}

/**
 * 医生排班 API
 */
export const scheduleApi = {
    /**
     * 获取医生排班
     */
    getByDoctor: (doctorId: number, startDate: string, endDate: string) =>
        http.get<any, DoctorSchedule[]>('/schedule/doctor/' + doctorId, {
            params: { startDate, endDate }
        }),

    /**
     * 获取科室排班
     */
    getByDepartment: (department: string, startDate: string, endDate: string) =>
        http.get<any, DoctorSchedule[]>('/schedule/department', {
            params: { department, startDate, endDate }
        }),

    /**
     * 检查排班是否可预约
     */
    checkAvailability: (doctorId: number, date: string, timePeriod: string) =>
        http.get<any, ScheduleCheckResult>('/schedule/check', {
            params: { doctorId, date, timePeriod }
        }),

    /**
     * 生成排班
     */
    generate: (days: number = 7) =>
        http.post('/schedule/generate', null, { params: { days } }),

    // ========== 管理员排班管理 API ==========

    /**
     * 查询排班列表
     */
    list: (params: ScheduleQueryParams) =>
        http.get<any, DoctorSchedule[]>('/schedule/list', { params }),

    /**
     * 获取单个排班详情
     */
    getById: (id: number) =>
        http.get<any, DoctorSchedule>(`/schedule/${id}`),

    /**
     * 创建排班
     */
    create: (schedule: Partial<DoctorSchedule>) =>
        http.post<any, DoctorSchedule>('/schedule', schedule),

    /**
     * 更新排班
     */
    update: (id: number, schedule: Partial<DoctorSchedule>) =>
        http.put<any, DoctorSchedule>(`/schedule/${id}`, schedule),

    /**
     * 删除排班
     */
    delete: (id: number) =>
        http.delete(`/schedule/${id}`),

    /**
     * 批量创建排班
     */
    batchCreate: (request: BatchCreateRequest) =>
        http.post<any, number>('/schedule/batch', request),

    /**
     * 批量删除排班
     */
    batchDelete: (ids: number[]) =>
        http.delete<any, number>('/schedule/batch', { data: ids }),

    /**
     * 切换排班状态
     */
    toggleStatus: (id: number, status: number) =>
        http.put<any, DoctorSchedule>(`/schedule/${id}/status`, null, { params: { status } })
};

