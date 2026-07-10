"use client";

import { Mail, Phone, Cake, UserRound, FileText, Calendar } from "lucide-react";

import Card from "@/components/ui/Card";
import Badge from "@/components/ui/Badge";
import ProfileAvatar from "@/components/profile/ProfileAvatar";

const roleBadgeColor = {
  manager: "purple",
  doctor: "blue",
  patient: "amber",
  secretary: "orange",
};

export default function ProfileView({ user = {} }) {
  return (
    <div className="mx-auto max-w-2xl space-y-8">
      <Card>
        <div className="flex flex-col items-center gap-6 sm:flex-row sm:items-start">
          <ProfileAvatar user={user} />

          <div className="flex-1 text-center sm:text-left">
            <h2 className="text-2xl font-bold">{user?.name}</h2>
            <div className="mt-2 flex items-center justify-center gap-3 sm:justify-start">
              <Badge text={user?.role} color={roleBadgeColor[user?.role]} />
              <Badge
                text={user?.status === "approved" ? "Active" : "Pending"}
                color={user?.status === "approved" ? "green" : "amber"}
              />
            </div>
            <div className="text-text-muted mt-2 flex items-center justify-center gap-1.5 text-sm sm:justify-start">
              <Calendar size={14} />
              Member since{" "}
              {user?.createdAt
                ? new Date(user.createdAt).toLocaleDateString("en-US", {
                    year: "numeric",
                    month: "long",
                  })
                : "..."}
            </div>
          </div>
        </div>
      </Card>

      <Card>
        <h3 className="mb-6 text-lg font-semibold">Personal Information</h3>
        <div className="space-y-5">
          <div className="flex items-center gap-3">
            <Mail size={18} className="text-text-subtle shrink-0" />
            <div>
              <p className="text-text-muted text-xs">Email</p>
              <p className="text-sm">{user?.email || "-"}</p>
            </div>
          </div>
          <div className="flex items-center gap-3">
            <Phone size={18} className="text-text-subtle shrink-0" />
            <div>
              <p className="text-text-muted text-xs">Phone</p>
              <p className="text-sm">{user?.phone || "-"}</p>
            </div>
          </div>
          <div className="flex items-center gap-3">
            <Cake size={18} className="text-text-subtle shrink-0" />
            <div>
              <p className="text-text-muted text-xs">Birth Date</p>
              <p className="text-sm">
                {user?.birthDate
                  ? new Date(user.birthDate).toLocaleDateString("en-US", {
                      year: "numeric",
                      month: "long",
                      day: "numeric",
                    })
                  : "-"}
              </p>
            </div>
          </div>
          <div className="flex items-center gap-3">
            <UserRound size={18} className="text-text-subtle shrink-0" />
            <div>
              <p className="text-text-muted text-xs">Gender</p>
              <p className="text-sm capitalize">{user?.gender || "-"}</p>
            </div>
          </div>
          <div className="flex items-start gap-3">
            <FileText size={18} className="text-text-subtle mt-0.5 shrink-0" />
            <div>
              <p className="text-text-muted text-xs">Bio</p>
              <em className="text-sm whitespace-pre-wrap">
                {user?.bio ? <>&quot;{user?.bio}&quot;</> : "-"}
              </em>
            </div>
          </div>
        </div>
      </Card>
    </div>
  );
}
