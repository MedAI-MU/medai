import {
  User,
  Ruler,
  Droplet,
  Heart,
  Calendar,
  Activity,
  Weight,
  CalendarCheck,
  Star,
  MessageSquare,
  Clock,
} from "lucide-react";
import Card from "@/components/ui/Card";
import StarRating from "../ui/StarRating";
import { formatTime12h, stripSeconds } from "@/lib/utils/DateTimeHelpers";
import { format } from "date-fns";

function DetailRow({
  icon: Icon,
  label,
  value,
  iconColor = "text-text-muted",
}) {
  return (
    <div className="border-border/50 flex items-center justify-between border-b py-3 last:border-0">
      <div className="flex items-center gap-3">
        {Icon && <Icon className={`${iconColor}`} size={18} />}
        <span className="text-text-muted text-sm font-medium">{label}</span>
      </div>
      <span className="text-text-base text-sm font-semibold capitalize">
        {value || "Not provided"}
      </span>
    </div>
  );
}

function DoctorAppointmentDetails({ appointment }) {
  const { scheduleSlot, patient, status, createdAt, review, rating } =
    appointment || {};
  const patientUser = patient?.user || {};

  const formatedStartTime = formatTime12h(
    stripSeconds(scheduleSlot?.startTime),
  );
  const formatedEndTime = formatTime12h(stripSeconds(scheduleSlot?.endTime));

  const appointmentDate = scheduleSlot?.schedule?.dayDate;

  const patientRows = [
    {
      icon: User,
      label: "Full Name",
      value: patientUser?.name,
      iconColor: "text-primary",
    },
    {
      icon: Activity,
      label: "Gender",
      value: patientUser?.gender,
      iconColor: "text-blue-500",
    },
    {
      icon: Droplet,
      label: "Blood Type",
      value: patient?.bloodType,
      iconColor: "text-danger",
    },
    {
      icon: Ruler,
      label: "Height",
      value: patient?.height ? `${patient.height} cm` : null,
      iconColor: "text-purple-500",
    },
    {
      icon: Weight,
      label: "Weight",
      value: patient?.weight ? `${patient.weight} kg` : null,
      iconColor: "text-emerald-500",
    },
    {
      icon: Heart,
      label: "Marital Status",
      value: patient?.maritalStatus,
      iconColor: "text-pink-500",
    },
  ];

  return (
    <>
      {/* Patient Information Section */}
      <div className="space-y-3">
        <h4 className="text-text-base border-border border-b pb-1 text-sm font-bold tracking-wider uppercase">
          Patient Profile
        </h4>
        <Card className="bg-surface-overlay/30 border-border/60 rounded-xl border p-4">
          {patientRows.map((row) => (
            <DetailRow
              key={row.label}
              icon={row.icon}
              label={row.label}
              value={row.value}
              iconColor={row.iconColor}
            />
          ))}
        </Card>
      </div>

      {/* Appointment Information Section */}
      <div className="space-y-3">
        <h4 className="text-text-base border-border border-b pb-1 text-sm font-bold tracking-wider uppercase">
          Appointment Details
        </h4>
        <Card className="bg-surface-overlay/30 border-border/60 rounded-xl border p-4">
          <DetailRow
            icon={Calendar}
            label="Appointment Date"
            value={appointmentDate}
            iconColor="text-primary"
          />
          <DetailRow
            icon={Clock}
            label="Time Slot"
            value={`${formatedStartTime} - ${formatedEndTime}`}
            iconColor="text-primary"
          />
          <DetailRow
            icon={CalendarCheck}
            label="Status"
            value={status}
            iconColor="text-primary"
          />
          <DetailRow
            icon={Calendar}
            label="Booked At"
            value={
              createdAt
                ? format(new Date(createdAt), "MMM d, yyyy • hh:mm a")
                : null
            }
            iconColor="text-text-muted"
          />
          {(status === "finished" || rating) && (
            <DetailRow
              icon={Star}
              iconColor="text-[#fcc419] fill-[#fcc419]"
              label="Rating"
              value={
                rating ? (
                  <StarRating
                    defualtRate={rating}
                    starClassName="size-4"
                    isViewOnly
                    isLabelHidden
                  />
                ) : (
                  <span className="text-text-muted text-xs italic">
                    Not rated yet
                  </span>
                )
              }
            />
          )}
          {review && (
            <div className="border-border/50 border-b py-3 last:border-0">
              <div className="mb-2 flex items-center gap-3">
                <MessageSquare className="text-primary" size={18} />
                <span className="text-text-muted text-sm font-medium">
                  Patient Feedback
                </span>
              </div>
              <p className="text-text-base bg-surface-overlay/40 border-border/30 rounded-lg border p-3 text-sm italic">
                &quot;{review}&quot;
              </p>
            </div>
          )}
        </Card>
      </div>
    </>
  );
}

export default DoctorAppointmentDetails;
