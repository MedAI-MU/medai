import "server-only";

import { apiServer } from "@/lib/api/apiFetchServer";

export function getAllPatients() {
  return apiServer.get("api/patients");
}

export function getPatientById(id) {
  return apiServer.get(`api/patients/${id}`);
}
