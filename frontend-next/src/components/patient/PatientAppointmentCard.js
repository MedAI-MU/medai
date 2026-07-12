"use client";

import { Clock } from "lucide-react";
import { formatTime12h, stripSeconds } from "@/lib/utils/DateTimeHelpers";
import { updateAppointmentStatus } from "@/services/client/appointment";
import { statusColor } from "@/constants/appointments";

import AppointmentCardLayout from "@/components/appointments/AppointmentCardLayout";
import Badge from "@/components/ui/Badge";
import Heading from "@/components/ui/Heading";
import Button from "@/components/ui/Button";
import DeleteDialog from "@/components/ui/DeleteDialog";
import FormDialog from "@/components/ui/FormDialog";
import AppointmentReivewForm from "./AppointmentReivewForm";

function PatientAppointmentCard({ appointment }) {
  const { id, scheduleSlot, doctor, status } = appointment || {};
  const formatedStartTime = formatTime12h(
    stripSeconds(scheduleSlot?.startTime),
  );
  const formatedEndTime = formatTime12h(stripSeconds(scheduleSlot?.endTime));
  const isCancellable = status === "pending" || status === "confirmed";

  return (
    <AppointmentCardLayout
      status={status}
      date={scheduleSlot?.schedule?.dayDate}
      isCompleted={status === "finished"}
      infoSection={
        <>
          <Heading Tag="h3" size="sm" title={`Dr. ${doctor?.name}`} />
          {doctor?.specialities?.length > 0 ? (
            <div className="flex flex-wrap gap-1">
              {doctor.specialities?.map((spec) => (
                <Badge key={spec} text={spec} color="purple" isRounded />
              ))}
            </div>
          ) : (
            <em className="text-text-muted block text-sm">
              No specialities listed yet
            </em>
          )}

          <div className="text-text-muted flex items-center gap-2 text-sm">
            <Clock size={16} />
            {formatedStartTime} - {formatedEndTime}
          </div>
        </>
      }
      actions={
        <>
          <Badge
            text={status}
            color={statusColor[status]}
            className="text-xs font-normal capitalize"
          />
          {(isCancellable || status === "finished") && (
            <div className="flex w-full gap-3 max-md:justify-end md:w-auto">
              {status === "finished" && (
                <FormDialog
                  title="Rate Your Appointment"
                  form={<AppointmentReivewForm appointment={appointment} />}
                >
                  <Button variation="outline">Review</Button>
                </FormDialog>
              )}
              {isCancellable && (
                <DeleteDialog
                  title="Cancel appointment?"
                  description={`You are about to cancel your appointment with Dr. ${doctor?.name}. This action cannot be undone.`}
                  confirmLabel="Yes, Cancel Appointment"
                  cancelLabel="No, Keep It"
                  successMessage="Appointment cancelled successfully"
                  failMessage="Failed to cancel Appointments"
                  onConfirm={() => updateAppointmentStatus(id, "cancelled")}
                >
                  <Button variation="dangerGhost">Cancel Booking</Button>
                </DeleteDialog>
              )}
            </div>
          )}
        </>
      }
    />
  );
}

export default PatientAppointmentCard;
