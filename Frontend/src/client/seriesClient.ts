import api from "#/config/axios.ts";
import type { AxiosResponse } from 'axios';
import { handleError, type DataResponse } from '#/client/shared.ts';

export type Series = {
  hardcoverId: number;
  name: string;
  slug: string;
  isCompleted: boolean | null;
  booksCount: number | null;
  libraryBooksCount: number;
};

export const getSeries = async (): Promise<Array<Series>> => {
  try {
    const res: AxiosResponse<DataResponse<Array<Series>>> = await api.get('/series');
    return res.data.data!;
  } catch (err: any) {
    throw handleError(err, 'Failed to fetch series');
  }
}
