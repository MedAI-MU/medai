import Image from "next/image";
import StarRating from "./StarRating";
import Button from "./Button";

function DoctorCard({ doctor }) {
  const { image, name, specialty, rating } = doctor;

  return (
    <div className="rounded-xl border border-[#E2E8F0] bg-white p-6 shadow-[0_2px_4px_rgba(0,0,0,0.1)]">
      <div className="relative mx-auto mb-4 h-32 w-32 overflow-hidden rounded-full">
        <Image
          src="/doctor.jfif"
          className="h-full w-full object-cover"
          fill
          alt={name}
        />
      </div>
      <h3 className="text-primary-dark mb-2 text-center text-xl font-semibold">
        {name}
      </h3>
      <div className="mb-3 flex justify-center">
        <span className="text-primary-blue rounded-full bg-[#E3F2FD] px-3 py-1 text-sm font-medium">
          {specialty}
        </span>
      </div>
      <div className="mb-3 flex items-center justify-center gap-1">
        <StarRating size={16} defualtRate={5} isReadOnly={true} />
        <span className="text-primary-dark ml-2 text-sm font-medium">
          {rating}
        </span>
      </div>
      <div className="mb-4 flex justify-center">
        <span className="rounded-full bg-[#9AE6B4] px-3 py-1 text-xs font-medium text-[#22543D]">
          Available
        </span>
      </div>
      <Button className="w-full">Book Appointment</Button>
    </div>
  );
}

export default DoctorCard;
