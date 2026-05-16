import { apiClient } from "@/lib/api/apiFetchClient";

export async function createScheduleTemplate(data, doctorId) {
  return apiClient.post(`api/doctors/${doctorId}/schedule-templates`, data);
}
