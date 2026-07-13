import { useQuery } from "@tanstack/react-query";
import { getVoiceReports } from "@/services/client/voiceReports";

export function useVoiceReports(appointmentId) {
  return useQuery({
    queryKey: ["voiceReports", appointmentId],
    queryFn: () => getVoiceReports(appointmentId),
    enabled: Boolean(appointmentId),
  });
}
