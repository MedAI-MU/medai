"use client";

import { Upload, X } from "lucide-react";
import { SheetBody, SheetFooter } from "../shadcn/sheet";
import SheetForm from "@/components/ui/SheetForm";
import SpinnerMini from "../ui/SpinnerMini";
import Button from "@/components/ui/Button";
import ButtonIcon from "@/components/ui/ButtonIcon";
import { useRouter } from "next/navigation";
import { useRef, useState } from "react";
import toast from "react-hot-toast";
import { createScan } from "@/services/client/scans";

function UploadScanForm({ patientId, closeSheet, appointmentId }) {
  const router = useRouter();
  const fileInputRef = useRef(null);
  const [files, setFiles] = useState([]);
  const [isUploading, setIsUploading] = useState(false);

  function handleFileSelect(e) {
    const selected = Array.from(e.target.files || []);
    setFiles((prev) => [...prev, ...selected]);
    e.target.value = "";
  }

  function removeFile(index) {
    setFiles((prev) => prev.filter((_, i) => i !== index));
  }

  async function handleSubmit(e) {
    e.preventDefault();
    if (files.length === 0) {
      toast.error("Please select at least one image.");
      return;
    }

    setIsUploading(true);
    const formData = new FormData();
    files.forEach((file) => formData.append("images", file));
    if (appointmentId) formData.append("appointmentId", appointmentId);

    try {
      await createScan(patientId, formData);
      toast.success("Scan uploaded successfully.");
      setFiles([]);
      closeSheet?.();
      router.refresh();
    } catch (err) {
      toast.error(err.message || "Failed to upload scan.");
    } finally {
      setIsUploading(false);
    }
  }

  return (
    <SheetForm onSubmit={handleSubmit}>
      <SheetBody>
        <div
          role="button"
          tabIndex={0}
          className="border-border hover:bg-surface-overlay flex cursor-pointer flex-col items-center gap-3 rounded-xl border-2 border-dashed p-10 transition-colors"
          onClick={() => fileInputRef.current?.click()}
          onKeyDown={(e) => {
            if (e.key === "Enter" || e.key === " ") {
              fileInputRef.current?.click();
            }
          }}
        >
          <Upload size={40} className="text-text-muted" />
          <p className="text-text-muted text-sm">Click to select</p>
          <p className="text-text-subtle text-xs">JPG, PNG, DICOM</p>
          <input
            ref={fileInputRef}
            type="file"
            multiple
            accept="image/*,.dcm"
            className="hidden"
            onChange={handleFileSelect}
          />
        </div>

        {files.length > 0 && (
          <div className="space-y-2">
            <p className="text-text-muted text-xs font-medium uppercase">
              Selected files ({files.length})
            </p>
            {files.map((file, idx) => (
              <div
                key={`${file.name}-${idx}`}
                className="bg-surface-overlay flex items-center justify-between rounded-lg px-3 py-2"
              >
                <span className="truncate text-sm">{file.name}</span>
                <ButtonIcon
                  onClick={() => removeFile(idx)}
                  className="text-text-muted hover:text-text-base shrink-0"
                >
                  <X size={14} />
                </ButtonIcon>
              </div>
            ))}
          </div>
        )}
      </SheetBody>

      <SheetFooter>
        <Button type="submit" disabled={isUploading || files.length === 0}>
          {isUploading ? <SpinnerMini /> : "Upload"}
        </Button>
      </SheetFooter>
    </SheetForm>
  );
}

export default UploadScanForm;
