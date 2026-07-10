"use client";

import Image from "next/image";
import { ImageIcon } from "lucide-react";

import Card from "@/components/ui/Card";
import Badge from "@/components/ui/Badge";
import Button from "@/components/ui/Button";
import { formatDate } from "@/lib/utils/DateTimeHelpers";
import { API_BASE } from "@/lib/constants";

function ScanCard({ scan, patientId, onViewDetails }) {
  const { id, images = [], createdAt } = scan || {};
  const firstImage = images?.[0];
  const imageSrc = firstImage
    ? `${API_BASE}/api/scans/images/${patientId}/${firstImage.id}/file`
    : null;

  return (
    <Card className="flex flex-col gap-4">
      <div className="bg-surface-overlay relative flex aspect-[4/3] items-center justify-center overflow-hidden rounded-lg">
        {imageSrc ? (
          <Image
            src={imageSrc}
            alt="Scan"
            fill
            unoptimized
            sizes="(max-width: 768px) 50vw, 33vw"
            className="object-cover"
          />
        ) : (
          <ImageIcon size={32} className="text-text-muted" />
        )}
      </div>

      <div className="flex items-center justify-between gap-2">
        <div className="space-y-1">
          <p className="text-text-muted text-xs">
            {createdAt ? formatDate(createdAt) : "Unknown date"}
          </p>
          <Badge
            color="slate"
            text={`${images.length} image${images.length !== 1 ? "s" : ""}`}
          />
        </div>
        <Button
          size="sm"
          variation="secondary"
          onClick={() => onViewDetails(id)}
        >
          See Details
        </Button>
      </div>
    </Card>
  );
}

export default ScanCard;
