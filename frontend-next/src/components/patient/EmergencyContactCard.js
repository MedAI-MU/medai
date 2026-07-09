import { Phone, Mail, MapPin } from "lucide-react";
import Card from "@/components/ui/Card";
import Badge from "@/components/ui/Badge";
import ActionButtons from "@/components/ui/ActionButtons";
import EditAction from "@/components/ui/EditAction";
import DeleteAction from "@/components/ui/DeleteAction";
import Heading from "@/components/ui/Heading";
import Button from "@/components/ui/Button";
import InfoRow from "@/components/ui/InfoRow";
import EmergencyContactForm from "./EmergencyContactForm";
import { deleteEmergencyContact } from "@/services/client/patient";

function EmergencyContactCard({ contact, patientId, readOnly = false }) {
  const { id, name, relation, phoneNumber, email, address, notes } =
    contact || {};

  return (
    <Card className="flex flex-col overflow-hidden p-0">
      <div className="flex-1 space-y-4 p-5">
        <div className="flex items-start justify-between">
          <div>
            <Heading title={name} size="sm" Tag="h4" />
            <Badge text={relation} color="blue" className="mt-1 inline-block" />
          </div>

          {!readOnly && (
            <ActionButtons absolute={false}>
              <EditAction
                title="Edit Emergency Contact"
                description="Update the contact's details."
                form={
                  <EmergencyContactForm
                    patientId={patientId}
                    contactToEdit={contact}
                  />
                }
              />
              <DeleteAction
                title="Delete Contact"
                description="This action cannot be undone."
                onConfirm={() => deleteEmergencyContact(patientId, id)}
                successMessage="Contact deleted"
                failMessage="Failed to delete"
              />
            </ActionButtons>
          )}
        </div>

        <div className="space-y-2">
          {phoneNumber && <InfoRow icon={Phone} value={phoneNumber} />}
          {email && <InfoRow icon={Mail} value={email} />}
          {address && <InfoRow icon={MapPin} value={address} />}
        </div>

        {notes && (
          <div className="border-border border-t pt-2">
            <p className="text-text-subtle mb-1 text-xs font-semibold tracking-wider uppercase">
              Notes
            </p>
            <p className="text-text-muted text-sm italic">
              &quot;{notes}&quot;
            </p>
          </div>
        )}
      </div>

      <div className="border-border bg-surface-overlay/30 flex gap-2 border-t p-3">
        <Button
          href={`tel:${phoneNumber}`}
          variation="green"
          className="flex-1"
        >
          <Phone size={18} />
          Call
        </Button>
        <Button href={`mailto:${email}`} className="flex-1">
          <Mail size={18} />
          Email
        </Button>
      </div>
    </Card>
  );
}

export default EmergencyContactCard;
