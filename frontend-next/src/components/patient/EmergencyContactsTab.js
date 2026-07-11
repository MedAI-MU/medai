import Heading from "@/components/ui/Heading";
import Grid from "@/components/ui/Grid";
import EmptyState from "@/components/ui/EmptyState";
import AddItemDialog from "@/components/ui/AddItemDialog";
import EmergencyContactCard from "./EmergencyContactCard";
import EmergencyContactForm from "./EmergencyContactForm";
function EmergencyContactsTab({ data, readOnly = false }) {
  const { emergencyContacts = [], userId } = data || {};

  const hasContacts = emergencyContacts?.length > 0;

  return (
    <>
      <Heading
        size="lg"
        title="Emergency Contacts"
        Tag="h2"
        subtitle="Trusted individuals to call in case of emergencies"
        hideSubtitleOnMobile
        rowOnMobile
      >
        {!readOnly && (
          <AddItemDialog
            title="Add emergency contact"
            description="Enter emergency contact details so medical staff can reach them quickly."
            form={<EmergencyContactForm patientId={userId} />}
          >
            Add Contact
          </AddItemDialog>
        )}
      </Heading>

      {!hasContacts ? (
        <EmptyState
          title="No Emergency Contacts Recorded"
          description={
            readOnly
              ? "This patient has no emergency contacts recorded."
              : "Adding emergency contacts ensures your loved ones are informed quickly in critical situations."
          }
        />
      ) : (
        <Grid cols="two">
          {emergencyContacts.map((contact) => (
            <EmergencyContactCard
              key={contact.id}
              contact={contact}
              patientId={userId}
              readOnly={readOnly}
            />
          ))}
        </Grid>
      )}
    </>
  );
}

export default EmergencyContactsTab;
