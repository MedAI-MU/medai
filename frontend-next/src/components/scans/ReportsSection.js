"use client";

import { FileText, Trash2 } from "lucide-react";

import Heading from "@/components/ui/Heading";
import Button from "@/components/ui/Button";
import DeleteDialog from "@/components/ui/DeleteDialog";
import EmptyState from "@/components/ui/EmptyState";
import SpinnerMini from "@/components/ui/SpinnerMini";
import UploadReportSheet from "./UploadReportSheet";
import { useScanReports } from "@/hooks/scans/useScanReports";
import { API_BASE } from "@/lib/constants";
import Link from "next/link";
import ErrorState from "../ui/ErrorState";

function ReportsSection({ scanId, patientId, readOnly = false }) {
  const { reports, isLoading, isError, refetch, deleteReport } = useScanReports(
    patientId,
    scanId,
  );

  if (isLoading)
    return (
      <div className="flex justify-center py-6">
        <SpinnerMini />
      </div>
    );

  if (isError)
    return (
      <ErrorState title="Failed to load reports" onRetry={() => refetch()} />
    );

  return (
    <div className="space-y-4">
      <div className="flex items-center justify-between">
        <Heading Tag="h4" size="sm" title="Reports" />
        {!readOnly && (
          <UploadReportSheet scanId={scanId} patientId={patientId}>
            <Button size="sm">Upload Report</Button>
          </UploadReportSheet>
        )}
      </div>

      {reports?.length === 0 ? (
        <EmptyState
          heading="No Reports"
          description="No reports have been uploaded for this scan yet."
        />
      ) : (
        <div className="space-y-2">
          {reports.map((report) => {
            const fileName =
              report.path?.split("/").pop() || `report-${report.id}`;
            const fileUrl = `${API_BASE}/api/reports/${patientId}/${report.id}/file`;

            return (
              <div
                key={report.id}
                className="bg-surface-overlay flex items-center justify-between rounded-lg px-4 py-3"
              >
                <Link
                  href={fileUrl}
                  target="_blank"
                  rel="noopener noreferrer"
                  className="flex items-center gap-3 hover:underline"
                >
                  <FileText size={18} className="text-primary shrink-0" />
                  <span className="truncate text-sm">{fileName}</span>
                </Link>

                {!readOnly && (
                  <DeleteDialog
                    title="Delete Report"
                    description="Are you sure you want to delete this report? This action cannot be undone."
                    onConfirm={() => deleteReport(report.id)}
                    successMessage="Report deleted."
                    failMessage="Failed to delete report."
                  >
                    <button className="text-text-muted transition-colors hover:text-red-500">
                      <Trash2 size={16} />
                    </button>
                  </DeleteDialog>
                )}
              </div>
            );
          })}
        </div>
      )}
    </div>
  );
}

export default ReportsSection;
