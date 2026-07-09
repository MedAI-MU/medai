import { apiServer } from "@/lib/api/apiFetchServer";

export function getUserAppointments() {
  return apiServer.get("api/appointments/me");
}

export function getDoctorAppointments(doctorId) {
  return apiServer.get(`api/appointments/doctor/${doctorId}`);
}

export function getAllAppointments() {
  return apiServer.get("api/appointments");
}
