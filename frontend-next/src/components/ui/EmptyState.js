import { FolderOpen, Share2, Users, HeartPulse, History } from "lucide-react";
import Heading from "./Heading";
import IconBadge from "./IconBadge";

export default function EmptyState({
  title,
  description,
  icon: Icon = <FolderOpen />,
  children,
}) {
  return (
    <div className="flex items-center justify-center py-10">
      <div className="border-border bg-surface flex w-full max-w-lg flex-1 flex-col items-center justify-center rounded-lg border p-12 shadow-sm transition-colors duration-300">
        <div className="mb-6 flex justify-center">
          <IconBadge color="blue" icon={Icon} isRounded />
        </div>

        <Heading
          className="mb-6 max-w-[480px] text-center"
          title={title}
          subtitle={description}
          size="lg"
          Tag="h3"
        />

        {children}
      </div>
    </div>
  );
}
