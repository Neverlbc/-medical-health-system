import http from '@/api/http';

export interface Appointment {
    id?: number;
    patientId?: number;
    doctorId: number;
    doctorName?: string; // Transient
    patientName?: string; // Transient
    patientUserId?: number; // Transient
    department: string;
    appointmentDate: string;
    appointmentTime: string;
    symptoms: string;
    status?: number; // 0: Pending, 1: Completed, 2: Cancelled
    createTime?: string;
}

export const appointmentApi = {
    create: (data: Appointment) => http.post('/appointment', data),
    cancel: (id: number) => http.put(`/appointment/${id}/cancel`),
    complete: (id: number) => http.put(`/appointment/${id}/complete`),
    list: (params: { pageNum: number; pageSize: number; keyword?: string }) =>
        http.get<any, { records: Appointment[]; total: number }>('/appointment/list', { params })
};
