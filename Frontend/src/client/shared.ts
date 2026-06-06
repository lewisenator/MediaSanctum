export type DataResponse<T> = {
  data?: T;
  error?: ErrorResponse;
};

export type Image = {
  id: string;
  url: string;
  color: string;
  width: number;
  height: number;
}

export type ErrorResponse = {
  error: string;
  message: string;
  timestamp: string;
};

export class ApiError extends Error {
  readonly status: number;
  readonly url: string;

  constructor(message: string, status: number, url: string) {
    super(message);
    this.name = 'ApiError';
    this.status = status;
    this.url = url;
  }
}

export const handleError = (err: any, defaultMessage: string): ApiError => {
  if (err instanceof ApiError) return err;
  const { status, statusText, data } = err.response;
  const url: string = err.config?.url ?? '';
  console.log(`Received a ${status} - ${statusText} - `, JSON.stringify(data));
  return new ApiError(data?.error?.message || defaultMessage, status, url);
};