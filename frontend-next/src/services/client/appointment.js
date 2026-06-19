import { apiClient } from "@/lib/api/apiFetchClient";

export async function createAppointment(data) {
  return apiClient.post("api/appointments", data);
}
