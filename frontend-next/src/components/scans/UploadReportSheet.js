"use client";

import FormSheet from "@/components/ui/FormSheet";
import UploadReportForm from "./UploadReportForm";

function UploadReportSheet({ scanId, patientId, children }) {
  return (
    <FormSheet
      title="Upload Report"
      description="Upload a report file for this scan."
      form={<UploadReportForm patientId={patientId} scanId={scanId} />}
    >
      {children}
    </FormSheet>
  );
}

export default UploadReportSheet;
