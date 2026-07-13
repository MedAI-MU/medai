"use client";

import { Upload, Mic, FileAudio } from "lucide-react";
import { useRef, useState } from "react";
import toast from "react-hot-toast";
import { useCreateVoice } from "@/hooks/voice-reports/useCreateVoice";

import Button from "@/components/ui/Button";
import Card from "@/components/ui/Card";
import SpinnerMini from "@/components/ui/SpinnerMini";

function VoiceReportUploader({ appointmentId }) {
  const fileInputRef = useRef(null);
  const [file, setFile] = useState(null);
  const { mutate: createVoice, isPending: isCreating } =
    useCreateVoice(appointmentId);

  function handleFileSelect(e) {
    const selected = e.target.files?.[0];
    if (selected) setFile(selected);
    e.target.value = "";
  }

  function handleUpload() {
    if (!file) {
      toast.error("Please select an audio file.");
      return;
    }

    const formData = new FormData();
    formData.append("audio", file);

    createVoice(formData, {
      onSuccess: () => {
        toast.success("Voice report uploaded. Processing...");
        setFile(null);
      },
      onError: (err) => {
        toast.error(err?.message || "Failed to upload voice report.");
      },
    });
  }

  return (
    <Card className="flex items-center gap-4">
      <div className="flex size-10 shrink-0 items-center justify-center rounded-lg bg-blue-50 dark:bg-blue-900/20">
        <Mic size={18} className="text-blue-600" />
      </div>

      <div className="flex-1">
        <input
          ref={fileInputRef}
          type="file"
          accept="audio/*"
          className="hidden"
          onChange={handleFileSelect}
        />
        <button
          type="button"
          onClick={() => fileInputRef.current?.click()}
          className="cursor-pointer text-sm font-medium text-text-base transition-colors hover:text-primary"
        >
          {file ? file.name : "Choose audio file..."}
        </button>
        <p className="text-xs text-text-subtle">
          {file
            ? `${(file.size / 1024 / 1024).toFixed(1)} MB`
            : "MP3, WAV, M4A — max 50MB"}
        </p>
      </div>

      <Button size="sm" onClick={handleUpload} disabled={isCreating || !file}>
        {isCreating ? <SpinnerMini /> : <Upload size={15} />}
        {isCreating ? "Uploading..." : "Upload"}
      </Button>
    </Card>
  );
}

export default VoiceReportUploader;
