"use client";

import {
  Dialog,
  DialogContent,
  DialogHeader,
  DialogTitle,
} from "@/components/shadcn/dialog";
import Image from "next/image";

function ScanImageViewer({ imageUrl, imageId, onClose }) {
  return (
    <Dialog open={!!imageUrl} onOpenChange={(open) => !open && onClose()}>
      <DialogContent
        className="sm:max-w-[95vw] sm:max-h-[95vh]"
        onInteractOutside={(e) => e.preventDefault()}
      >
        <DialogHeader>
          <DialogTitle>Scan Image #{imageId}</DialogTitle>
        </DialogHeader>
        <div className="bg-surface-overlay relative flex max-h-[85vh] min-h-[70vh] items-center justify-center overflow-hidden rounded-lg">
          {imageUrl && (
            <Image
              src={imageUrl}
              alt={`Scan ${imageId}`}
              fill
              unoptimized
              className="object-contain"
            />
          )}
        </div>
      </DialogContent>
    </Dialog>
  );
}

export default ScanImageViewer;
