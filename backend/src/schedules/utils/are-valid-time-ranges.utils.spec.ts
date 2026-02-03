import { areValidTimeRanges } from './are-valid-time-ranges.utils';

describe('areValidTimeRanges', () => {
  it('should be defined', () => {
    expect(areValidTimeRanges).toBeDefined();
  });

  describe('valid time ranges - single day', () => {
    it('should return true for a single valid time range', () => {
      const ranges = [{ startTime: '09:00', endTime: '10:00', day: 1 }];
      expect(areValidTimeRanges(ranges)).toBe(true);
    });

    it('should return true for multiple non-overlapping time ranges on same day', () => {
      const ranges = [
        { startTime: '09:00', endTime: '10:00', day: 1 },
        { startTime: '11:00', endTime: '12:00', day: 1 },
        { startTime: '14:00', endTime: '15:00', day: 1 },
      ];
      expect(areValidTimeRanges(ranges)).toBe(true);
    });

    it('should return true for adjacent time ranges without overlap', () => {
      const ranges = [
        { startTime: '09:00', endTime: '10:00', day: 1 },
        { startTime: '10:00', endTime: '11:00', day: 1 },
        { startTime: '11:00', endTime: '12:00', day: 1 },
      ];
      expect(areValidTimeRanges(ranges)).toBe(true);
    });

    it('should return true for unsorted but valid time ranges on same day', () => {
      const ranges = [
        { startTime: '14:00', endTime: '15:00', day: 2 },
        { startTime: '09:00', endTime: '10:00', day: 2 },
        { startTime: '11:00', endTime: '12:00', day: 2 },
      ];
      expect(areValidTimeRanges(ranges)).toBe(true);
    });

    it('should return true for time ranges spanning across different hours', () => {
      const ranges = [
        { startTime: '08:30', endTime: '09:45', day: 3 },
        { startTime: '10:15', endTime: '11:30', day: 3 },
      ];
      expect(areValidTimeRanges(ranges)).toBe(true);
    });

    it('should return true for early morning time ranges', () => {
      const ranges = [
        { startTime: '00:00', endTime: '01:00', day: 0 },
        { startTime: '02:00', endTime: '03:00', day: 0 },
      ];
      expect(areValidTimeRanges(ranges)).toBe(true);
    });

    it('should return true for late night time ranges', () => {
      const ranges = [
        { startTime: '22:00', endTime: '23:00', day: 5 },
        { startTime: '23:00', endTime: '23:59', day: 5 },
      ];
      expect(areValidTimeRanges(ranges)).toBe(true);
    });
  });

  describe('valid time ranges - multiple days', () => {
    it('should return true for ranges on different days', () => {
      const ranges = [
        { startTime: '09:00', endTime: '10:00', day: 1 },
        { startTime: '09:00', endTime: '10:00', day: 2 },
        { startTime: '09:00', endTime: '10:00', day: 3 },
      ];
      expect(areValidTimeRanges(ranges)).toBe(true);
    });

    it('should return true for overlapping times on different days', () => {
      const ranges = [
        { startTime: '09:00', endTime: '12:00', day: 1 },
        { startTime: '10:00', endTime: '11:00', day: 2 },
      ];
      expect(areValidTimeRanges(ranges)).toBe(true);
    });

    it('should return true for same times across all days', () => {
      const ranges = [
        { startTime: '09:00', endTime: '17:00', day: 0 },
        { startTime: '09:00', endTime: '17:00', day: 1 },
        { startTime: '09:00', endTime: '17:00', day: 2 },
        { startTime: '09:00', endTime: '17:00', day: 3 },
        { startTime: '09:00', endTime: '17:00', day: 4 },
        { startTime: '09:00', endTime: '17:00', day: 5 },
        { startTime: '09:00', endTime: '17:00', day: 6 },
      ];
      expect(areValidTimeRanges(ranges)).toBe(true);
    });

    it('should return true for complex schedule across multiple days', () => {
      const ranges = [
        { startTime: '09:00', endTime: '12:00', day: 1 },
        { startTime: '14:00', endTime: '17:00', day: 1 },
        { startTime: '08:00', endTime: '11:00', day: 2 },
        { startTime: '13:00', endTime: '16:00', day: 2 },
        { startTime: '10:00', endTime: '15:00', day: 3 },
      ];
      expect(areValidTimeRanges(ranges)).toBe(true);
    });

    it('should return true for unsorted ranges across multiple days', () => {
      const ranges = [
        { startTime: '14:00', endTime: '17:00', day: 3 },
        { startTime: '09:00', endTime: '12:00', day: 1 },
        { startTime: '10:00', endTime: '15:00', day: 5 },
        { startTime: '08:00', endTime: '11:00', day: 2 },
      ];
      expect(areValidTimeRanges(ranges)).toBe(true);
    });
  });

  describe('invalid time ranges - start >= end', () => {
    it('should return false when startTime equals endTime', () => {
      const ranges = [{ startTime: '09:00', endTime: '09:00', day: 1 }];
      expect(areValidTimeRanges(ranges)).toBe(false);
    });

    it('should return false when startTime is after endTime', () => {
      const ranges = [{ startTime: '10:00', endTime: '09:00', day: 1 }];
      expect(areValidTimeRanges(ranges)).toBe(false);
    });

    it('should return false when one range has startTime after endTime in multiple ranges', () => {
      const ranges = [
        { startTime: '09:00', endTime: '10:00', day: 1 },
        { startTime: '12:00', endTime: '11:00', day: 1 },
        { startTime: '14:00', endTime: '15:00', day: 1 },
      ];
      expect(areValidTimeRanges(ranges)).toBe(false);
    });

    it('should return false when last range has startTime equal to endTime', () => {
      const ranges = [
        { startTime: '09:00', endTime: '10:00', day: 2 },
        { startTime: '11:00', endTime: '11:00', day: 2 },
      ];
      expect(areValidTimeRanges(ranges)).toBe(false);
    });

    it('should return false when first range has startTime after endTime', () => {
      const ranges = [
        { startTime: '10:00', endTime: '09:00', day: 3 },
        { startTime: '11:00', endTime: '12:00', day: 3 },
      ];
      expect(areValidTimeRanges(ranges)).toBe(false);
    });

    it('should return false when invalid time exists on different day', () => {
      const ranges = [
        { startTime: '09:00', endTime: '10:00', day: 1 },
        { startTime: '15:00', endTime: '14:00', day: 2 },
      ];
      expect(areValidTimeRanges(ranges)).toBe(false);
    });
  });

  describe('invalid time ranges - overlapping on same day', () => {
    it('should return false when two ranges overlap on same day', () => {
      const ranges = [
        { startTime: '09:00', endTime: '11:00', day: 1 },
        { startTime: '10:00', endTime: '12:00', day: 1 },
      ];
      expect(areValidTimeRanges(ranges)).toBe(false);
    });

    it('should return false when second range is completely inside first range', () => {
      const ranges = [
        { startTime: '09:00', endTime: '12:00', day: 1 },
        { startTime: '10:00', endTime: '11:00', day: 1 },
      ];
      expect(areValidTimeRanges(ranges)).toBe(false);
    });

    it('should return false when first range is completely inside second range', () => {
      const ranges = [
        { startTime: '10:00', endTime: '11:00', day: 1 },
        { startTime: '09:00', endTime: '12:00', day: 1 },
      ];
      expect(areValidTimeRanges(ranges)).toBe(false);
    });

    it('should return false when multiple ranges have overlaps on same day', () => {
      const ranges = [
        { startTime: '09:00', endTime: '10:30', day: 2 },
        { startTime: '10:00', endTime: '11:30', day: 2 },
        { startTime: '11:00', endTime: '12:00', day: 2 },
      ];
      expect(areValidTimeRanges(ranges)).toBe(false);
    });

    it('should return false when ranges overlap by one minute', () => {
      const ranges = [
        { startTime: '09:00', endTime: '10:01', day: 3 },
        { startTime: '10:00', endTime: '11:00', day: 3 },
      ];
      expect(areValidTimeRanges(ranges)).toBe(false);
    });

    it('should return false when unsorted ranges have overlaps on same day', () => {
      const ranges = [
        { startTime: '14:00', endTime: '16:00', day: 4 },
        { startTime: '09:00', endTime: '10:00', day: 4 },
        { startTime: '15:00', endTime: '17:00', day: 4 },
      ];
      expect(areValidTimeRanges(ranges)).toBe(false);
    });

    it('should return false when third range overlaps with second range', () => {
      const ranges = [
        { startTime: '09:00', endTime: '10:00', day: 1 },
        { startTime: '11:00', endTime: '12:00', day: 1 },
        { startTime: '11:30', endTime: '13:00', day: 1 },
      ];
      expect(areValidTimeRanges(ranges)).toBe(false);
    });

    it('should return false for partial overlap at end of range', () => {
      const ranges = [
        { startTime: '09:00', endTime: '10:30', day: 1 },
        { startTime: '10:00', endTime: '11:00', day: 1 },
      ];
      expect(areValidTimeRanges(ranges)).toBe(false);
    });
  });

  describe('overlaps across different days', () => {
    it('should return true for overlapping times on different days', () => {
      const ranges = [
        { startTime: '09:00', endTime: '12:00', day: 1 },
        { startTime: '10:00', endTime: '11:00', day: 2 },
      ];
      expect(areValidTimeRanges(ranges)).toBe(true);
    });

    it('should return false when overlaps exist on one day but not others', () => {
      const ranges = [
        { startTime: '09:00', endTime: '12:00', day: 1 },
        { startTime: '14:00', endTime: '17:00', day: 1 },
        { startTime: '09:00', endTime: '12:00', day: 2 },
        { startTime: '10:00', endTime: '11:00', day: 2 },
      ];
      expect(areValidTimeRanges(ranges)).toBe(false);
    });

    it('should handle complex mixed valid and invalid schedules', () => {
      const ranges = [
        { startTime: '09:00', endTime: '12:00', day: 1 },
        { startTime: '13:00', endTime: '17:00', day: 1 },
        { startTime: '09:00', endTime: '11:00', day: 2 },
        { startTime: '10:00', endTime: '12:00', day: 2 }, // Overlaps on day 2
      ];
      expect(areValidTimeRanges(ranges)).toBe(false);
    });
  });

  describe('Date string type handling (yyyy-mm-dd format)', () => {
    it('should group by date string (yyyy-mm-dd format)', () => {
      const ranges = [
        { startTime: '09:00', endTime: '10:00', day: '2024-01-15' },
        { startTime: '11:00', endTime: '12:00', day: '2024-01-15' },
      ];
      expect(areValidTimeRanges(ranges)).toBe(true);
    });

    it('should return false when date string ranges overlap on same date', () => {
      const ranges = [
        { startTime: '09:00', endTime: '11:00', day: '2024-01-15' },
        { startTime: '10:00', endTime: '12:00', day: '2024-01-15' },
      ];
      expect(areValidTimeRanges(ranges)).toBe(false);
    });

    it('should allow overlapping times on different date strings', () => {
      const ranges = [
        { startTime: '09:00', endTime: '12:00', day: '2024-01-15' },
        { startTime: '10:00', endTime: '11:00', day: '2024-01-16' },
      ];
      expect(areValidTimeRanges(ranges)).toBe(true);
    });

    it('should return false when date string ranges have invalid time', () => {
      const ranges = [
        { startTime: '10:00', endTime: '09:00', day: '2024-01-15' },
      ];
      expect(areValidTimeRanges(ranges)).toBe(false);
    });

    it('should correctly group multiple ranges by different date strings', () => {
      const ranges = [
        { startTime: '09:00', endTime: '10:00', day: '2024-01-15' },
        { startTime: '09:00', endTime: '10:00', day: '2024-01-16' },
        { startTime: '09:00', endTime: '10:00', day: '2024-01-17' },
      ];
      expect(areValidTimeRanges(ranges)).toBe(true);
    });

    it('should detect overlaps within same date string but not across date strings', () => {
      const ranges = [
        { startTime: '09:00', endTime: '12:00', day: '2024-01-15' },
        { startTime: '11:00', endTime: '14:00', day: '2024-01-15' }, // Overlaps on same date
        { startTime: '11:00', endTime: '12:00', day: '2024-01-16' }, // OK on different date
      ];
      expect(areValidTimeRanges(ranges)).toBe(false);
    });
  });

  describe('edge cases', () => {
    it('should return true for empty array', () => {
      const ranges: { startTime: string; endTime: string; day: number }[] = [];
      expect(areValidTimeRanges(ranges)).toBe(true);
    });

    it('should handle time ranges with leading zeros', () => {
      const ranges = [
        { startTime: '08:00', endTime: '09:00', day: 1 },
        { startTime: '09:00', endTime: '10:00', day: 1 },
      ];
      expect(areValidTimeRanges(ranges)).toBe(true);
    });

    it('should handle many non-overlapping ranges on same day', () => {
      const ranges = [
        { startTime: '06:00', endTime: '07:00', day: 1 },
        { startTime: '07:00', endTime: '08:00', day: 1 },
        { startTime: '08:00', endTime: '09:00', day: 1 },
        { startTime: '09:00', endTime: '10:00', day: 1 },
        { startTime: '10:00', endTime: '11:00', day: 1 },
        { startTime: '11:00', endTime: '12:00', day: 1 },
        { startTime: '12:00', endTime: '13:00', day: 1 },
        { startTime: '13:00', endTime: '14:00', day: 1 },
      ];
      expect(areValidTimeRanges(ranges)).toBe(true);
    });

    it('should handle complex unsorted input on same day', () => {
      const ranges = [
        { startTime: '18:00', endTime: '19:00', day: 3 },
        { startTime: '08:00', endTime: '09:00', day: 3 },
        { startTime: '14:00', endTime: '15:00', day: 3 },
        { startTime: '10:00', endTime: '11:00', day: 3 },
      ];
      expect(areValidTimeRanges(ranges)).toBe(true);
    });

    it('should return false for ranges with minute-level overlap in unsorted input', () => {
      const ranges = [
        { startTime: '18:00', endTime: '19:00', day: 2 },
        { startTime: '08:00', endTime: '09:00', day: 2 },
        { startTime: '18:30', endTime: '19:30', day: 2 },
      ];
      expect(areValidTimeRanges(ranges)).toBe(false);
    });

    it('should handle minimum time range (1 minute)', () => {
      const ranges = [
        { startTime: '09:00', endTime: '09:01', day: 1 },
        { startTime: '09:01', endTime: '09:02', day: 1 },
      ];
      expect(areValidTimeRanges(ranges)).toBe(true);
    });

    it('should handle maximum day range (full day)', () => {
      const ranges = [{ startTime: '00:00', endTime: '23:59', day: 1 }];
      expect(areValidTimeRanges(ranges)).toBe(true);
    });

    it('should handle all days (0-6)', () => {
      const ranges = [
        { startTime: '09:00', endTime: '10:00', day: 0 }, // Sunday
        { startTime: '09:00', endTime: '10:00', day: 1 }, // Monday
        { startTime: '09:00', endTime: '10:00', day: 2 }, // Tuesday
        { startTime: '09:00', endTime: '10:00', day: 3 }, // Wednesday
        { startTime: '09:00', endTime: '10:00', day: 4 }, // Thursday
        { startTime: '09:00', endTime: '10:00', day: 5 }, // Friday
        { startTime: '09:00', endTime: '10:00', day: 6 }, // Saturday
      ];
      expect(areValidTimeRanges(ranges)).toBe(true);
    });

    it('should handle midnight edge case', () => {
      const ranges = [
        { startTime: '23:00', endTime: '23:59', day: 1 },
        { startTime: '00:00', endTime: '01:00', day: 1 },
      ];
      expect(areValidTimeRanges(ranges)).toBe(true);
    });

    it('should return false when identical ranges exist on same day', () => {
      const ranges = [
        { startTime: '09:00', endTime: '10:00', day: 1 },
        { startTime: '09:00', endTime: '10:00', day: 1 },
      ];
      expect(areValidTimeRanges(ranges)).toBe(false);
    });

    it('should return true when identical ranges exist on different days', () => {
      const ranges = [
        { startTime: '09:00', endTime: '10:00', day: 1 },
        { startTime: '09:00', endTime: '10:00', day: 2 },
      ];
      expect(areValidTimeRanges(ranges)).toBe(true);
    });
  });
});
