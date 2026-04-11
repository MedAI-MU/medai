"use client";

import { useState } from "react";
import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogHeader,
  DialogTitle,
  DialogTrigger,
} from "../shadcn/dialog";

import AllergyForm from "./AllergyForm";

function AddEditAllergy({ patientId, allergyToEdit, children }) {
  const [modalOpen, setModalOpen] = useState(false);

  return (
    <Dialog open={modalOpen} onOpenChange={setModalOpen}>
      <DialogTrigger asChild>{children}</DialogTrigger>
      <DialogContent>
        <DialogHeader>
          <DialogTitle className="capitalize">Add new allergy</DialogTitle>
          <DialogDescription>
            Enter the details of the allergy to add it to your medical records.
          </DialogDescription>
        </DialogHeader>

        <AllergyForm
          patientId={patientId}
          allergyToEdit={allergyToEdit}
          closeModal={() => setModalOpen(false)}
        />
      </DialogContent>
    </Dialog>
  );
}

export default AddEditAllergy;
