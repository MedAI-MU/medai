"use client";

import {
  CheckCircle2,
  Clock,
  XCircle,
  Loader2,
  Trash2,
  ChevronDown,
  ChevronUp,
} from "lucide-react";
import { useState } from "react";
import { format } from "date-fns";
import Badge from "@/components/ui/Badge";
import Card from "@/components/ui/Card";
import DeleteDialog from "@/components/ui/DeleteDialog";
import { useVoiceReport } from "@/hooks/voice-reports/useVoiceReport";
import { useDeleteVoice } from "@/hooks/voice-reports/useDeleteVoice";
import CustomAudioPlayer from "@/components/ui/CustomAudioPlayer";
import ErrorMessage from "../ui/ErrorMessage";
import Table from "@/components/ui/Table";
import { AnimatePresence, motion } from "framer-motion";

const statusMeta = {
  queued: {
    label: "Queued",
    icon: Clock,
    badge: "amber",
    iconColor: "text-amber-600",
    iconBg: "bg-amber-50 dark:bg-amber-900/20",
    border: "border-l-amber-500",
  },
  processing: {
    label: "Processing",
    icon: Loader2,
    badge: "blue",
    iconColor: "text-blue-600",
    iconBg: "bg-blue-50 dark:bg-blue-900/20",
    border: "border-l-blue-500",
  },
  completed: {
    label: "Completed",
    icon: CheckCircle2,
    badge: "green",
    iconColor: "text-green-600",
    iconBg: "bg-green-50 dark:bg-green-900/20",
    border: "border-l-green-500",
  },
  failed: {
    label: "Failed",
    icon: XCircle,
    badge: "red",
    iconColor: "text-red-600",
    iconBg: "bg-red-50 dark:bg-red-900/20",
    border: "border-l-red-500",
  },
};

function formatKey(key) {
  return key
    .replace(/([A-Z])/g, " $1")
    .replace(/^./, (s) => s.toUpperCase())
    .trim();
}

function ClinicalTable({ data }) {
  const rows = Array.isArray(data) ? data : [];
  if (!rows.length) return null;

  return (
    <Table columns="1fr 1fr">
      <Table.Header>
        <div>Clinical Term</div>
        <div>التوصيف</div>
      </Table.Header>
      <Table.Body
        data={rows}
        render={(item, i) => (
          <Table.Row key={i}>
            <div className="text-sm font-medium capitalize">
              {item.clinical_term}
            </div>
            <div className="text-sm">{item.description_ar}</div>
          </Table.Row>
        )}
      />
    </Table>
  );
}

function ClinicalReportView({ data }) {
  const entries =
    data && typeof data === "object" ? Object.entries(data) : null;

  if (!entries) return null;

  return (
    <div className="space-y-4">
      {entries.map(([key, value]) => {
        if (
          Array.isArray(value) &&
          value.length > 0 &&
          value[0]?.clinical_term
        ) {
          return (
            <div key={key}>
              <h6 className="text-text-muted mb-2 text-[11px] font-semibold tracking-wider uppercase">
                {formatKey(key)}
              </h6>
              <ClinicalTable data={value} />
            </div>
          );
        }

        return (
          <div
            key={key}
            className="border-border/50 overflow-hidden rounded-lg border"
          >
            <div className="border-border/50 bg-surface-overlay/50 text-text-muted border-b px-3 py-1.5 text-[11px] font-semibold tracking-wider uppercase">
              {formatKey(key)}
            </div>
            <div className="bg-surface px-3 py-2 text-sm leading-relaxed">
              {typeof value === "object" && value !== null ? (
                <pre className="font-mono text-xs leading-relaxed">
                  {JSON.stringify(value, null, 2)}
                </pre>
              ) : (
                String(value)
              )}
            </div>
          </div>
        );
      })}
    </div>
  );
}

function ProcessingIndicator() {
  return (
    <div className="mt-3 flex items-center gap-2.5">
      <span className="relative flex size-2.5">
        <span className="absolute inline-flex h-full w-full animate-ping rounded-full bg-blue-400 opacity-75" />
        <span className="relative inline-flex size-2.5 rounded-full bg-blue-500" />
      </span>
      <span className="text-xs font-medium text-blue-600">
        Analyzing audio recording...
      </span>
    </div>
  );
}

