"use client";

import { useState } from "react";
import PersonalInfoTab from "./PersonalInfoTab";
import AllergiesTab from "./AllergiesTab";

const TABS = [
  "personal info",
  "allergies",
  "chronic diseases",
  "surgeries",
  "familyHistories",
  "emergencyContacts",
];

function MedicalTabs({ data }) {
  const [activeTab, setActiveTab] = useState(TABS[0]);

  return (
    <div className="space-y-6">
      <div className="border-border no-scrollbar flex overflow-x-auto border-b">
        {TABS.map((tab) => (
          <button
            key={tab}
            className={`${activeTab === tab ? "border-primary text-primary " : "text-text-muted hover:text-text-base border-b-transparent"} cursor-pointer border-b-2 px-6 py-3 text-sm font-medium whitespace-nowrap capitalize transition-all`}
            onClick={() => setActiveTab(tab)}
          >
            {tab}
          </button>
        ))}
      </div>
      {activeTab === TABS[0] && (
        <PersonalInfoTab data={data} key={JSON.stringify(data)} />
      )}
      {activeTab === TABS[1] && <AllergiesTab data={data} />}
    </div>
  );
}

export default MedicalTabs;
