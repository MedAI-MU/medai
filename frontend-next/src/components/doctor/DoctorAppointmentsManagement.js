"use client";

import { useRouter } from "next/navigation";

import toast from "react-hot-toast";
import { updateAppointmentStatus } from "@/services/client/appointment";
import DoctorAppointments from "@/components/doctor/DoctorAppointments";

function DoctorAppointmentsManagement({ appointments }) {
  const router = useRouter();

  async function handleStatusChange(appointmentId, newStatus) {
    try {
      await updateAppointmentStatus(appointmentId, newStatus);
      toast.success(`Appointment is now ${newStatus}`);
      router.refresh();
    } catch {
      toast.error("Failed to update appointment status");
    }
  }

  return (
    <DoctorAppointments
      appointments={appointments}
      cardProps={{
        onStatusChange: handleStatusChange,
      }}
    />
  );
}

export default DoctorAppointmentsManagement;
