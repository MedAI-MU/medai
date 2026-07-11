import {
  Droplets,
  VenusAndMars,
  User,
  Ruler,
  Weight,
  Heart,
} from "lucide-react";
import Badge from "@/components/ui/Badge";
import Card from "@/components/ui/Card";
import Grid from "@/components/ui/Grid";
import Heading from "@/components/ui/Heading";
import InfoRow from "@/components/ui/InfoRow";

export default function PersonalInfoView({ data }) {
  return (
    <Card className="p-6">
      <Heading size="lg" title="Personal Health Information" className="mb-6" />
      <Grid cols="two" gap="gap-x-12 gap-y-4">
        <InfoRow icon={User} value={data?.name || "-"} />
        <InfoRow icon={VenusAndMars} value={data?.gender || "Not specified"} />
        <InfoRow
          icon={Droplets}
          value={
            data?.bloodType ? <Badge color="red" text={data.bloodType} /> : "-"
          }
        />
        <InfoRow
          icon={Ruler}
          value={data?.height ? `${data.height} cm` : "-"}
        />
        <InfoRow
          icon={Weight}
          value={data?.weight ? `${data.weight} kg` : "-"}
        />
        <InfoRow icon={Heart} value={data?.maritalStatus || "-"} />
      </Grid>
    </Card>
  );
}
