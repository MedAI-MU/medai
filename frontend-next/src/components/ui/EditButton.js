import { Pencil } from "lucide-react";
import ButtonIcon from "./ButtonIcon";

function EditButton({ ...props }) {
  return (
    <ButtonIcon className="hover:text-primary hover:bg-primary/10" {...props}>
      <Pencil size={16} />
    </ButtonIcon>
  );
}

export default EditButton;
