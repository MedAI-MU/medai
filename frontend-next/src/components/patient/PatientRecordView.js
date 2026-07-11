"use client";

import { useState } from "react";

import Tabs from "@/components/ui/Tabs";
import AllergiesTab from "@/components/patient/AllergiesTab";
import ChronicDiseasesTab from "@/components/patient/ChronicDiseasesTab";
import SurgeriesTab from "@/components/patient/SurgeriesTab";
import FamilyHistoryTab from "@/components/patient/FamilyHistoryTab";
import EmergencyContactsTab from "@/components/patient/EmergencyContactsTab";
import ScansSection from "@/components/scans/ScansSection";
import PersonalInfoView from "./PersonalInfoView";
import DiagnosesSection from "../diagnosis/DiagnosisSection";

const SECTIONS = [
  { label: "personal info", key: "info" },
  { label: "allergies", key: "allergies" },
  { label: "chronic diseases", key: "chronicDiseases" },
  { label: "surgeries", key: "surgeries" },
  { label: "family history", key: "familyHistories" },
  { label: "emergency contacts", key: "emergencyContacts" },
  { label: "scans", key: "scans" },
  { label: "diagnoses", key: "diagnoses" },
];

function PatientRecordView({
  patient,
  scans = [],
  diagnoses = [],
  role = "secretary",
  appointmentId,
  patientUserId,
}) {
  const [activeTab, setActiveTab] = useState(SECTIONS[0].key);

  const tabsArray = SECTIONS.map((s) => s.label);

  const isReadOnly = role !== "patient";

  return (
    <div className="space-y-6">
      <Tabs
        tabsArray={tabsArray}
        onSetActive={(label) => {
          const section = SECTIONS.find((s) => s.label === label);
          if (section) setActiveTab(section.key);
        }}
        defaultValue={tabsArray[0]}
      />

      {activeTab === "info" && <PersonalInfoView data={patient} />}
      {activeTab === "allergies" && (
        <AllergiesTab data={patient} readOnly={isReadOnly} />
      )}
      {activeTab === "chronicDiseases" && (
        <ChronicDiseasesTab data={patient} readOnly={isReadOnly} />
      )}
      {activeTab === "surgeries" && (
        <SurgeriesTab data={patient} readOnly={isReadOnly} />
      )}
      {activeTab === "familyHistories" && (
        <FamilyHistoryTab data={patient} readOnly={isReadOnly} />
      )}
      {activeTab === "emergencyContacts" && (
        <EmergencyContactsTab data={patient} readOnly={isReadOnly} />
      )}
      {activeTab === "scans" && (
        <ScansSection
          patientId={patientUserId || patient?.userId}
          scans={scans}
          readOnly={role !== "secretary"}
        />
      )}
      {activeTab === "diagnoses" && (
        <DiagnosesSection
          diagnoses={diagnoses}
          patientUserId={patientUserId || patient?.userId}
          appointmentId={appointmentId}
          canCreate={role === "doctor"}
        />
      )}
    </div>
  );
}

export default PatientRecordView;
