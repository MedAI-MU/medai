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
