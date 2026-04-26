import { User, Users, Baby, Heart, HelpCircle } from "lucide-react";
import Card from "@/components/ui/Card";
import IconBadge from "@/components/ui/IconBadge";
import Heading from "@/components/ui/Heading";
import ActionButtons from "@/components/ui/ActionButtons";
import EditAction from "@/components/ui/EditAction";
import DeleteAction from "@/components/ui/DeleteAction";
import Badge from "../ui/Badge";
import FamilyHistoryForm from "./FamilyHistoryForm";
import { deleteFamilyHistory } from "@/services/client/patient";

const RELATION_ICONS = {
  father: <User size={20} />,
  mother: <User size={20} />,
  sibling: <Users size={20} />,
  child: <Baby size={20} />,
  spouse: <Heart size={20} />,
  other: <HelpCircle size={20} />,
};

function FamilyHistoryCard({ record, patientId }) {
  const { relation, condition, notes, id: recordId } = record || {};

  const icon = RELATION_ICONS[relation?.toLowerCase()] || <User size={20} />;

  return (
    <Card className="flex flex-col items-center gap-4 text-center md:flex-row md:items-start md:text-start">
      <IconBadge icon={icon} color="blue" />

      <div>
        <Badge text={relation} color="slate" />
        <Heading
          Tag="h3"
          className="mt-2 flex-1 md:pr-12"
          size="sm"
          title={condition}
          subtitle={
            <>
              <strong className="text-text-base">Notes: </strong>
              {notes ? (
                notes
              ) : (
                <span className="text-text-subtle italic">
                  No additional notes provided.
                </span>
              )}
            </>
          }
        />
      </div>

      <ActionButtons>
        <EditAction
          title="Edit Family History"
          description="Update the family history record."
          form={
            <FamilyHistoryForm patientId={patientId} recordToEdit={record} />
          }
        />
        <DeleteAction
          title="Delete Record"
          description="This action cannot be undone."
          onConfirm={() => deleteFamilyHistory(patientId, recordId)}
          successMessage="Record deleted"
          failMessage="Failed to delete"
        />
      </ActionButtons>
    </Card>
  );
}

export default FamilyHistoryCard;
