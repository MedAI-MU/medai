"use client";

import { useState } from "react";
import Image from "next/image";
import { Trash2 } from "lucide-react";
import { useRouter } from "next/navigation";

import Card from "@/components/ui/Card";
import DeleteDialog from "@/components/ui/DeleteDialog";
import ScanImageViewer from "./ScanImageViewer";
import ReportsSection from "./ReportsSection";
import { deleteScan } from "@/services/client/scans";
import { API_BASE } from "@/lib/constants";

function ScanDetailPanel({ scan, patientId, readOnly = false }) {
  const [viewingImage, setViewingImage] = useState(null);
  const { id: scanId, images = [] } = scan || {};

  return (
    <div className="space-y-6">
      <Card className="space-y-6">
        <div className="grid grid-cols-2 gap-3 sm:grid-cols-3 md:grid-cols-4">
          {images.map((img) => {
            const imgUrl = `${API_BASE}/api/scans/images/${patientId}/${img.id}/file`;
            return (
              <button
                key={img.id}
                onClick={() => setViewingImage(imgUrl)}
                className="bg-surface-overlay group hover:ring-primary/50 relative aspect-square cursor-pointer overflow-hidden rounded-lg transition-shadow hover:ring-2"
              >
                <Image
                  src={imgUrl}
                  alt={`Scan image ${img.id}`}
                  fill
                  unoptimized
                  sizes="(max-width: 768px) 50vw, 25vw"
                  className="object-cover transition-transform duration-200 group-hover:scale-105"
                />
              </button>
            );
          })}
        </div>

        <ReportsSection
          scanId={scanId}
          patientId={patientId}
          readOnly={readOnly}
        />

        {!readOnly && (
          <div className="border-border flex justify-end border-t pt-4">
            <DeleteDialog
              title="Delete Scan"
              description="Are you sure you want to delete this scan and all its images? This action cannot be undone."
              onConfirm={() => deleteScan(patientId, scanId)}
              successMessage="Scan deleted successfully"
              failMessage="Failed to delete scan."
            >
              <button className="text-text-muted flex items-center gap-2 text-sm transition-colors hover:text-red-500">
                <Trash2 size={16} />
                Delete this Scan
              </button>
            </DeleteDialog>
          </div>
        )}
      </Card>

      {viewingImage && (
        <ScanImageViewer
          imageUrl={viewingImage}
          imageId={scanId}
          onClose={() => setViewingImage(null)}
        />
      )}
    </div>
  );
}

export default ScanDetailPanel;
