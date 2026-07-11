"use client";

import Image from "next/image";
import { useState } from "react";
import { Search, Check, Trash2, UserRound, Stethoscope } from "lucide-react";
import { deleteUser } from "@/services/client/manager";

import Table from "@/components/ui/Table";
import Badge from "@/components/ui/Badge";
import Button from "@/components/ui/Button";
import FormInput from "@/components/ui/FormInput";
import Tabs from "@/components/ui/Tabs";
import EmptyState from "@/components/ui/EmptyState";
import DeleteDialog from "@/components/ui/DeleteDialog";
import ApproveUserDialog from "@/components/manager/ApproveUserDialog";

export default function UserRoleManager({
  approved = [],
  pending = [],
  role,
  promoteFn,
}) {
  const [activeTab, setActiveTab] = useState("active");
  const [search, setSearch] = useState("");

  const currentList = activeTab === "active" ? approved : pending;
  const filtered = search?.trim()
    ? currentList.filter((u) =>
        u.name?.toLowerCase().includes(search.toLowerCase()),
      )
    : currentList;

  const roleLabel = role.charAt(0).toUpperCase() + role.slice(1);

  return (
    <div className="space-y-6">
      <div className="flex flex-wrap items-center justify-between gap-4">
        <Tabs
          tabsArray={["active", "pending"]}
          defaultValue="active"
          onSetActive={setActiveTab}
        />
        <FormInput
          containerClassName="max-w-sm w-full"
          placeholder="Search by name..."
          startIcon={<Search />}
          value={search}
          onChange={(e) => setSearch(e.target.value)}
        />
      </div>

      {!currentList.length ? (
        <EmptyState
          icon={<UserRound />}
          heading={`No ${activeTab} ${role}s`}
          description={
            activeTab === "active"
              ? `No approved ${role}s yet.`
              : `No pending ${role}s awaiting approval.`
          }
        />
      ) : !filtered.length ? (
        <EmptyState
          icon={<Stethoscope />}
          heading="No results found"
          description={`No ${role}s matched "${search}".`}
        />
      ) : (
        <Table columns="0.4fr 0.5fr 1.5fr 0.5fr 0.7fr 1fr">
          <Table.Header>
            <div></div>
            <div>ID</div>
            <div>Name</div>
            <div>Gender</div>
            <div>Status</div>
            <div>Actions</div>
          </Table.Header>
          <Table.Body
            data={filtered}
            render={(user = {}) => (
              <Table.Row key={user.id}>
                <div>
                  {user?.avatar ? (
                    <div className="relative size-8 shrink-0 overflow-hidden">
                      <Image
                        src={user?.avatar}
                        alt={`${user?.name}'s avatar`}
                        className="rounded-full object-cover"
                        fill
                        unoptimized
                      />
                    </div>
                  ) : (
                    <div className="bg-primary/20 size-8 shrink-0 overflow-hidden rounded-full">
                      <div className="flex h-full w-full items-center justify-center">
                        <UserRound size={14} className="text-primary" />
                      </div>
                    </div>
                  )}
                </div>
                <div className="text-text-muted font-mono text-sm">
                  {user.id}
                </div>
                <div className="font-medium">{user.name}</div>
                <div className="text-text-muted text-sm capitalize">
                  {user.gender || "-"}
                </div>
                <div>
                  {user.status === "approved" ? (
                    <Badge text="Active" color="green" />
                  ) : (
                    <Badge text="Pending" color="amber" />
                  )}
                </div>
                <div className="flex items-center gap-2">
                  {user.status !== "approved" && (
                    <ApproveUserDialog
                      title={`Approve ${roleLabel}`}
                      description={`Are you sure you want to approve ${user.name} as a ${role}?`}
                      confirmLabel="Approve"
                      successMessage={`${user.name} has been approved as a ${role}.`}
                      failMessage={`Failed to approve ${role}.`}
                      onConfirm={() => promoteFn(user.id)}
                    >
                      <Button size="sm" variation="green">
                        <Check size={14} />
                      </Button>
                    </ApproveUserDialog>
                  )}
                  <DeleteDialog
                    title={`Delete ${roleLabel}`}
                    description={`Are you sure you want to delete ${user.name}? This action cannot be undone.`}
                    confirmLabel="Delete"
                    successMessage={`${user.name} has been deleted.`}
                    failMessage={`Failed to delete ${role}.`}
                    onConfirm={() => deleteUser(user.id)}
                  >
                    <Button size="sm" variation="dangerGhost">
                      <Trash2 size={14} />
                    </Button>
                  </DeleteDialog>
                </div>
              </Table.Row>
            )}
          />
        </Table>
      )}
    </div>
  );
}
