import "server-only";

import { apiServer } from "@/lib/api/apiFetchServer";

export function getSecretaries() {
  return apiServer.get("api/users/secretaries");
}

export function getPendingSecretaries() {
  return apiServer.get("api/users/pending/secretaries");
}

export function getDoctors() {
  return apiServer.get("api/users/doctors");
}

export function getPendingDoctors() {
  return apiServer.get("api/users/pending/doctors");
}

export function getManagers() {
  return apiServer.get("api/users/managers");
}
