export function toMinutes(time) {
  return time
    .split(":")
    .map(Number)
    .reduce((h, m) => h * 60 + m);
}

export function isEndAfterStart(startTime, endTime) {
  return toMinutes(endTime) > toMinutes(startTime);
}
