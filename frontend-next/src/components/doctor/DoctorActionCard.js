import Card from "@/components/ui/Card";
import Button from "@/components/ui/Button";

function DoctorActionCard({
  icon: Icon,
  title,
  description,
  href,
  actionLabel = "Manage",
}) {
  return (
    <Card className="flex flex-col gap-4">
      <div className="bg-primary/10 flex size-12 items-center justify-center rounded-lg">
        <Icon size={24} className="text-primary" />
      </div>
      <div className="space-y-1">
        <h3 className="text-text-base text-base font-semibold">{title}</h3>
        <p className="text-text-muted text-sm">{description}</p>
      </div>
      <Button href={href} className="mt-auto w-full sm:w-auto">
        {actionLabel}
      </Button>
    </Card>
  );
}

export default DoctorActionCard;
