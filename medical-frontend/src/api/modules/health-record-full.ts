import http from '@/api/http';

// --- 1. 病史信息 (PatientHistory) ---
export interface PatientHistory {
    id?: number;
    patientId?: number;
    historyType: 'PERSONAL' | 'FAMILY' | 'PAST';
    diseaseName?: string;
    diagnosisDate?: string;
    relationship?: string;
    description?: string;
    status?: number;
    createTime?: string;
}

export const historyApi = {
    create: (data: PatientHistory) => http.post('/history', data),
    update: (data: PatientHistory) => http.put('/history', data),
    delete: (id: number) => http.delete(`/history/${id}`),
    list: (patientId: number, type?: string) => http.get<PatientHistory[]>('/history/list', { params: { patientId, type } })
};

// --- 2. 过敏史 (PatientAllergy) ---
export interface PatientAllergy {
    id?: number;
    patientId?: number;
    allergen: string;
    allergyType?: 'DRUG' | 'FOOD' | 'ENV' | 'OTHER';
    reaction?: string;
    severity?: 'MILD' | 'MODERATE' | 'SEVERE';
    recordDate?: string;
    createTime?: string;
}

export const allergyApi = {
    create: (data: PatientAllergy) => http.post('/allergy', data),
    update: (data: PatientAllergy) => http.put('/allergy', data),
    delete: (id: number) => http.delete(`/allergy/${id}`),
    list: (patientId: number) => http.get<PatientAllergy[]>('/allergy/list', { params: { patientId } })
};

// --- 3. 诊断记录 (DiagnosisRecord) ---
export interface DiagnosisRecord {
    id?: number;
    patientId?: number;
    diagnosisType: 'PRELIMINARY' | 'CONFIRMED';
    diseaseName: string;
    icd10Code?: string;
    diagnosisDate: string;
    doctorId?: number;
    description?: string;
    createTime?: string;
}

export const diagnosisApi = {
    create: (data: DiagnosisRecord) => http.post('/diagnosis', data),
    update: (data: DiagnosisRecord) => http.put('/diagnosis', data),
    delete: (id: number) => http.delete(`/diagnosis/${id}`),
    list: (patientId: number) => http.get<DiagnosisRecord[]>('/diagnosis/list', { params: { patientId } }),
    page: (params: { pageNum: number; pageSize: number; patientId: number; keyword?: string }) =>
        http.get('/diagnosis/page', { params })
};

// --- 4. 生命体征 (VitalSigns) ---
export interface VitalSigns {
    id?: number;
    patientId?: number;
    systolic?: number;
    diastolic?: number;
    heartRate?: number;
    temperature?: number;
    spo2?: number;
    respiratoryRate?: number;
    height?: number;
    weight?: number;
    bmi?: number;
    measureTime?: string;
    recorder?: string;
    createTime?: string;
}

export const vitalSignsApi = {
    create: (data: VitalSigns) => http.post('/vital-signs', data),
    update: (data: VitalSigns) => http.put('/vital-signs', data),
    delete: (id: number) => http.delete(`/vital-signs/${id}`),
    list: (patientId: number) => http.get<VitalSigns[]>('/vital-signs/list', { params: { patientId } })
};

// --- 5. 体格检查 (PhysicalExam) ---
export interface PhysicalExam {
    id?: number;
    patientId?: number;
    examDate: string;
    generalCondition?: string;
    skinMucosa?: string;
    lymphNodes?: string;
    headNeck?: string;
    chestLungs?: string;
    heart?: string;
    abdomen?: string;
    spineLimbs?: string;
    nervousSystem?: string;
    doctorId?: number;
    createTime?: string;
}

export const physicalExamApi = {
    create: (data: PhysicalExam) => http.post('/physical-exam', data),
    update: (data: PhysicalExam) => http.put('/physical-exam', data),
    delete: (id: number) => http.delete(`/physical-exam/${id}`),
    list: (patientId: number) => http.get<PhysicalExam[]>('/physical-exam/list', { params: { patientId } })
};

// --- 6. 实验室检查 (LabTest) ---
export interface LabTestItem {
    id?: number;
    testId?: number;
    itemName: string;
    itemValue: string;
    unit?: string;
    referenceRange?: string;
    isAbnormal: number; // 0: No, 1: Yes
    abnormalFlag?: 'H' | 'L';
}

export interface LabTest {
    id?: number;
    patientId?: number;
    testType: string;
    testDate: string;
    hospital?: string;
    reportUrl?: string;
    doctorId?: number;
    resultSummary?: string;
    createTime?: string;
    items?: LabTestItem[]; // For creating
}

export const labTestApi = {
    create: (data: LabTest) => http.post('/lab-test', data),
    update: (data: LabTest) => http.put('/lab-test', data),
    delete: (id: number) => http.delete(`/lab-test/${id}`),
    list: (patientId: number) => http.get<LabTest[]>('/lab-test/list', { params: { patientId } }),
    getItems: (testId: number) => http.get<LabTestItem[]>(`/lab-test/${testId}/items`),
    addItem: (data: LabTestItem) => http.post('/lab-test/item', data),
    deleteItem: (itemId: number) => http.delete(`/lab-test/item/${itemId}`)
};

// --- 7. 治疗方案 (TreatmentPlan) ---
export interface TreatmentPlan {
    id?: number;
    patientId?: number;
    diagnosisId?: number;
    planName: string;
    startDate: string;
    endDate?: string;
    status: 'ACTIVE' | 'COMPLETED' | 'TERMINATED';
    doctorId?: number;
    description?: string;
    createTime?: string;
}

export const treatmentPlanApi = {
    create: (data: TreatmentPlan) => http.post('/treatment-plan', data),
    update: (data: TreatmentPlan) => http.put('/treatment-plan', data),
    delete: (id: number) => http.delete(`/treatment-plan/${id}`),
    list: (patientId: number) => http.get<TreatmentPlan[]>('/treatment-plan/list', { params: { patientId } })
};

// --- 8. 随访记录 (FollowUp) ---
export interface FollowUp {
    id?: number;
    patientId?: number;
    doctorId?: number;
    followUpDate: string;
    method?: 'OUTPATIENT' | 'PHONE' | 'HOME';
    content?: string;
    result?: string;
    nextDate?: string;
    createTime?: string;
}

export const followUpApi = {
    create: (data: FollowUp) => http.post('/follow-up', data),
    update: (data: FollowUp) => http.put('/follow-up', data),
    delete: (id: number) => http.delete(`/follow-up/${id}`),
    list: (patientId: number) => http.get<FollowUp[]>('/follow-up/list', { params: { patientId } })
};
