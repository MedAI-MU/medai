"use client";

import FormSheet from "@/components/ui/FormSheet";
import UploadScanForm from "./UploadScanForm";

function UploadScanSheet({ patientId, appointmentId, children }) {
  return (
    <FormSheet
      title="Upload Scans"
      description="Select medical images to upload for this patient."
      form={<UploadScanForm patientId={patientId} appointmentId={appointmentId} />}
    >
      {children}
    </FormSheet>
  );
}

export default UploadScanSheet;
