import { Trash2 } from "lucide-react";
import ButtonIcon from "./ButtonIcon";
import DeleteDialog from "./DeleteDialog";

function DeleteAction({
  title,
  description,
  onConfirm,
  successMessage,
  failMessage,
}) {
  return (
    <DeleteDialog
      title={title}
      description={description}
      onConfirm={onConfirm}
      successMessage={successMessage}
      failMessage={failMessage}
    >
      <ButtonIcon className="hover:bg-danger-muted text-danger">
        <Trash2 size={16} />
      </ButtonIcon>
    </DeleteDialog>
  );
}

export default DeleteAction;
