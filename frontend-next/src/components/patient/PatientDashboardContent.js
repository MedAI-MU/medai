import {
  CalendarCheck,
  CalendarPlus,
  FileText,
  ArrowRight,
} from "lucide-react";
import Link from "next/link";
import { format } from "date-fns";

import { getUserAppointments } from "@/services/server/appointments";
import { getPatientById } from "@/services/server/patient";
import { getUserFromToken } from "@/lib/session";
import Card from "@/components/ui/Card";
import IconBadge from "@/components/ui/IconBadge";
import Heading from "@/components/ui/Heading";
import Grid from "@/components/ui/Grid";
import Badge from "@/components/ui/Badge";
import Button from "@/components/ui/Button";
import { statusColor } from "@/constants/appointments";
import { formatDate, formatTime12h, stripSeconds } from "@/lib/utils/DateTimeHelpers";

export default async function PatientDashboardContent() {
  const user = await getUserFromToken();
  const appointments = (await getUserAppointments()) || [];
  const todayStr = formatDate(new Date());

  let patient = null;
  if (user?.sub) {
    try {
      patient = await getPatientById(user.sub);
    } catch {
      // patient not found
    }
  }

  const upcomingApps = appointments.filter((a) => {
    const d = a?.scheduleSlot?.schedule?.dayDate;
    return d && d >= todayStr && a.status !== "cancelled" && a.status !== "finished";
  });

  const pendingApps = appointments.filter((a) => a.status === "pending");

  const counts = {
    upcoming: upcomingApps.length,
    pending: pendingApps.length,
  };

  const nextAppointment = upcomingApps.length > 0 ? upcomingApps[0] : null;

  const statCards = [
    {
      key: "upcoming",
      icon: CalendarCheck,
      label: "Upcoming Visits",
      color: "blue",
      href: "/patient/appointments",
    },
    {
      key: "pending",
      icon: CalendarPlus,
      label: "Pending",
      color: "amber",
      href: "/patient/appointments",
    },
  ];

  return (
    <div className="space-y-8">
      <Heading
        title="Dashboard"
        subtitle={`Welcome${patient?.name ? `, ${patient.name}` : ""}. Manage your health and appointments.`}
      />

      <Grid cols="two">
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

      {nextAppointment ? (
        <div className="space-y-4">
          <Heading Tag="h2" size="sm" title="Next Appointment" />
          <Card>
            <div className="flex items-center justify-between">
              <div className="space-y-1">
                <p className="text-text-base text-lg font-semibold">
                  Dr. {nextAppointment?.doctor?.name || "Doctor"}
                </p>
                <div className="flex items-center gap-3 text-sm text-text-muted">
                  <span>
                    {nextAppointment?.scheduleSlot?.schedule?.dayDate
                      ? format(
                          new Date(
                            nextAppointment.scheduleSlot.schedule.dayDate,
                          ),
                          "MMMM d, yyyy",
                        )
                      : ""}
                  </span>
                  <span className="text-border/60">•</span>
                  <span>
                    {formatTime12h(
                      stripSeconds(nextAppointment?.scheduleSlot?.startTime),
                    )}{" "}
                    —{" "}
                    {formatTime12h(
                      stripSeconds(nextAppointment?.scheduleSlot?.endTime),
                    )}
                  </span>
                </div>
              </div>
              <Badge
                color={statusColor[nextAppointment.status] || "slate"}
                text={nextAppointment.status}
                isRounded
                className="text-[10px]"
              />
            </div>
          </Card>
          <div className="flex flex-wrap gap-3">
            <Button href="/patient/appointments" variation="secondary" size="sm">
              View All Appointments
            </Button>
            <Button href="/patient/book-appointment" size="sm">
              Book New Appointment
            </Button>
          </div>
        </div>
      ) : (
        <div className="bg-surface-overlay space-y-4 rounded-lg p-8 text-center">
          <p className="text-text-muted">
            You have no upcoming appointments.
          </p>
          <Button href="/patient/book-appointment">
            Book an Appointment
          </Button>
        </div>
      )}

      <div>
        <Link
          href="/patient/medical-records"
          className="hover:border-primary bg-surface-overlay flex items-center justify-between rounded-lg border border-border/50 p-4 transition-colors"
        >
          <div className="flex items-center gap-3">
            <div className="flex size-10 items-center justify-center rounded-lg bg-blue-50 text-blue-600 dark:bg-blue-900/20">
              <FileText size={20} />
            </div>
            <div>
              <p className="text-sm font-semibold">Medical Records</p>
              <p className="text-text-muted text-xs">
                View your health history and documents
              </p>
            </div>
          </div>
          <ArrowRight size={18} className="text-text-muted" />
        </Link>
      </div>
    </div>
  );
}
