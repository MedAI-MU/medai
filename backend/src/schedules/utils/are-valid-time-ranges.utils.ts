export function areValidTimeRanges(
  ranges: { startTime: string; endTime: string; day: number | string }[],
): boolean {
  // Check if each range has valid start and end times
  for (const range of ranges) {
    if (range.startTime >= range.endTime) {
      return false;
    }
  }

  // Group ranges by day (number for weekday or string for date in yyyy-mm-dd format) to check for overlaps
  const rangesByDay = new Map<
    string | number,
    { startTime: string; endTime: string }[]
  >();

  for (const range of ranges) {
    // Use day as is (either weekday number or date string in yyyy-mm-dd format)
    const groupKey = range.day;
    if (!rangesByDay.has(groupKey)) {
      rangesByDay.set(groupKey, []);
    }
    rangesByDay.get(groupKey)!.push({
      startTime: range.startTime,
      endTime: range.endTime,
    });
  }

  // Check for overlaps within each day
  for (const [, dayRanges] of rangesByDay) {
    // Sort ranges by start time
    const sortedRanges = [...dayRanges].sort((a, b) =>
      a.startTime.localeCompare(b.startTime),
    );

    // Check for overlaps between consecutive ranges
    for (let i = 0; i < sortedRanges.length - 1; i++) {
      const current = sortedRanges[i];
      const next = sortedRanges[i + 1];

      // Check if current range overlaps with next range
      if (current.endTime > next.startTime) {
        return false;
      }
    }
  }

  return true;
}
