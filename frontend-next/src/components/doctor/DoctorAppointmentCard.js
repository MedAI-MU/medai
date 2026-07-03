"use client";

import { Clock } from "lucide-react";
import { formatTime12h, stripSeconds } from "@/lib/utils/DateTimeHelpers";
import { statusColor } from "@/constants/appointments";

import AppointmentCardLayout from "@/components/appointments/AppointmentCardLayout";
import Badge from "@/components/ui/Badge";
import Heading from "@/components/ui/Heading";
import Button from "@/components/ui/Button";
import ViewSheet from "@/components/ui/ViewSheet";
import DoctorAppointmentDetails from "./DoctorAppointmentDetails";

function DoctorAppointmentCard({ appointment }) {
  const { scheduleSlot, patient, status } = appointment || {};
  const patientUser = patient?.user || {};

  const formatedStartTime = formatTime12h(
    stripSeconds(scheduleSlot?.startTime),
  );
  const formatedEndTime = formatTime12h(stripSeconds(scheduleSlot?.endTime));

  const appointmentDate = scheduleSlot?.schedule?.dayDate;

  return (
    <AppointmentCardLayout
      status={status}
      date={appointmentDate}
      isCompleted={status === "finished"}
      infoSection={
        <>
          <Heading
            Tag="h3"
            size="sm"
            title={patientUser?.name || "Unknown Patient"}
          />
          <div className="text-text-muted mt-1 flex flex-wrap gap-2 text-xs">
            {patientUser?.gender && (
              <Badge text={patientUser.gender} color="blue" isRounded />
            )}
            {patient?.bloodType && (
              <Badge
                text={`Blood: ${patient.bloodType}`}
                color="red"
                isRounded
              />
            )}
          </div>

          <div className="text-text-muted mt-2 flex items-center gap-2 text-sm">
            <Clock size={16} />
            {formatedStartTime} - {formatedEndTime}
          </div>
        </>
      }
      actions={
        <>
          <Badge
            text={status}
            color={statusColor[status] || "blue"}
            className="text-xs font-normal capitalize"
          />
          <ViewSheet
            title="Patient & Appointment Details"
            description={`Detailed health profile and schedule info for ${patientUser?.name || "Patient"}`}
            trigger={<Button variation="outline">View Details</Button>}
          >
            <DoctorAppointmentDetails appointment={appointment} />
          </ViewSheet>
        </>
      }
    />
  );
}

export default DoctorAppointmentCard;
