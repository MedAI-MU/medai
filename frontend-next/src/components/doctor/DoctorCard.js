"use client";

import Badge from "@/components/ui/Badge";
import Button from "@/components/ui/Button";
import Card from "@/components/ui/Card";
import Heading from "@/components/ui/Heading";
import UserFallback from "../ui/UserFallback";
import StarRating from "../ui/StarRating";

function DoctorCard({
  doctor,
  actionLabel = "Book Appointment",
  basePath = "/patient/book-appointment",
}) {
  const {
    specialities = [],
    name,
    userId: doctorId,
    rating,
    bestReview,
    about,
  } = doctor || {};

  return (
    <Card>
      <div className="mb-4 flex flex-col items-center gap-4 text-center sm:flex-row sm:items-start sm:text-start">
        <UserFallback name={name} />
        <div>
          <Heading size="sm" Tag="h3" title={`Dr. ${name}`} />
          <Badge color="blue" text="Doctor" />
        </div>
      </div>

      {about && (
        <p className="text-text-muted mb-3 line-clamp-2 text-sm">{about}</p>
      )}

      <div className="flex flex-wrap gap-2">
        {specialities?.length === 0 ? (
          <span className="text-text-muted">
            <em>No specialities listed</em>
          </span>
        ) : (
          specialities.map((spec) => (
            <Badge key={spec?.id} text={spec?.speciality?.name} color="slate" />
          ))
        )}
      </div>

      {rating > 0 && (
        <div className="mt-3 flex items-center gap-2">
          <StarRating
            defualtRate={Math.round(rating)}
            isViewOnly
            isLabelHidden
          />
          <span className="text-text-subtle text-sm">{rating.toFixed(1)}</span>
        </div>
      )}

      {bestReview && (
        <p className="text-text-muted mt-2 text-xs italic line-clamp-2">
          &quot;{bestReview}&quot;
        </p>
      )}

      <footer className="border-border mt-4 border-t pt-4">
        <Button href={`${basePath}/${doctorId}`}>{actionLabel}</Button>
      </footer>
    </Card>
  );
}

export default DoctorCard;
