import { useMutation, useQueryClient } from "@tanstack/react-query";
import { uploadAudio } from "@/services/client/voiceReports";

export function useCreateVoice(appointmentId) {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: (formData) => uploadAudio(appointmentId, formData),
    onSuccess: () => {
      queryClient.invalidateQueries({
        queryKey: ["voiceReports", appointmentId],
      });
    },
    retry: 1,
  });
}
