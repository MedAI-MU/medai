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
} from "../shadcn/alert-dialog";
import toast from "react-hot-toast";
import { useRouter } from "next/navigation";
import SpinnerMini from "./SpinnerMini";

function DeleteDialog({
  title,
  description,
  onConfirm,
  successMessage,
  failMessage,
  children,
}) {
  const router = useRouter();
  const [alertOpen, setAlertOpen] = useState(false);
  const [isDeleting, startTransition] = useTransition();

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
            {description ?? "This action cannot be undone."}
          </AlertDialogDescription>
        </AlertDialogHeader>
        <AlertDialogFooter>
          <AlertDialogCancel disabled={isDeleting}>Cancel</AlertDialogCancel>
          <AlertDialogAction
            onClick={(e) => {
              e.preventDefault();
              handleConfirm();
            }}
            disabled={isDeleting}
          >
            {isDeleting ? <SpinnerMini /> : "Delete"}
          </AlertDialogAction>
        </AlertDialogFooter>
      </AlertDialogContent>
    </AlertDialog>
  );
}

export default DeleteDialog;
