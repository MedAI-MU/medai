"use client";

import { Play, Pause } from "lucide-react";
import { useRef, useState, useEffect } from "react";

function formatTime(seconds) {
  if (!Number.isFinite(seconds)) return "0:00";
  const m = Math.floor(seconds / 60);
  const s = Math.floor(seconds % 60);
  return `${m}:${s.toString().padStart(2, "0")}`;
}

function CustomAudioPlayer({ src }) {
  const audioRef = useRef(null);
  const progressRef = useRef(null);
  const [playing, setPlaying] = useState(false);
  const [current, setCurrent] = useState(0);
  const [duration, setDuration] = useState(0);

  useEffect(() => {
    const el = audioRef.current;
    if (!el) return;

    function onLoaded() {
      setDuration(el.duration);
      setCurrent(el.currentTime);
    }
    function onTimeUpdate() {
      setCurrent(el.currentTime);
    }
    function onEnded() {
      setPlaying(false);
      setCurrent(0);
    }
    function onPlay() {
      setPlaying(true);
    }
    function onPause() {
      setPlaying(false);
    }

    el.addEventListener("loadedmetadata", onLoaded);
    el.addEventListener("timeupdate", onTimeUpdate);
    el.addEventListener("ended", onEnded);
    el.addEventListener("play", onPlay);
    el.addEventListener("pause", onPause);

    return () => {
      el.removeEventListener("loadedmetadata", onLoaded);
      el.removeEventListener("timeupdate", onTimeUpdate);
      el.removeEventListener("ended", onEnded);
      el.removeEventListener("play", onPlay);
      el.removeEventListener("pause", onPause);
    };
  }, []);

  const togglePlay = () => {
    const el = audioRef.current;
    if (!el) return;
    if (el.paused) {
      el.play().catch(() => {});
    } else {
      el.pause();
    }
  };

  function seek(e) {
    const rect = progressRef.current?.getBoundingClientRect();
    if (!rect || !audioRef.current) return;
    const ratio = (e.clientX - rect.left) / rect.width;
    audioRef.current.currentTime = ratio * duration;
  }

  const pct = duration > 0 ? (current / duration) * 100 : 0;

  return (
    <div className="flex items-center gap-2 max-sm:w-full">
      <audio className="hidden" ref={audioRef} preload="metadata">
        <source src={src} />
      </audio>

      <button
        onClick={togglePlay}
        className="bg-primary hover:bg-primary-hover flex size-8 shrink-0 items-center justify-center rounded-full text-white transition-colors"
        title={playing ? "Pause" : "Play"}
      >
        {playing ? (
          <Pause size={13} fill="currentColor" />
        ) : (
          <Play size={13} fill="currentColor" className="ml-0.5" />
        )}
      </button>

      <div className="flex min-w-0 flex-1 items-center gap-2">
        <div
          ref={progressRef}
          onClick={seek}
          className="bg-surface-overlay relative h-1.5 w-14 flex-1 cursor-pointer overflow-hidden rounded-full"
        >
          <div
            className="bg-primary absolute inset-y-0 left-0 rounded-full transition-[width] duration-150"
            style={{ width: `${pct}%` }}
          />
        </div>

        <span className="text-text-muted shrink-0 text-right font-mono text-[11px] tabular-nums">
          {formatTime(current)} / {formatTime(duration)}
        </span>
      </div>
    </div>
  );
}

export default CustomAudioPlayer;
