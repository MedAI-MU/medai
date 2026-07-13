"use client";

import { Clock, ChevronDown } from "lucide-react";
import { formatTime12h, stripSeconds } from "@/lib/utils/DateTimeHelpers";
import { statusColor } from "@/constants/appointments";
import { usePatient } from "@/hooks/patient.js/usePatient";

import AppointmentCardLayout from "@/components/appointments/AppointmentCardLayout";
import Badge from "@/components/ui/Badge";
import Heading from "@/components/ui/Heading";
import Button from "@/components/ui/Button";
import ViewSheet from "@/components/ui/ViewSheet";
import DoctorAppointmentDetails from "./DoctorAppointmentDetails";
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuTrigger,
} from "@/components/shadcn/dropdown-menu";
import SkeletonBox from "../ui/SkeletonBox";

const STATUS_OPTIONS = ["pending", "confirmed", "cancelled", "finished"];

function DoctorAppointmentCard({ appointment, onStatusChange }) {
  const { scheduleSlot, patientUserId, status } = appointment || {};
  const { data: patient, isLoading: patientLoading } =
    usePatient(patientUserId);

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
            title={
              patientLoading ? (
                <SkeletonBox className="h-3 w-14" />
              ) : (
                patient?.name || "Unknown Patient"
              )
            }
          />
          <div className="text-text-muted mt-1 flex flex-wrap gap-2 text-xs">
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
          <div className="flex items-center gap-2">
            <Badge
              text={status}
              color={statusColor[status] || "blue"}
              className="text-xs font-normal capitalize"
            />
            {onStatusChange && (
              <DropdownMenu>
                <DropdownMenuTrigger asChild>
                  <button
                    className={`text-text-muted hover:bg-surface-overlay cursor-pointer rounded-lg p-1.5 transition-colors`}
                  >
                    <ChevronDown size={14} />
                  </button>
                </DropdownMenuTrigger>
                <DropdownMenuContent align="end">
                  {STATUS_OPTIONS.map((option) => (
                    <DropdownMenuItem
                      key={option}
                      className="capitalize"
                      disabled={option === status}
                      onSelect={() => onStatusChange(appointment.id, option)}
                    >
                      {option}
                    </DropdownMenuItem>
                  ))}
                </DropdownMenuContent>
              </DropdownMenu>
            )}
          </div>

          <div className="flex items-center gap-2">
            <ViewSheet
              title="Patient & Appointment Details"
              description={`Detailed health profile and schedule info for ${patient?.name || "Patient"}`}
              trigger={<Button variation="outline">View Details</Button>}
            >
              <DoctorAppointmentDetails appointment={appointment} />
            </ViewSheet>
          </div>
        </>
      }
    />
  );
}

export default DoctorAppointmentCard;
