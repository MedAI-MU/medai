import { Pencil, Trash2 } from "lucide-react";
import ButtonIcon from "./ButtonIcon";
import FormDialog from "./FormDialog";
import DeleteDialog from "./DeleteDialog";

function CardActions({
  position = "top-4 right-4",
  editTitle,
  editDescription,
  editForm,
  deleteTitle,
  deleteDescription,
  deleteSuccessMessage,
  deleteFailMessage,
  onConfirmDelete,
}) {
  return (
    <div className={`absolute ${position} flex gap-1 opacity-100`}>
      {editForm && (
        <FormDialog
          title={editTitle}
          description={editDescription}
          form={editForm}
        >
          <ButtonIcon>
            <Pencil size={16} />
          </ButtonIcon>
        </FormDialog>
      )}

      {onConfirmDelete && (
        <DeleteDialog
          title={deleteTitle}
          description={deleteDescription}
          successMessage={deleteSuccessMessage}
          failMessage={deleteFailMessage}
          onConfirm={onConfirmDelete}
        >
          <ButtonIcon className="hover:bg-danger-muted hover:text-danger">
            <Trash2 size={16} />
          </ButtonIcon>
        </DeleteDialog>
      )}
    </div>
  );
}

export default CardActions;
