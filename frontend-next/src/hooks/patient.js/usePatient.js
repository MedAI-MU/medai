import { useQuery } from "@tanstack/react-query";
import { getPatientById } from "@/services/client/patient";

export function usePatient(patientId) {
  return useQuery({
    queryKey: ["patient", patientId],
    queryFn: () => getPatientById(patientId),
    enabled: Boolean(patientId),
  });
}
