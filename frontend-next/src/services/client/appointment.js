import { apiClient } from "@/lib/api/apiFetchClient";

export async function createAppointment(data) {
  return apiClient.post("api/appointments", data);
}

export async function updateAppointmentStatus(appId, status) {
  return apiClient.patch(`api/appointments/${appId}/status`, { status });
}

export async function addAppointmentReview(appId, data) {
  return apiClient.patch(`api/appointments/${appId}/review`, data);
}

export async function deleteAppointment(appId) {
  return apiClient.delete(`api/appointments/${appId}`);
}

export function getDoctorAppointments(doctorId) {
  return apiClient.get(`api/appointments/doctor/${doctorId}`);
}
