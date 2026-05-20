import FormDialog from "./FormDialog";
import AddButton from "./AddButton";

function AddItemDialog({ title, description, form, children }) {
  return (
    <FormDialog title={title} description={description} form={form}>
      <AddButton>{children}</AddButton>
    </FormDialog>
  );
}

export default AddItemDialog;
