"use client";

import { useRef, useState } from "react";
import Image from "next/image";
import { Camera, X } from "lucide-react";
import toast from "react-hot-toast";
import { useRouter } from "next/navigation";

import defaultAvatar from "@/assets/default-user.jpg";
import { uploadAvatar, deleteAvatar } from "@/services/client/user";
import SpinnerMini from "../ui/SpinnerMini";

export default function ProfileAvatar({ user }) {
  const router = useRouter();
  const fileInputRef = useRef(null);
  const [isUploading, setIsUploading] = useState(false);
  const [isDeleting, setIsDeleting] = useState(false);

  async function handleDelete() {
    if (!user?.avatar) return;

    setIsDeleting(true);
    try {
      await deleteAvatar(user.id);
      toast.success("Avatar removed successfully.");
      router.refresh();
    } catch (err) {
      toast.error(err.message || "Failed to remove avatar.");
    } finally {
      setIsDeleting(false);
    }
  }

  async function handleFileSelect(e) {
    const file = e.target.files?.[0];
    if (!file) return;

    setIsUploading(true);
    try {
      await uploadAvatar(user.id, file);
      toast.success("Avatar updated successfully.");
      router.refresh();
    } catch (err) {
      toast.error(err.message || "Failed to upload avatar.");
    } finally {
      setIsUploading(false);
      e.target.value = "";
    }
  }

  const isLoading = isUploading || isDeleting;

  return (
    <div className="relative">
      <div
        role="button"
        tabIndex={0}
        className="group relative size-24 shrink-0 cursor-pointer overflow-hidden rounded-full"
        onClick={() => fileInputRef.current?.click()}
        onKeyDown={(e) => {
          if (e.key === "Enter" || e.key === " ") fileInputRef.current?.click();
        }}
      >
        <Image
          src={user?.avatar || defaultAvatar}
          alt={`${user?.name}'s avatar`}
          className="h-full w-full rounded-full object-cover"
          width={96}
          height={96}
          unoptimized
        />

        {isUploading || isDeleting ? (
          <div className="absolute inset-0 flex items-center justify-center rounded-full bg-black/50">
            <SpinnerMini size={22} />
          </div>
        ) : (
          <div className="absolute inset-0 flex items-center justify-center rounded-full bg-black/0 transition-colors group-hover:bg-black/40">
            <Camera
              size={20}
              className="text-white opacity-0 transition-opacity group-hover:opacity-100"
            />
          </div>
        )}
      </div>

      {user?.avatar && (
        <button
          type="button"
          onClick={(e) => {
            e.stopPropagation();
            handleDelete();
          }}
          className="absolute -top-1 -right-1 flex size-6 items-center justify-center rounded-full bg-red-500 text-white shadow transition-colors hover:bg-red-600"
          title="Remove avatar"
          disabled={isLoading}
        >
          <X size={12} />
        </button>
      )}

      <input
        ref={fileInputRef}
        type="file"
        accept="image/jpeg,image/png,image/gif,image/webp"
        className="hidden"
        onChange={handleFileSelect}
        disabled={isLoading}
      />
    </div>
  );
}
