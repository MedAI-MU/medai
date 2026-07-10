import { apiClient } from "@/lib/api/apiFetchClient";

export async function updateProfile(id, data) {
  return apiClient.patch(`api/users/${id}`, data);
}

export async function uploadAvatar(id, file) {
  const formData = new FormData();
  formData.append("file", file);
  return apiClient.post(`api/users/${id}/avatar`, formData);
}

export async function deleteAvatar(id) {
  return apiClient.delete(`api/users/${id}/avatar`);
}
