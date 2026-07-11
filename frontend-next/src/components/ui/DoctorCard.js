import Image from "next/image";
import StarRating from "./StarRating";
import Button from "./Button";
import doctorImg from "@/assets/doctor.png";
import Badge from "./Badge";

function DoctorCard({ doctor }) {
  const { image, name, specialty, rating } = doctor;

  return (
    <div className="border-border bg-surface rounded-xl border p-6 shadow-md transition-colors duration-300">
      {/* Avatar */}
      <div className="relative mx-auto mb-4 h-32 w-32 overflow-hidden rounded-full">
        <Image
          src={doctorImg}
          className="h-full w-full object-cover"
          fill
          alt={name}
        />
      </div>

      {/* Name */}
      <h3 className="text-text-base mb-2 text-center text-xl font-semibold">
        {name}
      </h3>

      {/* Specialty badge */}
      <div className="mb-3 flex justify-center">
        <Badge text={specialty} color="blue" />
      </div>

      {/* Rating */}
      <div className="mb-3 flex items-center justify-center gap-1">
        <StarRating
          size={16}
          defualtRate={rating}
          isReadOnly={true}
          isLabelHidden
        />
        <span className="text-text-base ml-2 text-sm font-medium">
          {rating}.0 / 5.0
        </span>
      </div>

      {/* Available badge */}
      <div className="mb-4 flex justify-center">
        <Badge text="Available" color="success" />
      </div>

      <Button className="w-full">Book Appointment</Button>
    </div>
  );
}

export default DoctorCard;
