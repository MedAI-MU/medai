import { Stethoscope, UserCog, Users, Hourglass } from "lucide-react";
import Link from "next/link";

import {
  getSecretaries,
  getDoctors,
  getManagers,
  getPendingSecretaries,
  getPendingDoctors,
} from "@/services/server/manager";
import Card from "@/components/ui/Card";
import IconBadge from "@/components/ui/IconBadge";
import Heading from "@/components/ui/Heading";
import Grid from "@/components/ui/Grid";

const statCards = [
  {
    key: "doctors",
    icon: Stethoscope,
    label: "Doctors",
    color: "blue",
    href: "/manager/users/doctors",
  },
  {
    key: "secretaries",
    icon: UserCog,
    label: "Secretaries",
    color: "purple",
    href: "/manager/users/secretaries",
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
  const [secretaries, doctors, managers, pendingSec, pendingDoc] =
    await Promise.all([
      getSecretaries(),
      getDoctors(),
      getManagers(),
      getPendingSecretaries(),
      getPendingDoctors(),
    ]);

  const counts = {
    doctors: doctors?.length ?? 0,
    secretaries: secretaries?.length ?? 0,
    managers: managers?.length ?? 0,
    pending: (pendingSec?.length ?? 0) + (pendingDoc?.length ?? 0),
  };

  return (
    <div className="space-y-8">
      <Heading
        title="Manager Dashboard"
        subtitle="Overview of the entire system."
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
    </div>
  );
}
