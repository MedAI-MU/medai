import { apiClient } from "@/lib/api/apiFetchClient";

export async function createScheduleTemplate(data, userId) {
  return apiClient.post(`api/doctors/${userId}/schedule-templates`, data);
}

export async function updateScheduleTemplate(data, userId, templateId) {
  return apiClient.patch(
    `api/doctors/${userId}/schedule-templates/${templateId}`,
    data,
  );
}

export async function deleteScheduleTemplate(userId, templateId) {
  return apiClient.delete(
    `api/doctors/${userId}/schedule-templates/${templateId}`,
  );
}

export async function applyScheduleTemplate(data, userId, templateId) {
  return apiClient.post(
    `api/doctors/${userId}/schedule-templates/${templateId}/apply`,
    data,
  );
}

export function createScheduleSlots(data, doctorId) {
  return apiClient.post(`api/doctors/${doctorId}/schedule-slots`, data);
}

export async function updateScheduleSlot(data, doctorId, slotId) {
  return apiClient.patch(
    `api/doctors/${doctorId}/schedule-slots/${slotId}`,
    data,
  );
}

export async function deleteScheduleSlot(doctorId, slotId) {
  return apiClient.delete(`api/doctors/${doctorId}/schedule-slots/${slotId}`);
}
