"use client";

import { FileText, Upload } from "lucide-react";
import { SheetBody, SheetFooter } from "../shadcn/sheet";
import SheetForm from "../ui/SheetForm";
import SpinnerMini from "../ui/SpinnerMini";
import Button from "../ui/Button";
import { useRouter } from "next/navigation";
import { useRef, useState } from "react";
import toast from "react-hot-toast";
import { createReport } from "@/services/client/scans";

function UploadReportForm({ patientId, scanId, loadReports, closeSheet }) {
  const router = useRouter();
  const fileInputRef = useRef(null);
  const [file, setFile] = useState(null);
  const [isUploading, setIsUploading] = useState(false);

  function handleFileSelect(e) {
    const selected = e.target.files?.[0];
    if (selected) setFile(selected);
    e.target.value = "";
  }

  async function handleSubmit(e) {
    e.preventDefault();
    if (!file) {
      toast.error("Please select a file.");
      return;
    }

    setIsUploading(true);
    const formData = new FormData();
    formData.append("file", file);

    try {
      await createReport(scanId, patientId, formData);
      toast.success("Report uploaded successfully.");
      setFile(null);
      loadReports?.();
      closeSheet?.();
      router.refresh();
    } catch (err) {
      toast.error(err.message || "Failed to upload report.");
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
          {file ? (
            <>
              <FileText size={40} className="text-primary" />
              <p className="text-text-base text-sm font-medium">{file.name}</p>
              <p className="text-text-subtle text-xs">
                {(file.size / 1024).toFixed(1)} KB
              </p>
            </>
          ) : (
            <>
              <Upload size={40} className="text-text-muted" />
              <p className="text-text-muted text-sm">Click to select a file</p>
              <p className="text-text-subtle text-xs">PDF, DOCX, XLSX</p>
            </>
          )}
          <input
            ref={fileInputRef}
            type="file"
            accept=".pdf,.docx,.xlsx,.doc,.xls"
            className="hidden"
            onChange={handleFileSelect}
          />
        </div>
      </SheetBody>

      <SheetFooter>
        <Button type="submit" disabled={isUploading || !file}>
          {isUploading ? <SpinnerMini /> : "Upload"}
        </Button>
      </SheetFooter>
    </SheetForm>
  );
}

export default UploadReportForm;
