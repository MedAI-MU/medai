"use client";

import { Clock, ChevronDown } from "lucide-react";
import { useRouter } from "next/navigation";
import toast from "react-hot-toast";
import { formatTime12h, stripSeconds } from "@/lib/utils/DateTimeHelpers";
import { statusColor } from "@/constants/appointments";
import { updateAppointmentStatus } from "@/services/client/appointment";

import AppointmentCardLayout from "@/components/appointments/AppointmentCardLayout";
import Badge from "@/components/ui/Badge";
import Button from "@/components/ui/Button";
import Heading from "@/components/ui/Heading";
import ViewSheet from "@/components/ui/ViewSheet";
import DoctorAppointmentDetails from "@/components/doctor/DoctorAppointmentDetails";
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuTrigger,
} from "@/components/shadcn/dropdown-menu";

function getStatusActions(status) {
  if (status === "pending") return ["confirmed", "cancelled"];
  if (status === "confirmed") return ["finished", "cancelled"];
  return [];
}

export default function AllAppointmentCard({ appointment }) {
  const router = useRouter();
  const { scheduleSlot, doctor, patient, status } = appointment || {};
  const doctorUser = doctor?.user || {};
  const patientUser = patient?.user || {};

  const formatedStartTime = formatTime12h(
    stripSeconds(scheduleSlot?.startTime),
  );
  const formatedEndTime = formatTime12h(stripSeconds(scheduleSlot?.endTime));
  const appointmentDate = scheduleSlot?.schedule?.dayDate;
  const statusActions = getStatusActions(status);

  async function handleStatusChange(newStatus) {
    try {
      await updateAppointmentStatus(appointment.id, newStatus);
      toast.success(`Appointment ${newStatus} successfully`);
      router.refresh();
    } catch {
      toast.error(`Failed to ${newStatus} appointment`);
    }
  }

  return (
    <AppointmentCardLayout
      status={status}
      date={appointmentDate}
      isCompleted={status === "finished"}
      infoSection={
        <>
          <Heading Tag="h3" size="sm" title={doctorUser?.name || "Unknown Doctor"} />
          <div className="text-text-muted mt-1 text-xs">
            Patient: {patientUser?.name || "Unknown Patient"}
          </div>
          <div className="text-text-muted mt-2 flex items-center gap-2 text-sm">
            <Clock size={16} />
            {formatedStartTime} - {formatedEndTime}
          </div>
        </>
      }
      actions={
        <div className="flex items-center gap-2">
          <Badge
            text={status}
            color={statusColor[status] || "blue"}
            className="text-xs font-normal capitalize"
          />
          {statusActions.length > 0 && (
            <DropdownMenu>
              <DropdownMenuTrigger asChild>
                <button className="text-text-muted hover:bg-surface-overlay cursor-pointer rounded-lg p-1.5 transition-colors">
                  <ChevronDown size={14} />
                </button>
              </DropdownMenuTrigger>
              <DropdownMenuContent align="end">
                {statusActions.map((option) => (
                  <DropdownMenuItem
                    key={option}
                    className="capitalize"
                    onSelect={() => handleStatusChange(option)}
                  >
                    {option}
                  </DropdownMenuItem>
                ))}
              </DropdownMenuContent>
            </DropdownMenu>
          )}
          <ViewSheet
            title="Patient & Appointment Details"
            description={`Detailed health profile and appointment info for ${patientUser?.name || "Patient"}`}
            trigger={<Button variation="outline">View Details</Button>}
          >
            <DoctorAppointmentDetails appointment={appointment} />
          </ViewSheet>
        </div>
      }
    />
  );
}
