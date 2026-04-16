import { Pencil } from "lucide-react";
import ButtonIcon from "./ButtonIcon";
import FormDialog from "./FormDialog";

function EditAction({ title, description, form }) {
  return (
    <FormDialog title={title} description={description} form={form}>
      <ButtonIcon className="hover:text-primary">
        <Pencil size={16} />
      </ButtonIcon>
    </FormDialog>
  );
}

export default EditAction;
