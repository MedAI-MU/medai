import Heading from "@/components/ui/Heading";
import Grid from "@/components/ui/Grid";
import EmptyState from "@/components/ui/EmptyState";
import AddItemDialog from "@/components/ui/AddItemDialog";
import EmergencyContactCard from "./EmergencyContactCard";
import EmergencyContactForm from "./EmergencyContactForm";
function EmergencyContactsTab({ data }) {
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
        <AddItemDialog
          title="Add emergency contact"
          description="Enter emergency contact details so medical staff can reach them quickly."
          form={<EmergencyContactForm patientId={userId} />}
        >
          Add Contact
        </AddItemDialog>
      </Heading>

      {!hasContacts ? (
        <EmptyState
          title="No Emergency Contacts Recorded"
          description="Adding emergency contacts ensures your loved ones are informed quickly in critical situations."
        />
      ) : (
        <Grid cols="two">
          {emergencyContacts.map((contact) => (
            <EmergencyContactCard
              key={contact.id}
              contact={contact}
              patientId={userId}
            />
          ))}
        </Grid>
      )}
    </>
  );
}

export default EmergencyContactsTab;
