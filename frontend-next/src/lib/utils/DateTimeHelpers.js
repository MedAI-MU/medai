import { format } from "date-fns";

export function toMinutes(time) {
  return time
    .split(":")
    .map(Number)
    .reduce((h, m) => h * 60 + m);
}

export function isEndAfterStart(startTime, endTime) {
  return toMinutes(endTime) > toMinutes(startTime);
}

export function stripSeconds(time) {
  if (!time) return "";
  return time.slice(0, 5);
}

export function formatDate(date) {
  if (!date) return "-";
  const d = new Date(date);

  return format(d, "yyyy-MM-dd");
}
