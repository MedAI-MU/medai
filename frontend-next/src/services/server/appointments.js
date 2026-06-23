import { apiServer } from "@/lib/api/apiFetchServer";

export function getUserAppointments() {
  return apiServer.get("api/appointments/me");
}
