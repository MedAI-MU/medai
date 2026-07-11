import { apiClient } from "@/lib/api/apiFetchClient";

export function getPatientDiagnoses(patientId) {
  return apiClient.get(`api/diagnosis/${patientId}`);
}

export function createDiagnosis(patientId, data) {
  return apiClient.post(`api/diagnosis/${patientId}`, data);
}

export function updateDiagnosis(patientId, diagnosisId, data) {
  return apiClient.patch(`api/diagnosis/${patientId}/${diagnosisId}`, data);
}

export function deleteDiagnosis(patientId, diagnosisId) {
  return apiClient.delete(`api/diagnosis/${patientId}/${diagnosisId}`);
}
