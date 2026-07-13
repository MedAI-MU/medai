"use client";

import { useState } from "react";
import { Scan, ArrowLeft } from "lucide-react";

import Grid from "@/components/ui/Grid";
import Button from "@/components/ui/Button";
import EmptyState from "@/components/ui/EmptyState";
import ScanCard from "./ScanCard";
import ScanDetailPanel from "./ScanDetailPanel";
import UploadScanSheet from "./UploadScanSheet";

function ScansSection({ patientId, scans = [], readOnly = false, appointmentId }) {
  const [expandedScanId, setExpandedScanId] = useState(null);

  const expandedScan = expandedScanId
    ? scans.find((s) => s.id === expandedScanId)
    : null;

  if (expandedScan) {
    return (
      <div className="space-y-6">
        <Button
          variation="secondary"
          size="sm"
          onClick={() => setExpandedScanId(null)}
        >
          <ArrowLeft size={16} />
          Back to all scans
        </Button>
        <ScanDetailPanel
          scan={expandedScan}
          patientId={patientId}
          readOnly={readOnly}
          onClose={() => setExpandedScanId(null)}
        />
      </div>
    );
  }

  return (
    <div className="space-y-6">
      {!readOnly && (
        <div className="flex items-center justify-end">
          <UploadScanSheet patientId={patientId} appointmentId={appointmentId}>
            <Button>Upload New Scan</Button>
          </UploadScanSheet>
        </div>
      )}

      {scans.length === 0 ? (
        <EmptyState
          icon={<Scan size={30} />}
          heading="No Scans"
          description="No scans have been uploaded for this patient yet."
        />
      ) : (
        <Grid cols="two" className="md:grid-cols-3">
          {scans.map((scan) => (
            <ScanCard
              key={scan.id}
              scan={scan}
              patientId={patientId}
              onViewDetails={(id) => setExpandedScanId(id)}
            />
          ))}
        </Grid>
      )}
    </div>
  );
}

export default ScansSection;
