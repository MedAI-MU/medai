"use client";

import { FileAudio } from "lucide-react";
import EmptyState from "@/components/ui/EmptyState";
import SpinnerMini from "@/components/ui/SpinnerMini";
import ErrorState from "@/components/ui/ErrorState";
import Heading from "@/components/ui/Heading";
import { useVoiceReports } from "@/hooks/voice-reports/useVoiceReports";
import VoiceReportCard from "./VoiceReportCard";

function VoiceReportList({ appointmentId }) {
  const { data: reports, isLoading, isError, refetch } =
    useVoiceReports(appointmentId);

  if (isLoading)
    return (
      <div className="flex justify-center py-10">
        <SpinnerMini />
      </div>
    );

  if (isError)
    return (
      <ErrorState
        title="Failed to load voice reports"
        onRetry={() => refetch()}
      />
    );

  if (!reports?.length)
    return (
      <EmptyState
        icon={<FileAudio size={28} />}
        heading="No Voice Reports"
        description="Upload an audio recording to generate a clinical report."
      />
    );

  return (
    <div>
      <Heading
        Tag="h4"
        size="sm"
        title={`Voice Reports (${reports.length})`}
        subtitle="Recorded audio transcribed and analyzed by AI"
      />
      <div className="mt-3 space-y-3">
        {reports.map((report) => (
          <VoiceReportCard
            key={report.id}
            report={report}
            appointmentId={appointmentId}
          />
        ))}
      </div>
    </div>
  );
}

export default VoiceReportList;
