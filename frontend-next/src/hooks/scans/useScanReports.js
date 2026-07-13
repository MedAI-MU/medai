import { useQuery, useMutation, useQueryClient } from "@tanstack/react-query";
import {
  getScanReports,
  deleteReport,
  createReport,
} from "@/services/client/scans";

export function useScanReports(patientId, scanId) {
  const queryClient = useQueryClient();
  const queryKey = ["scanReports", patientId, scanId];

  const query = useQuery({
    queryKey,
    queryFn: () => getScanReports(patientId, scanId),
    enabled: Boolean(patientId) && Boolean(scanId),
  });

  const deleteMutation = useMutation({
    mutationFn: (reportId) => deleteReport(patientId, reportId),
    onSuccess: () => queryClient.invalidateQueries({ queryKey }),
  });

  const createReportMutation = useMutation({
    mutationFn: (formData) => createReport(scanId, patientId, formData),
    onSuccess: () => queryClient.invalidateQueries({ queryKey }),
  });

  return {
    reports: query.data ?? [],
    isLoading: query.isLoading,
    refetch: query.refetch,
    isError: query.isError,
    error: query.error,
    deleteReport: deleteMutation.mutateAsync,
    isDeleting: deleteMutation.isPending,
    createReport: createReportMutation.mutate,
    isCreating: createReportMutation.isPending,
  };
}
