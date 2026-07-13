import { useMutation, useQueryClient } from "@tanstack/react-query";
import { deleteVoiceReport } from "@/services/client/voiceReports";

export function useDeleteVoice(appointmentId) {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: (reportId) => deleteVoiceReport(appointmentId, reportId),
    onSuccess: () => {
      queryClient.invalidateQueries({
        queryKey: ["voiceReports", appointmentId],
      });
    },
  });
}
