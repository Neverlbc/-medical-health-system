import http from '@/api/http';

export interface SimpleUser {
  id: number;
  username: string;
  nickname?: string;
  phone?: string;
}

export const searchPatients = (keyword: string) =>
  http.get<SimpleUser[]>('/user/patients', { params: { keyword, pageNum: 1, pageSize: 20 } });

export interface PatientInfo {
  id: number;
  userId: number;
  realName: string;
  gender: number;
  birthday: string;
  phone: string;
  idCard: string;
  height: number;
  weight: number;
  bloodType: string;
  emergencyContact: string;
  emergencyPhone: string;
  createTime: string;
}

export const getPatientInfoByUserId = (userId: number) => http.get<PatientInfo>(`/patient/info/user/${userId}`);
export const getPatientInfoById = (id: number) => http.get<PatientInfo>(`/patient/info/${id}`);

