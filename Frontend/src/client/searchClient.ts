import api from "#/config/axios.ts";
import type { AxiosResponse } from 'axios';
import { handleError, type DataResponse } from '#/client/shared.ts';

export type SearchResults<T> = {
  found: number;
  page: number;
  perPage: number;
  hits: T[];
};

export type BookResult = {
  hardcoverId: string;
  title: string;
  authors: string[];
  description: string;
  featuredSeriesName: string;
  featuredSeriesPosition: number;
  imageUrl: string;
  releaseYear: number;
};

export const searchBooks = async (query: string): Promise<SearchResults<BookResult>> => {
  try {
    const res: AxiosResponse<DataResponse<SearchResults<BookResult>>> = await api.post(`/search/books?q=${query}`);
    return res.data.data!;
  } catch (err: any) {
    throw handleError(err, 'Search failed');
  }
}