import "server-only";

import { apiServer } from "@/lib/api/apiFetchServer";

export function getPatientScans(patientId) {
  return apiServer.get(`api/scans/${patientId}`);
}

export function getScanReports(patientId, scanId) {
  return apiServer.get(`api/scans/${patientId}/${scanId}/reports`);
}

export function getPatientReports(patientId) {
  return apiServer.get(`api/reports/patient/${patientId}`);
}

export function getScanDetail(patientId, scanId) {
  return apiServer.get(`api/scans/${patientId}/${scanId}`);
}
