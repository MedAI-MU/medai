"use client";

import { useState } from "react";

function Tabs({ tabsArray, defaultValue, onSetActive }) {
  const [activeTab, setActiveTab] = useState(defaultValue ?? tabsArray[0]);

  return (
    <div className="border-border no-scrollbar flex overflow-x-auto border-b">
      {tabsArray.map((tab) => (
        <button
          key={tab}
          className={`${activeTab === tab ? "border-primary text-primary " : "text-text-muted hover:text-text-base border-b-transparent"} cursor-pointer border-b-2 px-6 py-3 text-sm font-medium whitespace-nowrap capitalize transition-all`}
          onClick={() => {
            setActiveTab(tab);
            onSetActive?.(tab);
          }}
        >
          {tab}
        </button>
      ))}
    </div>
  );
}

export default Tabs;
