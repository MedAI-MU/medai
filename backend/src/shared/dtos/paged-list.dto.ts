export class PagedListDto<T> {
  hasNext: boolean;
  hasPrevious: boolean;

  constructor(
    public data: T[],
    public totalCount: number,
    public currentPage: number,
    public pageSize: number,
  ) {
    this.hasNext = currentPage * pageSize < totalCount;
    this.hasPrevious = currentPage > 1;
  }
}
