import { Stethoscope } from "lucide-react";
import Card from "@/components/ui/Card";
import { getInitials } from "@/lib/utils/stringHelpers";
import Heading from "../ui/Heading";

function DoctorProfileCard({ name, speciality }) {
  const initials = getInitials(name);

  return (
    <Card className="relative overflow-hidden">
      {/* Decorative background accent */}
      <div className="bg-primary/15 pointer-events-none absolute -top-20 -right-20 h-64 w-64 rounded-full blur-3xl" />

      <div className="relative z-10 flex flex-col items-start gap-6 md:flex-row md:items-center">
        {/* Avatar */}
        <div className="relative shrink-0">
          <div className="border-border bg-primary/10 flex h-24 w-24 items-center justify-center overflow-hidden rounded-full border-4 shadow-md md:h-28 md:w-28">
            <span className="text-primary text-3xl font-bold">{initials}</span>
          </div>
          {/* Online dot */}
          <span className="border-surface bg-success absolute right-1 bottom-1 h-4 w-4 rounded-full border-2 shadow-sm" />
        </div>

        {/* Info */}
        <div className="flex-1 space-y-2">
          <div className="flex flex-col justify-between gap-4 sm:flex-row sm:items-center">
            {/* Name + speciality */}
            <div>
              <Heading title={name} />
              <p className="text-primary mt-1 flex items-center gap-1 text-sm font-medium">
                <Stethoscope size={15} />
                {speciality}
              </p>
            </div>

            {/* Stats */}
            <div className="border-border flex gap-6 sm:border-l sm:pl-6">
              <div className="text-center">
                <span className="text-text-base block text-xl font-bold">
                  4.9
                </span>
                <span className="text-text-muted block text-xs">Rating</span>
              </div>
              <div className="border-border border-l" />
              <div className="text-center">
                <span className="text-text-base block text-xl font-bold">
                  15+
                </span>
                <span className="text-text-muted block text-xs">Yrs Exp</span>
              </div>
            </div>
          </div>

          {/* Bio */}
          <div className="border-border mt-4 border-t border-dashed pt-3">
            <p className="text-text-muted line-clamp-2 text-sm">
              Experienced practitioner dedicated to providing comprehensive,
              patient-centered care. Focused on preventative medicine and
              holistic wellness.
            </p>
          </div>
        </div>
      </div>
    </Card>
  );
}

export default DoctorProfileCard;
