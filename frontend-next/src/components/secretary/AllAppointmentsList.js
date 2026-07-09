"use client";

import { useState } from "react";
import { Search } from "lucide-react";

import Tabs from "@/components/ui/Tabs";
import EmptyState from "@/components/ui/EmptyState";
import FormInput from "@/components/ui/FormInput";
import SortToggle from "@/components/ui/SortToggle";
import AnimateWrapper from "@/components/ui/AnimateWrapper";
import AllAppointmentCard from "./AllAppointmentCard";
import { sortAppointmentsByDate } from "@/lib/utils/DateTimeHelpers";

const tabsArray = ["all", "pending", "confirmed", "finished"];

export default function AllAppointmentsList({ appointments }) {
  const [activeTab, setActiveTab] = useState(tabsArray[0]);
  const [search, setSearch] = useState("");
  const [sortDir, setSortDir] = useState("desc");

  let filtered = appointments;

  if (activeTab !== "all") {
    filtered = filtered.filter((app) => app.status === activeTab);
  }

  if (search.trim()) {
    const q = search.toLowerCase();
    filtered = filtered.filter((app) => {
      const doctorName = app?.doctor?.user?.name?.toLowerCase() || "";
      const patientName = app?.patient?.user?.name?.toLowerCase() || "";
      return doctorName.includes(q) || patientName.includes(q);
    });
  }

  return (
    <>
      <Tabs
        tabsArray={tabsArray}
        onSetActive={setActiveTab}
        defaultValue={tabsArray[0]}
      />

      <div className="flex items-center gap-4">
        <FormInput
          containerClassName="max-w-md flex-1"
          placeholder="Search by patient or doctor name..."
          startIcon={<Search />}
          value={search}
          onChange={(e) => setSearch(e.target.value)}
        />
        <SortToggle
          direction={sortDir}
          onToggle={() =>
            setSortDir((d) => (d === "asc" ? "desc" : "asc"))
          }
        />
      </div>

      {filtered.length > 0 ? (
        <div className="space-y-6">
          {sortAppointmentsByDate(filtered, sortDir).map(
            (appointment, i) => (
              <AnimateWrapper
                key={appointment.id}
                delay={i * 0.08}
                triggerOnView={false}
                type="slideUp"
                transitionOptions={{
                  type: "spring",
                  damping: 15,
                  stiffness: 600,
                }}
              >
                <AllAppointmentCard appointment={appointment} />
              </AnimateWrapper>
            ),
          )}
        </div>
      ) : (
        <EmptyState
          title={`${search.trim() ? "No appointments match your search." : `There is no ${activeTab} appointments right now`}`}
        />
      )}
    </>
  );
}
