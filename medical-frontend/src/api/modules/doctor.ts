import http from '../http';

export interface DoctorInfo {
    id: number;
    userId: number;
    realName: string;
    department: string;
    title: string;
    specialty: string;
    introduction: string;
    workYears: number;
    consultationFee: number;
    rating: number;
    patientCount: number;
    status: number;
}

export const getDoctorList = () => {
    return http.get<any, DoctorInfo[]>('/doctor/list');
};
