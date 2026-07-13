import { Stethoscope, UserCog, Users, Hourglass, UserPlus } from "lucide-react";
import Link from "next/link";

import {
  getSecretaries,
  getDoctors,
  getManagers,
  getPendingSecretaries,
  getPendingDoctors,
} from "@/services/server/manager";
import { getAllPatients } from "@/services/server/patient";
import Card from "@/components/ui/Card";
import IconBadge from "@/components/ui/IconBadge";
import Heading from "@/components/ui/Heading";
import Grid from "@/components/ui/Grid";
import Badge from "@/components/ui/Badge";

export const metadata = {
  title: "Manager Dashboard",
  description: "Overview of the entire system.",
};

const statCards = [
  {
    key: "patients",
    icon: UserPlus,
    label: "Patients",
    color: "green",
    href: "/manager/patients",
  },
  {
    key: "doctors",
    icon: Stethoscope,
    label: "Doctors",
    color: "purple",
    href: "/manager/users/doctors",
  },
  {
    key: "secretaries",
    icon: UserCog,
    label: "Secretaries",
    color: "orange",
    href: "/manager/users",
  },
  {
    key: "managers",
    icon: Users,
    label: "Managers",
    color: "slate",
    href: "/manager/users/managers",
  },
  {
    key: "pending",
    icon: Hourglass,
    label: "Pending",
    color: "amber",
    href: "/manager/users/doctors",
  },
];

export default async function ManagerDashboard() {
  const [secretaries, doctors, managers, pendingSec, pendingDoc, patients] =
    await Promise.all([
      getSecretaries(),
      getDoctors(),
      getManagers(),
      getPendingSecretaries(),
      getPendingDoctors(),
      getAllPatients(),
    ]);

  const counts = {
    patients: patients?.length ?? 0,
    doctors: doctors?.length ?? 0,
    secretaries: secretaries?.length ?? 0,
    managers: managers?.length ?? 0,
    pending: (pendingSec?.length ?? 0) + (pendingDoc?.length ?? 0),
  };

  const allPending = [
    ...(pendingSec || []).map((u) => ({ ...u, role: "secretary" })),
    ...(pendingDoc || []).map((u) => ({ ...u, role: "doctor" })),
  ];

  return (
    <div className="space-y-8">
      <Heading
        title="Manager Dashboard"
        subtitle="Overview of the entire system."
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

      <div className="space-y-4">
        <Heading Tag="h2" size="sm" title="Pending Approvals" />
        {allPending.length > 0 ? (
          <div className="space-y-2">
            {allPending.slice(0, 6).map((user) => (
              <div
                key={`${user.role}-${user.id}`}
                className="bg-surface-overlay flex items-center justify-between rounded-lg px-4 py-3"
              >
                <div className="flex items-center gap-3">
                  <div className="flex size-9 items-center justify-center rounded-full bg-amber-50 text-amber-600 dark:bg-amber-900/20">
                    <Hourglass size={15} />
                  </div>
                  <div>
                    <p className="text-sm font-medium">
                      {user.name || "Unnamed"}
                    </p>
                    <p className="text-text-muted text-xs">{user.email}</p>
                  </div>
                </div>
                <div className="flex items-center gap-2">
                  <Badge
                    color="amber"
                    text={user.role}
                    isRounded
                    className="text-[10px]"
                  />
                  <Link
                    href={`/manager/users/${user.role === "doctor" ? "doctors" : "secretaries"}`}
                    className="text-primary text-xs font-medium hover:underline"
                  >
                    Review
                  </Link>
                </div>
              </div>
            ))}
            {allPending.length > 6 && (
              <p className="text-text-muted text-center text-sm">
                +{allPending.length - 6} more pending approvals
              </p>
            )}
          </div>
        ) : (
          <div className="bg-surface-overlay rounded-lg py-8 text-center">
            <p className="text-text-muted text-sm">
              No pending approvals. All users have been reviewed.
            </p>
          </div>
        )}
      </div>
    </div>
  );
}
