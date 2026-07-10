import { apiClient } from "@/lib/api/apiFetchClient";

export function createScan(patientId, formData) {
  return apiClient.post(`api/scans/${patientId}`, formData);
}

export function deleteScan(patientId, scanId) {
  return apiClient.delete(`api/scans/${patientId}/${scanId}`);
}

export function createReport(scanId, patientId, formData) {
  return apiClient.post(`api/scans/${patientId}/${scanId}/reports`, formData);
}

export function getScanReports(patientId, scanId) {
  return apiClient.get(`api/scans/${patientId}/${scanId}/reports`);
}

export function deleteReport(patientId, reportId) {
  return apiClient.delete(`api/reports/${patientId}/${reportId}`);
}
