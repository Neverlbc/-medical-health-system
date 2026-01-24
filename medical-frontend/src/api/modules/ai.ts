import http from '@/api/http';

export interface SymptomAnalysisPayload {
  symptoms: string;
}

export const analyzeSymptom = (payload: SymptomAnalysisPayload) =>
  http.post<string>('/ai/symptom-analysis', payload, { timeout: 60000 });

export interface MedicationGuidePayload {
  medicineName: string;
}

export const getMedicationGuide = (payload: MedicationGuidePayload) =>
  http.post<string>('/ai/medication-guide', payload, { timeout: 60000 });

export interface HealthQuestionPayload {
  question: string;
}

export const answerHealthQuestion = (payload: HealthQuestionPayload) =>
  http.post<string>('/ai/health-question', payload, { timeout: 60000 });

export interface HealthAnalysisPayload {
  question?: string;
  patientId?: number;
}

export const aiApi = {
  analyzeHealth: (payload: HealthAnalysisPayload) =>
    http.post<string>('/ai/health-analysis', payload, { timeout: 120000 }), // Longer timeout for analysis

  getDailyTip: () =>
    http.get<string>('/ai/daily-tip', { timeout: 30000 }),

  chat: (payload: { message: string }) =>
    http.post<string>('/ai/chat', payload, { timeout: 60000 }),

  // Re-export others for convenience if needed, or just keep using standalone functions
  analyzeSymptom,
  getMedicationGuide,
  answerHealthQuestion
};

