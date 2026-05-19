import FormDialog from "./FormDialog";
import EditButton from "./EditButton";

function EditAction({ title, description, form }) {
  return (
    <FormDialog title={title} description={description} form={form}>
      <EditButton />
    </FormDialog>
  );
}

export default EditAction;