function VoiceReportCard({ report, appointmentId }) {
  const [expanded, setExpanded] = useState(false);
  const { data: detail } = useVoiceReport(appointmentId, report.id);
  const { mutateAsync: deleteVoice, isPending: isDeleting } =
    useDeleteVoice(appointmentId);

  const status = detail?.status || report.status;
  const meta = statusMeta[status] || statusMeta.queued;
  const StatusIcon = meta.icon;
  const isComplete = status === "completed";
  const isFailed = status === "failed";
  const isProcessing = status === "processing";

  const createdAt = report.createdAt
    ? format(new Date(report.createdAt), "MMM d, yyyy • hh:mm a")
    : null;

  return (
    <Card
      className={`border-l-[3px] ${meta.border} transition-all hover:shadow-md`}
    >
      <div className="flex flex-col items-start justify-between gap-3 sm:flex-row">
        <div className="flex min-w-0 flex-1 items-start gap-3">
          <div
            className={`${meta.iconBg} flex size-10 shrink-0 items-center justify-center rounded-lg`}
          >
            <StatusIcon
              size={18}
              className={`${meta.iconColor} ${isProcessing ? "animate-spin" : ""}`}
            />
          </div>

          <div className="min-w-0 flex-1">
            <div className="flex items-center gap-2">
              <span className="text-text-base truncate text-sm font-semibold">
                Voice Report
              </span>
              <Badge
                color={meta.badge}
                text={meta.label}
                isRounded
                className="shrink-0 text-[10px]"
              />
            </div>
            <div className="text-text-muted mt-0.5 flex items-center gap-1.5 text-xs">
              <span>#{report.id}</span>
              {createdAt && (
                <>
                  <span className="text-border/60">•</span>
                  <span className="truncate">{createdAt}</span>
                </>
              )}
            </div>
          </div>
        </div>

        <div className="flex shrink-0 items-center gap-2 max-sm:w-full">
          {detail?.audioUrl && <CustomAudioPlayer src={detail.audioUrl} />}

          <div className="flex items-center gap-2">
            <DeleteDialog
              title="Delete Voice Report"
              description="Are you sure you want to delete this voice report?"
              onConfirm={() => deleteVoice(report.id)}
              successMessage="Voice report deleted."
              failMessage="Failed to delete voice report."
            >
              <button
                disabled={isDeleting}
                className="text-text-muted flex size-9 items-center justify-center rounded-lg transition-colors hover:bg-red-50 hover:text-red-500 dark:hover:bg-red-900/20"
              >
                <Trash2 size={15} />
              </button>
            </DeleteDialog>

            {isComplete && (
              <button
                onClick={() => setExpanded((p) => !p)}
                className="text-text-muted hover:bg-surface-overlay hover:text-text-base flex size-9 items-center justify-center rounded-lg transition-colors"
              >
                {expanded ? <ChevronUp size={17} /> : <ChevronDown size={17} />}
              </button>
            )}
          </div>
        </div>
      </div>

      {isProcessing && <ProcessingIndicator />}
      {isFailed && detail?.errorMessage && (
        <ErrorMessage
          message={detail.errorMessage}
          className="mt-3 p-3 font-medium"
          withBg
        />
      )}

      <AnimatePresence initial={false}>
        {expanded && detail && (
          <motion.div
            initial={{ height: 0, opacity: 0 }}
            animate={{ height: "auto", opacity: 1 }}
            exit={{ height: 0, opacity: 0 }}
            transition={{
              duration: 0.3,
              ease: "easeInOut",
            }}
            className="overflow-hidden"
          >
            <motion.div
              initial={{ y: -8, opacity: 0 }}
              animate={{ y: 0, opacity: 1 }}
              exit={{ y: -8, opacity: 0 }}
              transition={{
                duration: 0.2,
                delay: 0.05,
              }}
              className="border-border mt-4 space-y-5 border-t pt-4"
            >
              {detail?.transcription && (
                <section>
                  <h5 className="text-text-muted mb-2 text-[11px] font-bold tracking-widest uppercase">
                    Transcription
                  </h5>

                  <div className="border-border/50 bg-surface-overlay/30 rounded-lg border p-3.5 text-sm leading-relaxed">
                    {detail.transcription}
                  </div>
                </section>
              )}

              {detail?.clinicalReport && (
                <section>
                  <h5 className="text-text-muted mb-2 text-[11px] font-bold tracking-widest uppercase">
                    Clinical Report
                  </h5>

                  <ClinicalReportView data={detail.clinicalReport} />
                </section>
              )}
            </motion.div>
          </motion.div>
        )}
      </AnimatePresence>
    </Card>
  );
}

export default VoiceReportCard;
