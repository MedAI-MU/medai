"use client";

import PersonalInfoTab from "./PersonalInfoTab";
import AllergiesTab from "./AllergiesTab";
import ChronicDiseasesTab from "./ChronicDiseasesTab";
import SurgeriesTab from "./SurgeriesTab";
import { useState } from "react";
import FamilyHistoryTab from "./FamilyHistoryTab";
import EmergencyContactsTab from "./EmergencyContactsTab";
import Tabs from "../ui/Tabs";

const TABS = [
  {
    label: "personal info",
    component: PersonalInfoTab,
  },
  {
    label: "allergies",
    component: AllergiesTab,
  },
  {
    label: "chronic diseases",
    component: ChronicDiseasesTab,
  },
  {
    label: "surgeries",
    component: SurgeriesTab,
  },
  {
    label: "family histories",
    component: FamilyHistoryTab,
  },
  {
    label: "emergency contacts",
    component: EmergencyContactsTab,
  },
];

const tabsArray = TABS.map((tab) => tab.label);

function MedicalTabs({ data }) {
  const [activeTab, setActiveTab] = useState(tabsArray[0]);

  const ActiveComponent = TABS.find((tab) => tab.label === activeTab).component;

  return (
    <div className="space-y-6">
      <Tabs
        tabsArray={tabsArray}
        onSetActive={setActiveTab}
        defaultValue={tabsArray[0]}
      />

      {ActiveComponent && <ActiveComponent data={data} />}
    </div>
  );
}

export default MedicalTabs;
