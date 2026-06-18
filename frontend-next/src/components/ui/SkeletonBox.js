import { cn } from "@/lib/utils";

export default function SkeletonBox({ className = "" }) {
  return (
    <div className={cn(`bg-border animate-pulse rounded-md`, className)} />
  );
}
