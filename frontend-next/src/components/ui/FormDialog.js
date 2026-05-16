"use client";

import { cloneElement, useState } from "react";
import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogHeader,
  DialogTitle,
  DialogTrigger,
} from "../shadcn/dialog";

function FormDialog({ title, description, form, children }) {
  const [modalOpen, setModalOpen] = useState(false);

  return (
    <Dialog open={modalOpen} onOpenChange={setModalOpen}>
      <DialogTrigger asChild>{children}</DialogTrigger>
      <DialogContent>
        <DialogHeader>
          <DialogTitle className="capitalize">{title}</DialogTitle>
          <DialogDescription>{description}</DialogDescription>
        </DialogHeader>

        {/* Inject close modal in form */}
        {modalOpen &&
          cloneElement(form, { closeModal: () => setModalOpen(false) })}
      </DialogContent>
    </Dialog>
  );
}

export default FormDialog;
