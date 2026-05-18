import ScheduleTemplateCard from "@/components/schedule/ScheduleTemplateCard";
import { getScheduleTemplates } from "@/services/server/schedule";

async function ScheduleTemplateList({ name }) {
  const {
    data: templates = [],
    totalCount,
    hasNext,
    hasPrevious,
  } = await getScheduleTemplates(name);
  console.log(templates);
  //     "currentPage": 1,
  //     "pageSize": 10,
  //     "hasNext": true,
  //     "hasPrevious": false

  return (
    <div className="mt-8 flex flex-col gap-6">
      {templates.map((template = {}) => (
        <ScheduleTemplateCard key={template.id} template={template} />
      ))}
    </div>
  );
}

export default ScheduleTemplateList;
// {
//     "data": [
//         {
//             "id": 15,
//             "name": "alsdhal",
//             "doctor": {
//                 "id": 5,
//                 "name": "Abdo Ghozal",
//                 "specialty": "General"
//             },
//             "slots": [
//                 {
//                     "weekDay": 1,
//                     "startTime": "09:00:00",
//                     "endTime": "17:00:00"
//                 },
//                 {
//                     "weekDay": 2,
//                     "startTime": "09:00:00",
//                     "endTime": "17:00:00"
//                 }
//             ],
//             "createdBy": {
//                 "id": 5,
//                 "name": "Abdo Ghozal"
//             },
//             "createdAt": "2026-05-16T23:42:12.549Z",
//             "updatedAt": "2026-05-16T23:42:12.549Z"
//         },
//     ],
//     "totalCount": 15,
//     "currentPage": 1,
//     "pageSize": 10,
//     "hasNext": true,
//     "hasPrevious": false
// }
