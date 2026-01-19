import { areValidTimeRanges } from './are-valid-time-ranges.utils';

describe('areValidTimeRanges', () => {
  it('should be defined', () => {
    expect(areValidTimeRanges).toBeDefined();
  });

  describe('valid time ranges', () => {
    it('should return true for a single valid time range', () => {
      const ranges = [{ startTime: '09:00', endTime: '10:00' }];
      expect(areValidTimeRanges(ranges)).toBe(true);
    });

    it('should return true for multiple non-overlapping time ranges', () => {
      const ranges = [
        { startTime: '09:00', endTime: '10:00' },
        { startTime: '11:00', endTime: '12:00' },
        { startTime: '14:00', endTime: '15:00' },
      ];
      expect(areValidTimeRanges(ranges)).toBe(true);
    });

    it('should return true for adjacent time ranges without overlap', () => {
      const ranges = [
        { startTime: '09:00', endTime: '10:00' },
        { startTime: '10:00', endTime: '11:00' },
        { startTime: '11:00', endTime: '12:00' },
      ];
      expect(areValidTimeRanges(ranges)).toBe(true);
    });

    it('should return true for unsorted but valid time ranges', () => {
      const ranges = [
        { startTime: '14:00', endTime: '15:00' },
        { startTime: '09:00', endTime: '10:00' },
        { startTime: '11:00', endTime: '12:00' },
      ];
      expect(areValidTimeRanges(ranges)).toBe(true);
    });

    it('should return true for time ranges spanning across different hours', () => {
      const ranges = [
        { startTime: '08:30', endTime: '09:45' },
        { startTime: '10:15', endTime: '11:30' },
      ];
      expect(areValidTimeRanges(ranges)).toBe(true);
    });

    it('should return true for early morning time ranges', () => {
      const ranges = [
        { startTime: '00:00', endTime: '01:00' },
        { startTime: '02:00', endTime: '03:00' },
      ];
      expect(areValidTimeRanges(ranges)).toBe(true);
    });

    it('should return true for late night time ranges', () => {
      const ranges = [
        { startTime: '22:00', endTime: '23:00' },
        { startTime: '23:00', endTime: '23:59' },
      ];
      expect(areValidTimeRanges(ranges)).toBe(true);
    });

    it('should return true for time ranges with seconds', () => {
      const ranges = [
        { startTime: '09:00:00', endTime: '10:00:00' },
        { startTime: '11:00:00', endTime: '12:00:00' },
      ];
      expect(areValidTimeRanges(ranges)).toBe(true);
    });
  });

  describe('invalid time ranges - start >= end', () => {
    it('should return false when startTime equals endTime', () => {
      const ranges = [{ startTime: '09:00', endTime: '09:00' }];
      expect(areValidTimeRanges(ranges)).toBe(false);
    });

    it('should return false when startTime is after endTime', () => {
      const ranges = [{ startTime: '10:00', endTime: '09:00' }];
      expect(areValidTimeRanges(ranges)).toBe(false);
    });

    it('should return false when one range has startTime after endTime in multiple ranges', () => {
      const ranges = [
        { startTime: '09:00', endTime: '10:00' },
        { startTime: '12:00', endTime: '11:00' },
        { startTime: '14:00', endTime: '15:00' },
      ];
      expect(areValidTimeRanges(ranges)).toBe(false);
    });

    it('should return false when last range has startTime equal to endTime', () => {
      const ranges = [
        { startTime: '09:00', endTime: '10:00' },
        { startTime: '11:00', endTime: '11:00' },
      ];
      expect(areValidTimeRanges(ranges)).toBe(false);
    });

    it('should return false when first range has startTime after endTime', () => {
      const ranges = [
        { startTime: '10:00', endTime: '09:00' },
        { startTime: '11:00', endTime: '12:00' },
      ];
      expect(areValidTimeRanges(ranges)).toBe(false);
    });
  });

  describe('invalid time ranges - overlapping', () => {
    it('should return false when two ranges overlap', () => {
      const ranges = [
        { startTime: '09:00', endTime: '11:00' },
        { startTime: '10:00', endTime: '12:00' },
      ];
      expect(areValidTimeRanges(ranges)).toBe(false);
    });

    it('should return false when second range is completely inside first range', () => {
      const ranges = [
        { startTime: '09:00', endTime: '12:00' },
        { startTime: '10:00', endTime: '11:00' },
      ];
      expect(areValidTimeRanges(ranges)).toBe(false);
    });

    it('should return false when first range is completely inside second range', () => {
      const ranges = [
        { startTime: '10:00', endTime: '11:00' },
        { startTime: '09:00', endTime: '12:00' },
      ];
      expect(areValidTimeRanges(ranges)).toBe(false);
    });

    it('should return false when multiple ranges have overlaps', () => {
      const ranges = [
        { startTime: '09:00', endTime: '10:30' },
        { startTime: '10:00', endTime: '11:30' },
        { startTime: '11:00', endTime: '12:00' },
      ];
      expect(areValidTimeRanges(ranges)).toBe(false);
    });

    it('should return false when ranges overlap by one minute', () => {
      const ranges = [
        { startTime: '09:00', endTime: '10:01' },
        { startTime: '10:00', endTime: '11:00' },
      ];
      expect(areValidTimeRanges(ranges)).toBe(false);
    });

    it('should return false when unsorted ranges have overlaps', () => {
      const ranges = [
        { startTime: '14:00', endTime: '16:00' },
        { startTime: '09:00', endTime: '10:00' },
        { startTime: '15:00', endTime: '17:00' },
      ];
      expect(areValidTimeRanges(ranges)).toBe(false);
    });

    it('should return false when third range overlaps with second range', () => {
      const ranges = [
        { startTime: '09:00', endTime: '10:00' },
        { startTime: '11:00', endTime: '12:00' },
        { startTime: '11:30', endTime: '13:00' },
      ];
      expect(areValidTimeRanges(ranges)).toBe(false);
    });
  });

  describe('edge cases', () => {
    it('should return true for empty array', () => {
      const ranges: { startTime: string; endTime: string }[] = [];
      expect(areValidTimeRanges(ranges)).toBe(true);
    });

    it('should handle time ranges with leading zeros', () => {
      const ranges = [
        { startTime: '08:00', endTime: '09:00' },
        { startTime: '09:00', endTime: '10:00' },
      ];
      expect(areValidTimeRanges(ranges)).toBe(true);
    });

    it('should handle many non-overlapping ranges', () => {
      const ranges = [
        { startTime: '06:00', endTime: '07:00' },
        { startTime: '07:00', endTime: '08:00' },
        { startTime: '08:00', endTime: '09:00' },
        { startTime: '09:00', endTime: '10:00' },
        { startTime: '10:00', endTime: '11:00' },
        { startTime: '11:00', endTime: '12:00' },
        { startTime: '12:00', endTime: '13:00' },
        { startTime: '13:00', endTime: '14:00' },
      ];
      expect(areValidTimeRanges(ranges)).toBe(true);
    });

    it('should handle complex unsorted input', () => {
      const ranges = [
        { startTime: '18:00', endTime: '19:00' },
        { startTime: '08:00', endTime: '09:00' },
        { startTime: '14:00', endTime: '15:00' },
        { startTime: '10:00', endTime: '11:00' },
      ];
      expect(areValidTimeRanges(ranges)).toBe(true);
    });

    it('should return false for ranges with minute-level overlap in unsorted input', () => {
      const ranges = [
        { startTime: '18:00', endTime: '19:00' },
        { startTime: '08:00', endTime: '09:00' },
        { startTime: '18:30', endTime: '19:30' },
      ];
      expect(areValidTimeRanges(ranges)).toBe(false);
    });
  });
});
