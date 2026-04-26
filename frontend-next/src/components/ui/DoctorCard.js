import Image from "next/image";
import StarRating from "./StarRating";
import Button from "./Button";

function DoctorCard({ doctor }) {
  const { image, name, specialty, rating } = doctor;

  return (
    <div className="rounded-xl border border-border bg-surface p-6 shadow-md transition-colors duration-300">
      {/* Avatar */}
      <div className="relative mx-auto mb-4 h-32 w-32 overflow-hidden rounded-full">
        <Image
          src="/doctor.jfif"
          className="h-full w-full object-cover"
          fill
          alt={name}
        />
      </div>

      {/* Name */}
      <h3 className="mb-2 text-center text-xl font-semibold text-text-base">
        {name}
      </h3>

      {/* Specialty badge */}
      <div className="mb-3 flex justify-center">
        <span className="rounded-full bg-primary/10 px-3 py-1 text-sm font-medium text-primary">
          {specialty}
        </span>
      </div>

      {/* Rating */}
      <div className="mb-3 flex items-center justify-center gap-1">
        <StarRating size={16} defualtRate={5} isReadOnly={true} />
        <span className="ml-2 text-sm font-medium text-text-base">{rating}</span>
      </div>

      {/* Available badge */}
      <div className="mb-4 flex justify-center">
        <span className="rounded-full bg-success/15 px-3 py-1 text-xs font-medium text-success">
          Available
        </span>
      </div>

      <Button className="w-full">Book Appointment</Button>
    </div>
  );
}

export default DoctorCard;
