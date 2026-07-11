import "server-only";

import { apiServer } from "@/lib/api/apiFetchServer";

export function getPatientDiagnoses(patientId) {
  return apiServer.get(`api/diagnosis/${patientId}`);
}
