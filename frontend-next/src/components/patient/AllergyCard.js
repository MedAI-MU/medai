import { Trash2, Pencil, TriangleAlert } from "lucide-react";
import ButtonIcon from "../ui/ButtonIcon";
import IconBadge from "../ui/IconBadge";
import AddEditAllergy from "./AddEditAllergy";
import DeleteDialog from "../ui/DeleteDialog";
import { deleteAllergy } from "@/services/client/patient";

function AllergyCard({ allergy, patientId }) {
  const { name, description, id: allergyId } = allergy || {};

  return (
    <div className="group border-border bg-surface hover:border-primary relative rounded-xl border p-5 shadow-sm transition-all">
      <div className="flex flex-col items-center gap-4 text-center sm:flex-row sm:text-start">
        {/* icon */}
        <IconBadge
          icon={<TriangleAlert size={20} />}
          isRounded
          color="orange"
        />

        {/* content */}
        <div className="flex-1 sm:pr-12">
          <h4 className="text-text-base text-lg font-bold capitalize">
            {name}
          </h4>
          <p className="text-text-muted mt-1 text-sm">{description}</p>
        </div>
      </div>

      {/* actions — visible on hover */}
      <div className="absolute top-4 right-4 flex gap-1 opacity-0 transition-opacity group-hover:opacity-100">
        <AddEditAllergy allergyToEdit={allergy} patientId={patientId}>
          <ButtonIcon>
            <Pencil size={16} />
          </ButtonIcon>
        </AddEditAllergy>

        <DeleteDialog
          description="This action cannot be undone. This will permanently delete allergy
            from our servers."
          successMessage="Allergy has been deleted successfully"
          failMessage="Failed to delete allergy"
          onConfirm={() => deleteAllergy(patientId, allergyId)}
        >
          <ButtonIcon className="hover:bg-danger-muted hover:text-danger">
            <Trash2 size={16} />
          </ButtonIcon>
        </DeleteDialog>
      </div>
    </div>
  );
}

export default AllergyCard;
