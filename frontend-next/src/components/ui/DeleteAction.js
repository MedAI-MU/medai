import { Trash2 } from "lucide-react";
import ButtonIcon from "./ButtonIcon";
import DeleteDialog from "./DeleteDialog";
import DeleteButtonIcon from "./DeleteButtonIcon";

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
      <DeleteButtonIcon />
    </DeleteDialog>
  );
}

export default DeleteAction;
