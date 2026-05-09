import {
  Plus,
  Info,
  FolderOpen,
  Share2,
  Users,
  HeartPulse,
  History,
} from "lucide-react";
import Heading from "./Heading";

export default function EmptyState({
  title,
  description,
  icon: Icon = FolderOpen,
  backgroundIcons = [Share2, History, Users, HeartPulse],
  children,
}) {
  return (
    <div className="border-border bg-surface flex flex-1 flex-col items-center justify-center rounded-lg border px-4 py-16 shadow-sm transition-colors duration-300">
      <div className="relative mb-8">
        <div className="bg-surface-overlay/50 relative flex h-64 w-64 items-center justify-center overflow-hidden rounded-full">
          {/* الأيقونات العائمة في الخلفية */}
          <div className="text-text-subtle absolute inset-0 flex scale-110 rotate-12 flex-wrap items-center justify-center gap-4 opacity-10">
            {backgroundIcons.map((BgIcon, idx) => (
              <BgIcon key={idx} size={60} strokeWidth={1.5} />
            ))}
          </div>

          {/* المحتوى المركزي */}
          <div className="relative z-10 flex flex-col items-center">
            {/* الدائرة المحيطة بالأيقونة الرئيسية */}
            <div className="bg-primary/10 mb-2 flex size-32 items-center justify-center rounded-full">
              <Icon className="text-primary" size={64} strokeWidth={1.5} />
            </div>
            {/* خطوط ديكورية */}
            <div className="bg-border mb-1 h-1.5 w-24 rounded-full"></div>
            <div className="bg-border h-1.5 w-16 rounded-full opacity-60"></div>
          </div>
        </div>
      </div>

      <Heading
        className="mb-8 max-w-[480px] text-center"
        title={title}
        subtitle={description}
      />

      {children}
    </div>
  );
}
