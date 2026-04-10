// {
//     "createdAt": "2026-04-08T18:19:57.229Z",
//     "updatedAt": "2026-04-08T18:19:57.229Z",
//     "userId": 4,
//     "user": {
//         "createdAt": "2026-04-08T18:19:57.157Z",
//         "updatedAt": "2026-04-08T18:19:57.157Z",
//         "id": 4,
//         "name": "Abdol",
//         "gender": null,
//         "role": "patient"
//     },
//     "height": null,
//     "weight": null,
//     "bloodType": null,
//     "maritalStatus": null,
//     "allergies": [],
//     "chronicDiseases": [],
//     "surgeries": [],
//     "familyHistories": [],
//     "emergencyContacts": []
// }

import PageHeading from "@/components/ui/PageHeading";
import MedicalStates from "./MedicalStates";
import { getPatient } from "@/services/server/patient";
import MedicalTabs from "@/components/patient/MedicalTabs";

async function Page() {
  const data = await getPatient();

  return (
    <div className="flex flex-col gap-8">
      <PageHeading
        title="my medical records"
        subtitle="Manage and view your comprehensive medical history."
      />

      <MedicalStates data={data} />
      <MedicalTabs data={data} key={JSON.stringify(data)} />
    </div>
  );
}

export default Page;
