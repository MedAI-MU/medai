import ActionButtons from "@/components/ui/ActionButtons";
import AddItemDialog from "@/components/ui/AddItemDialog";
import EditAction from "@/components/ui/EditAction";
import EmptyState from "@/components/ui/EmptyState";
import Heading from "@/components/ui/Heading";
import Timeline from "@/components/ui/Timeline";
import TimelineCard from "@/components/ui/TimelineCard";
import TimelineItem from "@/components/ui/TimelineItem";
import DoctorDiagnosisForm from "@/components/diagnosis/DoctorDiagnosisForm";
import DeleteAction from "@/components/ui/DeleteAction";
import { deleteDiagnosis } from "@/services/client/diagnosis";

export default function DiagnosesSection({
  diagnoses,
  patientUserId,
  appointmentId,
  canCreate,
}) {
  return (
    <div className="space-y-6">
      {canCreate && (
        <div>
          <Heading
            size="lg"
            title="Add Diagnosis"
            subtitle="Record your diagnosis for this appointment."
            className="mb-4"
          >
            <AddItemDialog
              title="Add Diagnosis"
              description="Record your diagnosis for this patient."
              form={
                <DoctorDiagnosisForm
                  patientUserId={patientUserId}
                  appointmentId={appointmentId}
                />
              }
            >
              Add Diagnosis
            </AddItemDialog>
          </Heading>
        </div>
      )}

      <div>
        {!diagnoses?.length ? (
          <EmptyState
            title="No Diagnoses Recorded"
            description={
              canCreate
                ? "No previous diagnoses for this patient. Add one above."
                : "This patient has no previous diagnoses."
            }
          />
        ) : (
          <Timeline
            items={diagnoses}
            sortBy="createdAt"
            render={(item) => (
              <TimelineItem key={item.id} date={item.createdAt}>
                <TimelineCard
                  title={`Diagnosis #${item.id}`}
                  description={item.summary}
                  date={item.createdAt}
                  dateDescription="Diagnosed at"
                >
                  {canCreate && (
                    <ActionButtons>
                      <EditAction
                        title="Edit Diagnosis"
                        description="Update the symptoms and diagnosis summary."
                        form={
                          <DoctorDiagnosisForm
                            patientUserId={patientUserId}
                            diagnosisToEdit={item}
                          />
                        }
                      />
                      <DeleteAction
                        title="Delete Diagnosis"
                        description="This action cannot be undone. This will permanently remove this diagnosis from the patient's records."
                        onConfirm={() =>
                          deleteDiagnosis(patientUserId, item.id)
                        }
                        successMessage="Diagnosis deleted successfully"
                        failMessage="Failed to delete diagnosis"
                      />
                    </ActionButtons>
                  )}
                  <div className="border-border/50 border-t pt-3">
                    <p className="text-text-muted text-xs font-medium tracking-wide uppercase">
                      Symptoms
                    </p>
                    <p className="text-text-base mt-1 text-sm">
                      {item.symptoms}
                    </p>
                  </div>
                </TimelineCard>
              </TimelineItem>
            )}
          />
        )}
      </div>
    </div>
  );
}
