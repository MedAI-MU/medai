import { useQuery } from "@tanstack/react-query";
import { getVoiceReport } from "@/services/client/voiceReports";

export function useVoiceReport(appointmentId, reportId) {
  return useQuery({
    queryKey: ["voiceReport", appointmentId, reportId],
    queryFn: () => getVoiceReport(appointmentId, reportId),
    enabled: Boolean(appointmentId) && Boolean(reportId),
    refetchInterval: (query) => {
      const status = query.state.data?.status;
      if (status === "completed" || status === "failed") return false;
      return 2000;
    },
  });
}
