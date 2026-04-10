import { apiClient } from "@/lib/api/apiFetchClient";

export async function updatePatientPersonalInfo(data, patientId) {
  return apiClient.patch(`api/patients/${patientId}`, data);
}
