import http from '@/api/http';

export interface PatientRecord {
  id?: number;
  userId?: number;
  allergies?: string;
  familyHistory?: string;
  medicalHistory?: string;
  medicationHistory?: string;
  remark?: string;
  createTime?: string;
  updateTime?: string;
}

export interface PageResult<T> {
  records: T[];
  total: number;
  current: number;
  size: number;
}

export const listRecords = (params: { pageNum?: number; pageSize?: number; keyword?: string; userId?: number }) =>
  http.get<PageResult<PatientRecord>>('/record/list', { params });

export const getRecord = (id: number) => http.get<PatientRecord>(`/record/${id}`);

export const createRecord = (payload: PatientRecord) => http.post('/record', payload);

export const updateRecord = (id: number, payload: PatientRecord) => http.put(`/record/${id}`, payload);

export interface RecordAttachment {
  id?: number;
  recordId?: number;
  fileName: string;
  fileUrl: string;
  fileType?: string;
  fileSize?: number;
  createTime?: string;
}

export const listAttachments = (recordId: number) =>
  http.get<RecordAttachment[]>(`/record/${recordId}/attachments`);

export const addAttachment = (recordId: number, payload: RecordAttachment) =>
  http.post(`/record/${recordId}/attachments`, payload);

export const deleteAttachment = (attachmentId: number) =>
  http.delete(`/record/attachments/${attachmentId}`);
