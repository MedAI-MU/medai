import { format, parse } from "date-fns";

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

export function formatTime12h(time) {
  if (!time) return "--:--";
  const [hourStr, minute] = time.split(":");
  const hour = parseInt(hourStr, 10);
  const ampm = hour >= 12 ? "PM" : "AM";
  const formattedHour = hour % 12 === 0 ? 12 : hour % 12;
  return `${formattedHour}:${minute} ${ampm}`;
}

export function getUniqueDays(slots) {
  if (!slots) return [];
  return [...new Set(slots?.map((slot) => slot.weekDay))];
}

export function parseDate(strDate) {
  if (!strDate) return new Date();
  return parse(strDate, "yyyy-MM-dd", new Date());
}
