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

import Heading from "@/components/ui/Heading";
import MedicalStates from "@/components/patient/MedicalStates";
import { getPatient } from "@/services/server/patient";
import MedicalTabs from "@/components/patient/MedicalTabs";

async function Page() {
  const data = await getPatient();

  return (
    <div className="flex flex-col gap-8">
      <Heading
        title="My Medical Records"
        subtitle="Manage and view your comprehensive medical history."
      />

      <MedicalStates data={data} />
      <MedicalTabs data={data} />
    </div>
  );
}

export default Page;
