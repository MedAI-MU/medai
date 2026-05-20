"use client";

import FormSheet from "@/components/ui/FormSheet";
import TemplateForm from "@/components/schedule/TemplateForm";
import AddButton from "@/components/ui/AddButton";
import EditButton from "@/components/ui/EditButton";

function AddEditTemplate({ template }) {
  return (
    <FormSheet
      title="New Schedule Template"
      form={<TemplateForm templateToEdit={template} />}
    >
      {template ? (
        <EditButton aria-label={`Edit ${template?.name}`} />
      ) : (
        <AddButton>New Template</AddButton>
      )}
    </FormSheet>
  );
}

export default AddEditTemplate;
