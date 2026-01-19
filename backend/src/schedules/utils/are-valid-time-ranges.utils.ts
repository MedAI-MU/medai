export function areValidTimeRanges(
  ranges: { startTime: string; endTime: string }[],
): boolean {
  // Check if all ranges have valid start/end times
  for (const range of ranges) {
    if (range.startTime >= range.endTime) {
      return false;
    }
  }

  // Check for overlaps between ranges
  const sorted = ranges
    .map((r, i) => ({ ...r, index: i }))
    .sort((a, b) => a.startTime.localeCompare(b.startTime));

  for (let i = 0; i < sorted.length - 1; i++) {
    if (sorted[i].endTime > sorted[i + 1].startTime) {
      return false;
    }
  }

  return true;
}
