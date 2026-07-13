import {
  CalendarCheck,
  Users,
  Stethoscope,
  Briefcase,
  ArrowRight,
} from "lucide-react";
import Link from "next/link";
import { format } from "date-fns";

import { getAllAppointments } from "@/services/server/appointments";
import { getAllPatients } from "@/services/server/patient";
import { getTopRatedDoctors, getSpecialities } from "@/services/server/doctors";
import Card from "@/components/ui/Card";
import IconBadge from "@/components/ui/IconBadge";
import Heading from "@/components/ui/Heading";
import Grid from "@/components/ui/Grid";
import Badge from "@/components/ui/Badge";
import { statusColor } from "@/constants/appointments";
import { formatTime12h, stripSeconds } from "@/lib/utils/DateTimeHelpers";

const statCards = [
  {
    key: "appointments",
    icon: CalendarCheck,
    label: "Appointments",
    color: "blue",
    href: "/secretary/appointments",
  },
  {
    key: "doctors",
    icon: Stethoscope,
    label: "Doctors",
    color: "purple",
    href: "/secretary/doctors",
  },
  {
    key: "patients",
    icon: Users,
    label: "Patients",
    color: "green",
    href: "/secretary/patients",
  },
  {
    key: "specialities",
    icon: Briefcase,
    label: "Specialities",
    color: "amber",
    href: "/secretary/specialities",
  },
];

export default async function SecretaryDashboardContent() {
  const [appointments, patients, doctors, specialities] = await Promise.all([
    getAllAppointments(),
    getAllPatients(),
    getTopRatedDoctors(),
    getSpecialities(),
  ]);

  const counts = {
    appointments: appointments?.length ?? 0,
    patients: patients?.length ?? 0,
    doctors: doctors?.length ?? 0,
    specialities: specialities?.length ?? 0,
  };

  const recentAppointments = [...(appointments || [])]
    .sort((a, b) => new Date(b.createdAt) - new Date(a.createdAt))
    .slice(0, 5);

  return (
    <div className="space-y-8">
      <Heading
        title="Dashboard"
        subtitle="Overview of appointments, doctors, and patients."
      />

      <Grid cols="four">
        {statCards.map(({ key, icon: Icon, label, color, href }) => (
          <Link key={key} href={href} className="block">
            <Card className="hover:border-primary cursor-pointer transition-colors">
              <div className="flex items-center gap-4">
                <IconBadge icon={<Icon size={22} />} color={color} />
                <div>
                  <p className="text-text-muted text-sm">{label}</p>
                  <p className="text-2xl font-bold">{counts[key]}</p>
                </div>
              </div>
            </Card>
          </Link>
        ))}
      </Grid>

      <div className="space-y-4">
        <div className="flex items-center justify-between">
          <Heading Tag="h2" size="sm" title="Recent Appointments" />
          <Link
            href="/secretary/appointments"
            className="text-primary hover:text-primary-hover flex items-center gap-1 text-sm font-medium"
          >
            View All <ArrowRight size={14} />
          </Link>
        </div>

        {recentAppointments.length > 0 ? (
          <div className="space-y-2">
            {recentAppointments.map((apt) => {
              const startTime = formatTime12h(
                stripSeconds(apt?.scheduleSlot?.startTime),
              );
              const endTime = formatTime12h(
                stripSeconds(apt?.scheduleSlot?.endTime),
              );
              const date = apt?.scheduleSlot?.schedule?.dayDate;
              const formattedDate = date
                ? format(new Date(date), "MMM d, yyyy")
                : "";

              return (
                <div
                  key={apt.id}
                  className="bg-surface-overlay flex items-center justify-between rounded-lg px-4 py-3"
                >
                  <div className="flex items-center gap-4">
                    <div className="text-center">
                      <p className="text-sm font-semibold">{formattedDate}</p>
                      <p className="text-text-muted text-xs">
                        {startTime} — {endTime}
                      </p>
                    </div>
                    <div className="text-sm">
                      <p className="font-medium">{apt?.doctor?.name || "Doctor"}</p>
                      <p className="text-text-muted text-xs">
                        Patient #{apt.patientUserId}
                      </p>
                    </div>
                  </div>
                  <Badge
                    color={statusColor[apt.status] || "slate"}
                    text={apt.status}
                    isRounded
                    className="text-[10px]"
                  />
                </div>
              );
            })}
          </div>
        ) : (
          <p className="text-text-muted py-4 text-sm">
            No appointments yet.
          </p>
        )}
      </div>
    </div>
  );
}
