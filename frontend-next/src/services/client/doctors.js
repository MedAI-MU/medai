import { apiClient } from "@/lib/api/apiFetchClient";

export async function createSpeciality(data) {
  return apiClient.post("api/doctors/specialities", data);
}

export async function updateSpeciality(id, data) {
  return apiClient.patch(`api/doctors/specialities/${id}`, data);
}

export async function deleteSpeciality(id) {
  return apiClient.delete(`api/doctors/specialities/${id}`);
}

export async function addDoctorSpeciality(doctorId, data) {
  return apiClient.post(`api/doctors/${doctorId}/specialities`, data);
}

export async function updateDoctorSpeciality(doctorId, specialityId, data) {
  return apiClient.patch(
    `api/doctors/${doctorId}/specialities/${specialityId}`,
    data,
  );
}

export async function removeDoctorSpeciality(doctorId, specialityId) {
  return apiClient.delete(
    `api/doctors/${doctorId}/specialities/${specialityId}`,
  );
}
