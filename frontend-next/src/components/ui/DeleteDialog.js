import { useState } from "react";
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

  async function handleConfirm() {
    try {
      await onConfirm();
      toast.success(successMessage);
      setAlertOpen(false);
      router.refresh();
    } catch (err) {
      console.error(err.message);
      toast.error(failMessage);
    }
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
          <AlertDialogCancel>Cancel</AlertDialogCancel>
          <AlertDialogAction onClick={handleConfirm}>Delete</AlertDialogAction>
        </AlertDialogFooter>
      </AlertDialogContent>
    </AlertDialog>
  );
}

export default DeleteDialog;
