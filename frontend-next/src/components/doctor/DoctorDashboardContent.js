import {
  CalendarCheck,
  Clock,
  CalendarDays,
  ArrowRight,
} from "lucide-react";
import Link from "next/link";
import { format } from "date-fns";

import { getUserAppointments } from "@/services/server/appointments";
import { getUserFromToken } from "@/lib/session";
import Card from "@/components/ui/Card";
import IconBadge from "@/components/ui/IconBadge";
import Heading from "@/components/ui/Heading";
import Grid from "@/components/ui/Grid";
import Badge from "@/components/ui/Badge";
import { statusColor } from "@/constants/appointments";
import { formatDate, formatTime12h, stripSeconds } from "@/lib/utils/DateTimeHelpers";

const statCards = [
  {
    key: "today",
    icon: CalendarCheck,
    label: "Today's Appointments",
    color: "blue",
    href: "/doctor/appointments",
  },
  {
    key: "pending",
    icon: Clock,
    label: "Pending",
    color: "amber",
    href: "/doctor/appointments",
  },
  {
    key: "upcoming",
    icon: CalendarDays,
    label: "Upcoming",
    color: "purple",
    href: "/doctor/appointments",
  },
];

export default async function DoctorDashboardContent() {
  const user = await getUserFromToken();
  const appointments = (await getUserAppointments()) || [];
  const todayStr = formatDate(new Date());

  const todayApps = appointments.filter((a) => {
    const d = a?.scheduleSlot?.schedule?.dayDate;
    return d === todayStr;
  });

  const pendingApps = appointments.filter((a) => a.status === "pending");
  const upcomingApps = appointments.filter((a) => {
    const d = a?.scheduleSlot?.schedule?.dayDate;
    return d && d > todayStr;
  });

  const counts = {
    today: todayApps.length,
    pending: pendingApps.length,
    upcoming: upcomingApps.length,
  };

  const nextAppointments = [...todayApps, ...upcomingApps]
    .sort((a, b) => {
      const dateA = a?.scheduleSlot?.schedule?.dayDate || "";
      const dateB = b?.scheduleSlot?.schedule?.dayDate || "";
      if (dateA !== dateB) return dateA.localeCompare(dateB);
      return (a?.scheduleSlot?.startTime || "").localeCompare(
        b?.scheduleSlot?.startTime || "",
      );
    })
    .slice(0, 5);

  return (
    <div className="space-y-8">
      <Heading
        title="Dashboard"
        subtitle={`Welcome${user?.name ? `, ${user.name}` : ""}. Overview of your appointments and schedule.`}
      />

      <Grid cols="three">
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
          <Heading Tag="h2" size="sm" title="Upcoming Appointments" />
          <Link
            href="/doctor/appointments"
            className="text-primary hover:text-primary-hover flex items-center gap-1 text-sm font-medium"
          >
            View All <ArrowRight size={14} />
          </Link>
        </div>

        {nextAppointments.length > 0 ? (
          <div className="space-y-2">
            {nextAppointments.map((apt) => {
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
                    <p className="text-sm font-medium">
                      Patient #{apt.patientUserId}
                    </p>
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
          <div className="bg-surface-overlay rounded-lg p-8 text-center">
            <p className="text-text-muted mb-2 text-sm">
              No upcoming appointments.
            </p>
            <Link
              href="/doctor/availability"
              className="text-primary text-sm font-medium hover:underline"
            >
              Manage your availability
            </Link>
          </div>
        )}
      </div>
    </div>
  );
}
