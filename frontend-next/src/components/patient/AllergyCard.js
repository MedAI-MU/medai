import { TriangleAlert } from "lucide-react";
import { deleteAllergy } from "@/services/client/patient";
import IconBadge from "@/components/ui/IconBadge";
import AllergyForm from "./AllergyForm";
import Heading from "@/components/ui/Heading";
import Card from "@/components/ui/Card";
import ActionButtons from "@/components/ui/ActionButtons";
import EditAction from "@/components/ui/EditAction";
import DeleteAction from "@/components/ui/DeleteAction";

function AllergyCard({ allergy, patientId, readOnly = false }) {
  const { name, description, id: allergyId } = allergy || {};

  return (
    <Card className="flex flex-col items-center gap-4 text-center md:flex-row md:text-start">
      <IconBadge icon={<TriangleAlert size={20} />} isRounded color="orange" />
      <Heading
        Tag="h3"
        className="flex-1 md:pr-12"
        size="sm"
        title={name}
        subtitle={description}
      />

      {!readOnly && (
        <ActionButtons>
          <EditAction
            title="Edit Allergy"
            description="Edit your allergy to be precise to help doctor know your diagnose."
            form={<AllergyForm allergyToEdit={allergy} patientId={patientId} />}
          />
          <DeleteAction
            description="This action cannot be undone. This will permanently delete allergy
            from our servers."
            onConfirm={() => deleteAllergy(patientId, allergyId)}
            successMessage="Allergy has been deleted successfully"
            failMessage="Failed to delete allergy"
          />
        </ActionButtons>
      )}
    </Card>
  );
}

export default AllergyCard;
