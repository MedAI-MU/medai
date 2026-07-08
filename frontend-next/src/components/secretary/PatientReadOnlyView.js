"use client";

import { useState } from "react";
import {
  User,
  Droplets,
  Ruler,
  Weight,
  Heart,
  VenusAndMars,
} from "lucide-react";

import Card from "@/components/ui/Card";
import Badge from "@/components/ui/Badge";
import Grid from "@/components/ui/Grid";
import Tabs from "@/components/ui/Tabs";
import Heading from "@/components/ui/Heading";
import InfoRow from "@/components/ui/InfoRow";
import EmptyState from "@/components/ui/EmptyState";
import Timeline from "@/components/ui/Timeline";
import TimelineItem from "@/components/ui/TimelineItem";
import TimelineCard from "@/components/ui/TimelineCard";
import AllergyCard from "@/components/patient/AllergyCard";
import FamilyHistoryCard from "@/components/patient/FamilyHistoryCard";
import EmergencyContactCard from "@/components/patient/EmergencyContactCard";

const SECTIONS = [
  { label: "personal info", key: "info" },
  { label: "allergies", key: "allergies" },
  { label: "chronic diseases", key: "chronicDiseases" },
  { label: "surgeries", key: "surgeries" },
  { label: "family history", key: "familyHistories" },
  { label: "emergency contacts", key: "emergencyContacts" },
];

function PersonalInfoSection({ data }) {
  return (
    <Card className="p-6">
      <Heading size="lg" title="Personal Health Information" className="mb-6" />
      <Grid cols="two" gap="gap-x-12 gap-y-4">
        <InfoRow icon={User} value={data?.name || "-"} />
        <InfoRow
          icon={VenusAndMars}
          value={data?.user?.gender || "Not specified"}
        />
        <InfoRow
          icon={Droplets}
          value={
            data?.bloodType ? <Badge color="red" text={data.bloodType} /> : "-"
          }
        />
        <InfoRow
          icon={Ruler}
          value={data?.height ? `${data.height} cm` : "-"}
        />
        <InfoRow
          icon={Weight}
          value={data?.weight ? `${data.weight} kg` : "-"}
        />
        <InfoRow icon={Heart} value={data?.maritalStatus || "-"} />
      </Grid>
    </Card>
  );
}

function AllergiesSection({ allergies, userId }) {
  if (!allergies?.length) {
    return (
      <EmptyState
        heading="No Allergies Recorded"
        description="This patient has no allergies recorded."
      />
    );
  }
  return (
    <Grid cols="two">
      {allergies.map((item) => (
        <AllergyCard key={item.id} allergy={item} patientId={userId} readOnly />
      ))}
    </Grid>
  );
}

function ChronicDiseasesSection({ chronicDiseases }) {
  if (!chronicDiseases?.length) {
    return (
      <EmptyState
        heading="No Chronic Diseases Recorded"
        description="This patient has no chronic diseases recorded."
      />
    );
  }
  return (
    <Timeline
      items={chronicDiseases}
      sortBy="diagnosisDate"
      render={(item) => (
        <TimelineItem key={item.id} date={item.diagnosisDate}>
          <TimelineCard
            title={item.name}
            description={item.description}
            date={item.diagnosisDate}
            dateDescription="Diagnosed at"
          />
        </TimelineItem>
      )}
    />
  );
}

function SurgeriesSection({ surgeries }) {
  if (!surgeries?.length) {
    return (
      <EmptyState
        heading="No Surgeries Recorded"
        description="This patient has no surgeries recorded."
      />
    );
  }
  return (
    <Timeline
      items={surgeries}
      sortBy="date"
      render={(item) => (
        <TimelineItem key={item.id} date={item.date}>
          <TimelineCard
            title={item.name}
            description={item.description}
            date={item.date}
            dateDescription="Performed at"
          />
        </TimelineItem>
      )}
    />
  );
}

function FamilyHistorySection({ familyHistories, userId }) {
  if (!familyHistories?.length) {
    return (
      <EmptyState
        heading="No Family History Recorded"
        description="This patient has no family history recorded."
      />
    );
  }
  return (
    <Grid cols="two">
      {familyHistories.map((record) => (
        <FamilyHistoryCard
          key={record.id}
          record={record}
          patientId={userId}
          readOnly
        />
      ))}
    </Grid>
  );
}

function EmergencyContactsSection({ emergencyContacts, userId }) {
  if (!emergencyContacts?.length) {
    return (
      <EmptyState
        heading="No Emergency Contacts Recorded"
        description="This patient has no emergency contacts recorded."
      />
    );
  }
  return (
    <Grid cols="two">
      {emergencyContacts.map((contact) => (
        <EmergencyContactCard
          key={contact.id}
          contact={contact}
          patientId={userId}
          readOnly
        />
      ))}
    </Grid>
  );
}

export default function PatientReadOnlyView({ data }) {
  const [activeTab, setActiveTab] = useState(SECTIONS[0].key);

  const tabsArray = SECTIONS.map((s) => s.label);

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

      {activeTab === "info" && <PersonalInfoSection data={data} />}
      {activeTab === "allergies" && (
        <AllergiesSection allergies={data?.allergies} userId={data?.userId} />
      )}
      {activeTab === "chronicDiseases" && (
        <ChronicDiseasesSection chronicDiseases={data?.chronicDiseases} />
      )}
      {activeTab === "surgeries" && (
        <SurgeriesSection surgeries={data?.surgeries} />
      )}
      {activeTab === "familyHistories" && (
        <FamilyHistorySection
          familyHistories={data?.familyHistories}
          userId={data?.userId}
        />
      )}
      {activeTab === "emergencyContacts" && (
        <EmergencyContactsSection
          emergencyContacts={data?.emergencyContacts}
          userId={data?.userId}
        />
      )}
    </div>
  );
}
