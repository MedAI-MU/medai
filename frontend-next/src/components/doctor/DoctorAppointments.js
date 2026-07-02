"use client";

import { useState } from "react";
import { formatDate } from "@/lib/utils/DateTimeHelpers";

import Tabs from "@/components/ui/Tabs";
import EmptyState from "@/components/ui/EmptyState";
import AnimateWrapper from "@/components/ui/AnimateWrapper";
import DoctorAppointmentCard from "./DoctorAppointmentCard";

const tabsArray = ["today", "upcoming", "past"];

function DoctorAppointments({ appointments }) {
  const [activeTab, setActiveTab] = useState(tabsArray[0]);

  const todayStr = formatDate(new Date());

  let filteredAppointments = [];

  if (activeTab === "today") {
    filteredAppointments = appointments?.filter((app) => {
      const slot = app?.scheduleSlot;
      const dayDate = slot?.schedule?.dayDate;
      if (!dayDate) return false;
      return dayDate === todayStr;
    });
  }

  if (activeTab === "upcoming") {
    filteredAppointments = appointments?.filter((app) => {
      const slot = app?.scheduleSlot;
      const dayDate = slot?.schedule?.dayDate;
      if (!dayDate) return false;
      return dayDate > todayStr && app?.status !== "finished";
    });
  }

  if (activeTab === "past") {
    filteredAppointments = appointments?.filter((app) => {
      const slot = app?.scheduleSlot;
      const dayDate = slot?.schedule?.dayDate;
      if (!dayDate) return false;
      return (
        dayDate < todayStr ||
        (dayDate !== todayStr && app?.status === "finished")
      );
    });
  }

  return (
    <>
      <Tabs
        tabsArray={tabsArray}
        onSetActive={setActiveTab}
        defaultValue={tabsArray[0]}
      />

      {filteredAppointments?.length > 0 ? (
        <div className="space-y-6">
          {filteredAppointments?.map((appointment, i) => (
            <AnimateWrapper
              key={appointment?.id}
              delay={i * 0.08}
              triggerOnView={false}
              type="slideUp"
              transitionOptions={{
                type: "spring",
                damping: 15,
                stiffness: 600,
              }}
            >
              <DoctorAppointmentCard appointment={appointment} />
            </AnimateWrapper>
          ))}
        </div>
      ) : (
        <EmptyState
          title={`There is no ${activeTab} appointments right now.`}
        />
      )}
    </>
  );
}

export default DoctorAppointments;
