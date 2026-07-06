import "server-only";

import { apiServer } from "@/lib/api/apiFetchServer";

export function searchDoctorsByName(name) {
  return apiServer.post("api/doctors/search/name", { name });
}

export function searchDoctorsBySpeciality(speciality) {
  return apiServer.post("api/doctors/search/speciality", {
    name: speciality,
  });
}

export function getDoctorById(doctorId) {
  return apiServer.get(`api/doctors/${doctorId}`);
}
