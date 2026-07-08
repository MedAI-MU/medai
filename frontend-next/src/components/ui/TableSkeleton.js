import SkeletonBox from "@/components/ui/SkeletonBox";

export default function TableSkeleton({ columns = "", rows = 4 }) {
  const colCount = columns.split(" ").length;

  return (
    <div className="bg-surface border-border overflow-hidden rounded-xl border shadow-md">
      <div
        className={`bg-surface-overlay border-border grid gap-6 border-b px-6 py-4`}
        style={{ gridTemplateColumns: columns }}
      >
        {Array.from({ length: colCount }).map((_, i) => (
          <SkeletonBox key={i} className="h-3 w-full rounded" />
        ))}
      </div>

      {Array.from({ length: rows }).map((_, i) => (
        <div
          key={i}
          className={`border-border grid items-center gap-6 border-b px-6 py-4 last:border-b-0`}
          style={{ gridTemplateColumns: columns }}
        >
          {Array.from({ length: colCount }).map((_, j) => (
            <SkeletonBox
              key={j}
              className={`h-5 rounded ${j === colCount - 1 ? "w-16" : "w-3/4"}`}
            />
          ))}
        </div>
      ))}
    </div>
  );
}
