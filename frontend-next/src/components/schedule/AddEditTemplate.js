"use client";

import FormSheet from "@/components/ui/FormSheet";
import TemplateForm from "@/components/schedule/TemplateForm";

function AddEditTemplate({ children, template }) {
  return (
    <FormSheet
      title="New Schedule Template"
      form={<TemplateForm templateToEdit={template} />}
    >
      {children}
    </FormSheet>
  );
}

export default AddEditTemplate;
