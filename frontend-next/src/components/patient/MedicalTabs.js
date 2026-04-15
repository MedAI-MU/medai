"use client";

import { useState } from "react";
import PersonalInfoTab from "./PersonalInfoTab";
import AllergiesTab from "./AllergiesTab";
import ChronicDiseasesTab from "./ChronicDiseasesTab";

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
    component: null,
  },
  {
    label: "familyHistories",
    component: null,
  },
  {
    label: "emergencyContacts",
    component: null,
  },
];

function MedicalTabs({ data }) {
  const [activeTab, setActiveTab] = useState(TABS[0].label);

  const ActiveComponent = TABS.find((tab) => tab.label === activeTab).component;

  return (
    <div className="space-y-6">
      <div className="border-border no-scrollbar flex overflow-x-auto border-b">
        {TABS.map(({ label }) => (
          <button
            key={label}
            className={`${activeTab === label ? "border-primary text-primary " : "text-text-muted hover:text-text-base border-b-transparent"} cursor-pointer border-b-2 px-6 py-3 text-sm font-medium whitespace-nowrap capitalize transition-all`}
            onClick={() => setActiveTab(label)}
          >
            {label}
          </button>
        ))}
      </div>

      {ActiveComponent && <ActiveComponent data={data} />}
    </div>
  );
}

export default MedicalTabs;
