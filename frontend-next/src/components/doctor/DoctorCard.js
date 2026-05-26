import Badge from "@/components/ui/Badge";
import Button from "@/components/ui/Button";
import Card from "@/components/ui/Card";
import Heading from "@/components/ui/Heading";

function getInitials(name = "") {
  return name
    .trim()
    .split(/\s+/)
    .slice(0, 2)
    .map((w) => w[0]?.toUpperCase() || "")
    .join("");
}

function DoctorCard({ doctor }) {
  const { specialities = [], name, userId } = doctor || {};

  const initial = getInitials(name);

  return (
    <Card>
      <div className="mb-4 flex flex-col items-center gap-4 text-center sm:flex-row sm:items-start sm:text-start">
        <div className="bg-primary flex size-16 shrink-0 items-center justify-center rounded-full text-white">
          {initial}
        </div>
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
        <Button>Book Appointment</Button>
        <Button variation="secondary">View Profile</Button>
      </footer>
    </Card>
  );
}

export default DoctorCard;
