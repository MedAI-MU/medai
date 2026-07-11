"use client";

import { useState } from "react";
import { Search, Trash2, UserRound, Stethoscope } from "lucide-react";

import Table from "@/components/ui/Table";
import Button from "@/components/ui/Button";
import FormInput from "@/components/ui/FormInput";
import EmptyState from "@/components/ui/EmptyState";
import DeleteDialog from "@/components/ui/DeleteDialog";
import { deleteUser } from "@/services/client/manager";
import Image from "next/image";

export default function ManagersList({ managers = [] }) {
  const [search, setSearch] = useState("");

  const filtered = search?.trim()
    ? managers.filter((u) =>
        u.name?.toLowerCase().includes(search.toLowerCase()),
      )
    : managers;

  return (
    <div className="space-y-6">
      <FormInput
        containerClassName="max-w-sm"
        placeholder="Search by name..."
        startIcon={<Search />}
        value={search}
        onChange={(e) => setSearch(e.target.value)}
      />

      {!managers.length ? (
        <EmptyState
          icon={<UserRound />}
          heading="No managers"
          description="No manager accounts exist in the system."
        />
      ) : !filtered.length ? (
        <EmptyState
          icon={<Stethoscope />}
          heading="No results found"
          description={`No managers matched "${search}".`}
        />
      ) : (
        <Table columns="0.4fr 0.5fr 1.5fr 0.5fr 1fr">
          <Table.Header>
            <div></div>
            <div>ID</div>
            <div>Name</div>
            <div>Gender</div>
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
                  <DeleteDialog
                    title="Delete Manager"
                    description={`Are you sure you want to delete ${user.name}? This action cannot be undone.`}
                    confirmLabel="Delete"
                    successMessage={`${user.name} has been deleted.`}
                    failMessage="Failed to delete manager."
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
