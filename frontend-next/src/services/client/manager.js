import { apiClient } from "@/lib/api/apiFetchClient";

export async function promoteToDoctor(userId) {
  return apiClient.post("api/users/add/doctor", { userId });
}

export async function promoteToSecretary(userId) {
  return apiClient.post("api/users/add/secretary", { userId });
}

export async function deleteUser(userId) {
  return apiClient.delete(`api/users/${userId}`);
}
