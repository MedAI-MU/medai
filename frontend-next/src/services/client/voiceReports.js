import { apiClient } from "@/lib/api/apiFetchClient";

export function uploadAudio(appointmentId, formData) {
  return apiClient.post(`api/appointments/${appointmentId}/voice-reports`, formData);
}

export function getVoiceReports(appointmentId) {
  return apiClient.get(`api/appointments/${appointmentId}/voice-reports`);
}

export function getVoiceReport(appointmentId, reportId) {
  return apiClient.get(`api/appointments/${appointmentId}/voice-reports/${reportId}`);
}

export function deleteVoiceReport(appointmentId, reportId) {
  return apiClient.delete(`api/appointments/${appointmentId}/voice-reports/${reportId}`);
}
