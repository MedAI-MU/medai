"use client";

import {
  Mic,
  Square,
  Play,
  Pause,
  RotateCcw,
  Upload,
  Music,
  X,
  FileAudio,
} from "lucide-react";
import { useRef, useState, useEffect } from "react";
import toast from "react-hot-toast";
import { useCreateVoice } from "@/hooks/voice-reports/useCreateVoice";

import Button from "@/components/ui/Button";
import Card from "@/components/ui/Card";
import SpinnerMini from "@/components/ui/SpinnerMini";

function formatDuration(seconds) {
  const m = Math.floor(seconds / 60);
  const s = seconds % 60;
  return `${String(m).padStart(2, "0")}:${String(s).padStart(2, "0")}`;
}

function formatSize(bytes) {
  if (bytes < 1024 * 1024) return `${(bytes / 1024).toFixed(1)} KB`;
  return `${(bytes / 1024 / 1024).toFixed(1)} MB`;
}

function VoiceReportUploader({ appointmentId }) {
  const [mode, setMode] = useState("idle");
  const { mutate: createVoice, isPending: isCreating } =
    useCreateVoice(appointmentId);

  // --- File upload ---
  const fileInputRef = useRef(null);
  const [file, setFile] = useState(null);

  // --- Recording ---
  const [recordingDuration, setRecordingDuration] = useState(0);
  const [recordedBlob, setRecordedBlob] = useState(null);
  const [previewUrl, setPreviewUrl] = useState(null);
  const [isPlaying, setIsPlaying] = useState(false);

  const mediaRecorderRef = useRef(null);
  const audioChunksRef = useRef([]);
  const timerRef = useRef(null);
  const streamRef = useRef(null);
  const audioRef = useRef(null);

  // Keep ref in sync with previewUrl for cleanup
  const latestUrlRef = useRef(null);
  useEffect(() => {
    latestUrlRef.current = previewUrl;
  }, [previewUrl]);

  // Cleanup on unmount — capture ref values in locals
  useEffect(() => {
    const timer = timerRef.current;
    const stream = streamRef.current;
    return () => {
      if (timer) clearInterval(timer);
      if (stream) stream.getTracks().forEach((t) => t.stop());
      if (latestUrlRef.current) URL.revokeObjectURL(latestUrlRef.current);
    };
  }, []);

  // =================== FILE HANDLERS ===================

  function handleFileSelect(e) {
    const selected = e.target.files?.[0];
    if (selected) {
      setFile(selected);
      setMode("file");
    }
    e.target.value = "";
  }

  function handleRemoveFile() {
    setFile(null);
    setMode("idle");
  }

  function handleUploadFile() {
    if (!file) {
      toast.error("Please select an audio file.");
      return;
    }

    setMode("uploading");

    const formData = new FormData();
    formData.append("audio", file);

    createVoice(formData, {
      onSuccess: () => {
        toast.success("Voice report uploaded. Processing...");
        setFile(null);
        setMode("idle");
      },
      onError: (err) => {
        toast.error(err?.message || "Failed to upload voice report.");
        setMode("file");
      },
    });
  }

  // =================== RECORDING HANDLERS ===================

  async function handleStartRecording() {
    streamRef.current = await navigator.mediaDevices.getUserMedia({
      audio: true,
    });
    mediaRecorderRef.current = new MediaRecorder(streamRef.current);

    mediaRecorderRef.current.ondataavailable = (e) => {
      audioChunksRef.current.push(e.data);
    };
    mediaRecorderRef.current.onstop = handleRecordingComplete;

    mediaRecorderRef.current.start();
    setMode("recording");
    timerRef.current = setInterval(
      () => setRecordingDuration((d) => d + 1),
      1000,
    );
  }

  function handleStopRecording() {
    mediaRecorderRef.current?.stop();

    if (timerRef.current) {
      clearInterval(timerRef.current);
      timerRef.current = null;
    }
  }

  function handleRecordingComplete() {
    const mimeType = audioChunksRef.current[0]?.type || "audio/webm";
    const blob = new Blob(audioChunksRef.current, { type: mimeType });
    const url = URL.createObjectURL(blob);
    setRecordedBlob(blob);
    setPreviewUrl(url);
    setMode("recorded");
    audioChunksRef.current = [];

    streamRef.current?.getTracks().forEach((track) => track.stop());
    streamRef.current = null;
  }

  function handleReRecord() {
    if (audioRef.current) {
      audioRef.current.pause();
      audioRef.current = null;
    }
    if (previewUrl) URL.revokeObjectURL(previewUrl);
    setIsPlaying(false);
    setRecordedBlob(null);
    setPreviewUrl(null);
    setRecordingDuration(0);
    setMode("idle");
  }

  function handleUploadRecording() {
    if (!recordedBlob) return;

    setMode("uploading");

    const recordedFile = new File([recordedBlob], "voice-recording.webm", {
      type: recordedBlob.type || "audio/webm",
    });

    const formData = new FormData();
    formData.append("audio", recordedFile);

    createVoice(formData, {
      onSuccess: () => {
        toast.success("Voice report uploaded. Processing...");
        if (previewUrl) URL.revokeObjectURL(previewUrl);
        setRecordedBlob(null);
        setPreviewUrl(null);
        setRecordingDuration(0);
        if (audioRef.current) {
          audioRef.current.pause();
          audioRef.current = null;
        }
        setIsPlaying(false);
        setMode("idle");
      },
      onError: (err) => {
        toast.error(err?.message || "Failed to upload voice report.");
        setMode("recorded");
      },
    });
  }

  async function handleTogglePlayback() {
    if (!audioRef.current) return;

    if (isPlaying) {
      audioRef.current.pause();
      setIsPlaying(false);
    } else {
      try {
        await audioRef.current.play();
        setIsPlaying(true);
      } catch {
        toast.error("Audio playback failed");
      }
    }
  }

  // =================== RENDER ===================

  if (mode === "idle") {
    return (
      <Card className="flex items-center gap-4">
        <div className="flex size-10 shrink-0 items-center justify-center rounded-lg bg-blue-50 dark:bg-blue-900/20">
          <Mic size={18} className="text-blue-600" />
        </div>

        <div className="flex flex-1 items-center gap-3">
          <button
            type="button"
            onClick={handleStartRecording}
            className="flex cursor-pointer items-center gap-2 rounded-lg border border-blue-200 bg-blue-50 px-4 py-2 text-sm font-medium text-blue-700 transition-colors hover:bg-blue-100 dark:border-blue-800 dark:bg-blue-900/30 dark:text-blue-300 dark:hover:bg-blue-900/50"
          >
            <Mic size={16} />
            Record
          </button>

          <span className="text-text-subtle text-xs">or</span>

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
            className="border-border bg-surface text-text-base hover:bg-surface-overlay flex cursor-pointer items-center gap-2 rounded-lg border px-4 py-2 text-sm font-medium transition-colors"
          >
            <Upload size={16} />
            Upload File
          </button>
        </div>

        <p className="text-text-subtle shrink-0 text-xs">
          Record or upload an audio file
        </p>
      </Card>
    );
  }

  if (mode === "recording") {
    return (
      <Card className="flex items-center gap-4">
        <div className="flex size-10 shrink-0 items-center justify-center rounded-lg bg-red-50 dark:bg-red-900/20">
          <span className="relative flex size-3">
            <span className="absolute inline-flex h-full w-full animate-ping rounded-full bg-red-400 opacity-75" />
            <span className="relative inline-flex size-3 rounded-full bg-red-500" />
          </span>
        </div>

        <div className="flex-1">
          <p className="text-sm font-semibold text-red-600">
            Recording {formatDuration(recordingDuration)}
          </p>
          <p className="text-text-subtle text-xs">
            Speak clearly into the microphone
          </p>
        </div>

        <Button
          size="sm"
          variation="danger"
          onClick={handleStopRecording}
          startIcon={<Square size={14} />}
        >
          Stop
        </Button>
      </Card>
    );
  }

  if (mode === "recorded") {
    return (
      <Card className="flex items-center gap-4">
        <div className="flex size-10 shrink-0 items-center justify-center rounded-lg bg-green-50 dark:bg-green-900/20">
          <Music size={18} className="text-green-600" />
        </div>

        <div className="flex flex-1 items-center gap-3">
          {previewUrl && (
            <>
              <button
                type="button"
                onClick={handleTogglePlayback}
                className="bg-primary flex size-9 cursor-pointer items-center justify-center rounded-full text-white transition-colors hover:opacity-90"
              >
                {isPlaying ? <Pause size={16} /> : <Play size={16} />}
              </button>

              <audio
                ref={audioRef}
                src={previewUrl}
                onEnded={() => setIsPlaying(false)}
                style={{
                  position: "absolute",
                  width: 0,
                  height: 0,
                  opacity: 0,
                  pointerEvents: "none",
                }}
              />
            </>
          )}

          <div>
            <p className="text-text-base text-sm font-medium">
              Recording — {formatDuration(recordingDuration)}
            </p>
            <p className="text-text-subtle text-xs">
              {recordedBlob ? formatSize(recordedBlob.size) : ""}
            </p>
          </div>
        </div>

        <div className="flex items-center gap-2">
          <Button
            size="sm"
            variation="outline"
            onClick={handleReRecord}
            startIcon={<RotateCcw size={14} />}
          >
            Re-record
          </Button>

          <Button
            size="sm"
            variation="primary"
            onClick={handleUploadRecording}
            startIcon={<Upload size={14} />}
          >
            Upload
          </Button>
        </div>
      </Card>
    );
  }

  if (mode === "file") {
    return (
      <Card className="flex items-center gap-4">
        <div className="flex size-10 shrink-0 items-center justify-center rounded-lg bg-blue-50 dark:bg-blue-900/20">
          <FileAudio size={18} className="text-blue-600" />
        </div>

        <div className="flex-1">
          <p className="text-text-base text-sm font-medium">{file?.name}</p>
          <p className="text-text-subtle text-xs">
            {file ? formatSize(file.size) : ""}
          </p>
        </div>

        <div className="flex items-center gap-2">
          <button
            type="button"
            onClick={handleRemoveFile}
            className="text-text-muted flex size-9 cursor-pointer items-center justify-center rounded-lg transition-colors hover:bg-red-50 hover:text-red-500 dark:hover:bg-red-900/20"
          >
            <X size={16} />
          </button>

          <Button
            size="sm"
            variation="primary"
            onClick={handleUploadFile}
            startIcon={<Upload size={14} />}
          >
            Upload
          </Button>
        </div>
      </Card>
    );
  }

  if (mode === "uploading") {
    return (
      <Card className="flex items-center gap-4">
        <div className="flex size-10 shrink-0 items-center justify-center rounded-lg bg-blue-50 dark:bg-blue-900/20">
          <SpinnerMini />
        </div>

        <div className="flex-1">
          <p className="text-text-base text-sm font-medium">
            Uploading voice report...
          </p>
          <p className="text-text-subtle text-xs">This may take a moment</p>
        </div>
      </Card>
    );
  }

  return null;
}

export default VoiceReportUploader;
