import { apiClient } from "@/lib/api/apiFetchClient";

export async function updatePatientPersonalInfo(data, patientId) {
  return apiClient.patch(`api/patients/${patientId}`, data);
}

export async function addAllergy(data, patientId) {
  return apiClient.post(`api/patients/${patientId}/allergies`, data);
}

export async function updateAllergy(data, patientId) {
  const { allergyId, ...allergyData } = data;

  return apiClient.patch(
    `api/patients/${patientId}/allergies/${allergyId}`,
    allergyData,
  );
}

export async function deleteAllergy(patientId, allergyId) {
  return apiClient.delete(`api/patients/${patientId}/allergies/${allergyId}`);
}

export async function addChronicDisease(data, patientId) {
  return apiClient.post(`api/patients/${patientId}/chronic-diseases`, data);
}

export async function updateChronicDiesease(data, patientId) {
  const { chronicId, ...chronicData } = data;

  return apiClient.patch(
    `api/patients/${patientId}/chronic-diseases/${chronicId}`,
    chronicData,
  );
}

export async function deleteChronicDisease(patientId, chronicId) {
  return apiClient.delete(
    `api/patients/${patientId}/chronic-diseases/${chronicId}`,
  );
}

export async function addSurgery(data, patientId) {
  return apiClient.post(`api/patients/${patientId}/surgeries`, data);
}

export async function updateSurgery(data, patientId) {
  const { surgeryId, ...surgeryData } = data;

  return apiClient.patch(
    `api/patients/${patientId}/surgeries/${surgeryId}`,
    surgeryData,
  );
}

export async function deleteSurgery(patientId, surgeryId) {
  return apiClient.delete(`api/patients/${patientId}/surgeries/${surgeryId}`);
}

export async function addFamilyHistory(data, patientId) {
  return apiClient.post(`api/patients/${patientId}/family-histories`, data);
}

export async function updateFamilyHistory(data, patientId) {
  const { recordId, ...familyHistoryData } = data;

  return apiClient.patch(
    `api/patients/${patientId}/family-histories/${recordId}`,
    familyHistoryData,
  );
}

export async function deleteFamilyHistory(patientId, recordId) {
  return apiClient.delete(
    `api/patients/${patientId}/family-histories/${recordId}`,
  );
}
