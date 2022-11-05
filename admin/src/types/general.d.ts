export interface IRequestState<T> {
  data: T | null;
  loading: boolean;
  error: string | null;
}
