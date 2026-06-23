import Badge from "@/components/ui/Badge";
import Button from "@/components/ui/Button";
import Card from "@/components/ui/Card";
import Heading from "@/components/ui/Heading";
import UserFallback from "../ui/UserFallback";

function DoctorCard({ doctor }) {
  const { specialities = [], name, userId: doctorId } = doctor || {};

  return (
    <Card>
      <div className="mb-4 flex flex-col items-center gap-4 text-center sm:flex-row sm:items-start sm:text-start">
        <UserFallback name={name} />
        <div>
          <Heading size="sm" Tag="h3" title={`Dr. ${name}`} />
          <Badge color="blue" text="Doctor" />
        </div>
      </div>

      <div className="flex flex-wrap gap-2">
        {specialities?.length === 0 ? (
          <span className="text-text-muted">
            <em>No specialities listed</em>
          </span>
        ) : (
          specialities.map((spec) => (
            <Badge key={spec} text={spec} color="slate" isRounded={false} />
          ))
        )}
      </div>
      <footer className="border-border mt-4 flex flex-col gap-3 border-t pt-4">
        <Button href={`/patient/book-appointment/${doctorId}`}>
          Book Appointment
        </Button>
        <Button variation="secondary">View Profile</Button>
      </footer>
    </Card>
  );
}

export default DoctorCard;
