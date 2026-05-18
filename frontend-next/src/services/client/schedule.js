import { apiClient } from "@/lib/api/apiFetchClient";

export async function createScheduleTemplate(data, doctorId) {
  return apiClient.post(`api/doctors/${doctorId}/schedule-templates`, data);
}

export async function updateScheduleTemplate(data, doctorId, templateId) {
  return apiClient.patch(
    `api/doctors/${doctorId}/schedule-templates/${templateId}`,
    data,
  );
}

export async function deleteScheduleTemplate(userId, templateId) {
  return apiClient.delete(
    `api/doctors/${userId}/schedule-templates/${templateId}`,
  );
}
