import { TriangleAlert } from "lucide-react";
import { deleteAllergy } from "@/services/client/patient";
import IconBadge from "@/components/ui/IconBadge";
import AllergyForm from "./AllergyForm";
import Heading from "@/components/ui/Heading";
import CardActions from "@/components/ui/CardActions";
import Card from "@/components/ui/Card";

function AllergyCard({ allergy, patientId }) {
  const { name, description, id: allergyId } = allergy || {};

  return (
    <Card className="flex flex-col items-center gap-4 text-center sm:flex-row sm:text-start">
      {/* icon */}
      <IconBadge icon={<TriangleAlert size={20} />} isRounded color="orange" />
      <Heading
        Tag="h3"
        className="flex-1 sm:pr-12"
        size="sm"
        title={name}
        subtitle={description}
      />

      {/* actions — visible on hover */}
      <CardActions
        editTitle="Edit Allergy"
        editDescription="Edit your allergy to be precise to help doctor know your diagnose."
        editForm={<AllergyForm allergyToEdit={allergy} patientId={patientId} />}
        deleteDescription="This action cannot be undone. This will permanently delete allergy
            from our servers."
        onConfirmDelete={() => deleteAllergy(patientId, allergyId)}
        deleteSuccessMessage="Allergy has been deleted successfully"
        deleteFailMessage="Failed to delete allergy"
      />
    </Card>
  );
}

export default AllergyCard;
