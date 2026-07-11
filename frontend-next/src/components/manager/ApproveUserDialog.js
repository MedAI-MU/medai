"use client";

import { useState, useTransition } from "react";
import {
  AlertDialog,
  AlertDialogAction,
  AlertDialogCancel,
  AlertDialogContent,
  AlertDialogDescription,
  AlertDialogFooter,
  AlertDialogHeader,
  AlertDialogTitle,
  AlertDialogTrigger,
} from "@/components/shadcn/alert-dialog";
import toast from "react-hot-toast";
import { useRouter } from "next/navigation";
import SpinnerMini from "@/components/ui/SpinnerMini";

export default function ApproveUserDialog({
  title,
  description,
  onConfirm,
  successMessage,
  failMessage,
  confirmLabel = "Approve",
  cancelLabel = "Cancel",
  children,
}) {
  const router = useRouter();
  const [alertOpen, setAlertOpen] = useState(false);
  const [isApproving, startTransition] = useTransition();

  function handleConfirm() {
    startTransition(async () => {
      try {
        await onConfirm();
        setAlertOpen(false);
        toast.success(successMessage);
        router.refresh();
      } catch (err) {
        console.error(err.message);
        toast.error(failMessage);
      }
    });
  }

  return (
    <AlertDialog open={alertOpen} onOpenChange={setAlertOpen}>
      <AlertDialogTrigger asChild>{children}</AlertDialogTrigger>
      <AlertDialogContent>
        <AlertDialogHeader>
          <AlertDialogTitle>{title || "Are you sure?"}</AlertDialogTitle>
          <AlertDialogDescription>
            {description ?? "This action will approve the user."}
          </AlertDialogDescription>
        </AlertDialogHeader>
        <AlertDialogFooter>
          <AlertDialogCancel disabled={isApproving}>
            {cancelLabel}
          </AlertDialogCancel>
          <AlertDialogAction
            variation="primary"
            onClick={(e) => {
              e.preventDefault();
              handleConfirm();
            }}
            disabled={isApproving}
          >
            {isApproving ? <SpinnerMini /> : confirmLabel}
          </AlertDialogAction>
        </AlertDialogFooter>
      </AlertDialogContent>
    </AlertDialog>
  );
}
