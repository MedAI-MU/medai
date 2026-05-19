import ScheduleTemplateCard from "@/components/schedule/ScheduleTemplateCard";
import { getScheduleTemplates } from "@/services/server/schedule";
import Pagination from "@/components/ui/Pagination";
import { PAGE_SIZE } from "@/constants/pagination";
import AnimateWrapper from "../ui/AnimateWrapper";

async function ScheduleTemplateList({ name, pageNo = 1 }) {
  const { data: templates = [], totalCount } = await getScheduleTemplates(
    name,
    PAGE_SIZE,
    pageNo,
  );

  return (
    <>
      <div className="mt-10 flex flex-col gap-6">
        {templates.map((template = {}, i) => (
          <AnimateWrapper
            key={template.id}
            type="slideUp"
            delay={i * 0.1}
            transitionOptions={{ type: "spring", damping: 15, stiffness: 500 }}
          >
            <ScheduleTemplateCard template={template} />
          </AnimateWrapper>
        ))}
      </div>
      <Pagination
        totalCount={totalCount}
        pageSize={PAGE_SIZE}
        className="mt-14"
      />
    </>
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
