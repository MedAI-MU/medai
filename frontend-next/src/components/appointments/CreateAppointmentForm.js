import toast from "react-hot-toast";
import { useRouter } from "next/navigation";
import { Calendar, Clock, User } from "lucide-react";
import { stripSeconds } from "@/lib/utils/DateTimeHelpers";
import { useDoctorInfo } from "@/contexts/DoctorInfoContext";
import { createAppointment } from "@/services/client/appointment";

import { DialogFooter } from "../shadcn/dialog";
import Button from "../ui/Button";
import DialogBody from "../ui/DialogBody";
import { useTransition } from "react";
import SpinnerMini from "../ui/SpinnerMini";

function CreateAppointmentForm({ slot, day, closeModal }) {
  const { startTime, endTime, id: slotId } = slot || {};
  const {
    doctor: { name, doctorId },
  } = useDoctorInfo();
  const router = useRouter();
  const [isSubmitting, startTransition] = useTransition();

  function handleSubmit(e) {
    e.preventDefault();

    startTransition(async () => {
      try {
        await createAppointment({
          slotId,
          doctorId: Number(doctorId),
        });
        toast.success(
          `Appointment booked for ${day} at ${stripSeconds(startTime)}!`,
        );
        closeModal?.();
        router.refresh();
      } catch (err) {
        console.error(err);
        if (err?.status === 400) {
          toast.error(err?.message);
          return;
        }
        toast.error("Failed to book this slot!");
      }
    });
  }

  return (
    <form onSubmit={handleSubmit}>
      <DialogBody>
        <div className="flex items-start gap-4">
          <div className="bg-primary/10 rounded-lg p-2">
            <User className="text-primary h-5 w-5" />
          </div>
          <div>
            <p className="text-text-muted mb-0.5 text-[11px] font-bold tracking-wider uppercase">
              Doctor
            </p>
            <p className="text-on-surface text-base font-semibold">
              Dr. {name}
            </p>
          </div>
        </div>

        {/* Row: Date */}
        <div className="flex items-start gap-4">
          <div className="bg-primary/10 rounded-lg p-2">
            <Calendar className="text-primary h-5 w-5" />
          </div>
          <div>
            <p className="text-text-muted mb-0.5 text-[11px] font-bold tracking-wider uppercase">
              Date
            </p>
            <p className="text-on-surface text-base font-semibold">{day}</p>
          </div>
        </div>

        {/* Row: Time Slot */}
        <div className="flex items-start gap-4">
          <div className="bg-primary/10 rounded-lg p-2">
            <Clock className="text-primary h-5 w-5" />
          </div>
          <div>
            <p className="text-text-muted mb-0.5 text-[11px] font-bold tracking-wider uppercase">
              Time Slot
            </p>
            <p className="text-on-surface text-base font-semibold">
              {stripSeconds(startTime)} - {stripSeconds(endTime)}
            </p>
          </div>
        </div>
      </DialogBody>
      <DialogFooter className="flex bg-transparent">
        <Button
          variation="outline"
          type="button"
          onClick={() => closeModal?.()}
          className="flex-1"
          disabled={isSubmitting}
        >
          Cancel
        </Button>
        <Button className="flex-1" disabled={isSubmitting}>
          {isSubmitting ? <SpinnerMini /> : "Confirm"}
        </Button>
      </DialogFooter>
    </form>
  );
}

export default CreateAppointmentForm;
