import "server-only";

import { apiServer } from "@/lib/api/apiFetchServer";

const DAY_IN_SECONDS = 86400;

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

export function getSpecialities() {
  return apiServer.get("api/doctors/specialities");
}

export async function getTopRatedDoctors() {
  const res = await fetch(
    `${process.env.NEXT_PUBLIC_API_BASE_URL}/api/doctors/top-rated`,
    { next: { revalidate: DAY_IN_SECONDS } },
  );

  if (!res.ok) throw new Error("Failed to fetch top-rated doctors");

  return res.json();
}
