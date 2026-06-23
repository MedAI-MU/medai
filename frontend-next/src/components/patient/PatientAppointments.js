"use client";

import { useState } from "react";
import { isAfter } from "date-fns";

import Tabs from "@/components/ui/Tabs";
import EmptyState from "@/components/ui/EmptyState";
import PatientAppointmentCard from "./PatientAppointmentCard";
import AnimateWrapper from "../ui/AnimateWrapper";

const tabsArray = ["upcoming", "past", "cancelled"];

function PatientAppointments({ appointments }) {
  const [activeTab, setActiveTab] = useState(tabsArray[0]);

  let filteredAppointments = [];

  if (activeTab === "upcoming") {
    filteredAppointments = appointments?.filter((app) => {
      const slot = app?.scheduleSlot;
      if (!slot?.schedule?.dayDate || !slot?.startTime) return false;

      // 1) We assumed that the backend date generated in cairo => +03:00
      const fullAppointmentDateTime = new Date(
        `${slot.schedule.dayDate}T${slot.startTime}`,
      );

      // 2) Validate if current date is still comming
      const isStillUpcoming = isAfter(fullAppointmentDateTime, new Date());
      const isValidStatus =
        app.status === "pending" || app.status === "confirmed";

      return isStillUpcoming && isValidStatus;
    });
  }

  if (activeTab === "past")
    filteredAppointments = appointments?.filter((app) => {
      const slot = app?.scheduleSlot;
      if (!slot?.schedule?.dayDate || !slot?.startTime) return false;

      // 1) We assumed that the backend date generated in cairo => +03:00
      const fullAppointmentDateTime = new Date(
        `${slot.schedule.dayDate}T${slot.startTime}`,
      );

      // 2) Validate if current date/time is in the past.
      const isPast = isAfter(new Date(), fullAppointmentDateTime);

      return isPast && app?.status !== "cancelled";
    });

  if (activeTab === "cancelled")
    filteredAppointments = appointments?.filter(
      (app) => app?.status === "cancelled",
    );

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
              <PatientAppointmentCard appointment={appointment} />
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

export default PatientAppointments;
