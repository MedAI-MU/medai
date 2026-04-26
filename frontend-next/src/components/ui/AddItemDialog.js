import { Plus } from "lucide-react";
import Button from "./Button";
import FormDialog from "./FormDialog";

function AddItemDialog({ title, description, form, children }) {
  return (
    <FormDialog title={title} description={description} form={form}>
      <Button>
        <Plus />
        <span>{children}</span>
      </Button>
    </FormDialog>
  );
}

export default AddItemDialog;
