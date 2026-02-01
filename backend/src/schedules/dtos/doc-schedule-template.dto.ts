export class DocScheduleTemplateDto {
  id: number;
  name: string;
  doctor: {
    id: number;
    name: string;
    specialty: string;
  };
  slots: {
    weekDay: number;
    startTime: string;
    endTime: string;
  }[];
  createdBy: {
    id: number;
    name: string;
  };
  createdAt: Date;
  updatedAt: Date;

  constructor(partial: Partial<DocScheduleTemplateDto>) {
    Object.assign(this, partial);
  }
}
