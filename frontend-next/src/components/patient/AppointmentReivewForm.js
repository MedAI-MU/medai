import { CalendarDays, Stethoscope } from "lucide-react";
import { format } from "date-fns";
import {
  formatTime12h,
  parseDate,
  stripSeconds,
} from "@/lib/utils/DateTimeHelpers";

import { DialogFooter } from "../shadcn/dialog";
import DialogBody from "../ui/DialogBody";
import IconBadge from "../ui/IconBadge";
import Heading from "../ui/Heading";
import TextArea from "../ui/TextArea";
import Button from "../ui/Button";
import StarRating from "../ui/StarRating";
import { Controller, useForm } from "react-hook-form";
import ErrorMessage from "../ui/ErrorMessage";
import { addAppointmentReview } from "@/services/client/appointment";
import { useRouter } from "next/navigation";
import toast from "react-hot-toast";

function AppointmentReivewForm({ appointment, closeModal }) {
  const { id: appId, scheduleSlot, doctor, rating, review } = appointment || {};
  const {
    control,
    register,
    formState: { isSubmitting, errors },
    handleSubmit,
  } = useForm({ defaultValues: { rating: rating ?? 0, review: review ?? "" } });
  const formatedStartTime = formatTime12h(
    stripSeconds(scheduleSlot?.startTime),
  );
  const router = useRouter();

  async function onSubmit(data) {
    try {
      await addAppointmentReview(appId, data);
      toast.success("Review added successfully");
      closeModal?.();
      router.refresh();
    } catch (err) {
      console.error(err);
      toast.error(err?.message);
    }
  }

  return (
    <form onSubmit={handleSubmit(onSubmit)}>
      <DialogBody>
        {/* <!-- Appointment Recap Card --> */}
        <div className="bg-surface-overlay/50 border-border flex items-start gap-4 rounded-lg border p-4">
          <IconBadge
            icon={<Stethoscope />}
            color="blue"
            className="shadow-sm"
          />
          <div className="flex-1">
            <Heading Tag="h3" size="xs" title={`Dr. ${doctor?.name}`} />
            <div className="text-text-muted mt-1 flex items-center gap-2 text-sm">
              <CalendarDays size={16} />
              <span>
                {format(
                  parseDate(scheduleSlot?.schedule?.dayDate),
                  "MMM dd, yyyy",
                )}{" "}
                at {formatedStartTime}
              </span>
            </div>
          </div>
        </div>
        {/* <!-- Rating Display --> */}
        <Controller
          name="rating"
          control={control}
          rules={{
            validate: (val) => {
              return (val >= 1 && val <= 5) || "Please select at least 1 star";
            },
          }}
          render={({ field: { value, onChange }, fieldState }) => (
            <div>
              <StarRating
                starClassName="size-8"
                containerClassName="flex-col gap-2 text-sm"
                defualtRate={value}
                onSetRating={onChange}
                disabled={isSubmitting}
              />
              {fieldState?.error?.message && (
                <ErrorMessage
                  message={fieldState?.error?.message}
                  className="justify-center"
                />
              )}
            </div>
          )}
        />
        {/* <!-- Review Text Block --> */}
        <TextArea
          label="Feedback"
          placeholder="Tell us about your experience with the doctor and your appointment."
          rows={3}
          {...register("review", {
            maxLength: {
              value: 500,
              message: "Review cannot exceed 500 characters",
            },
          })}
          error={errors?.review?.message}
          disabled={isSubmitting}
        />
      </DialogBody>
      <DialogFooter>
        <Button disabled={isSubmitting}>Submit Review</Button>
      </DialogFooter>
    </form>
  );
}

export default AppointmentReivewForm;
