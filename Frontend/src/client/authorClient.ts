import api from "#/config/axios.ts";
import type { AxiosResponse } from 'axios';
import { handleError, type DataResponse, type Image } from '#/client/shared.ts';

export type AuthorLink = {
  url: string;
  title: string;
}

export type Author = {
  id: string;
  name: string;
  title: string;
  alternateNames: string[];
  slug: string;
  bio: string;
  bornYear: number;
  deathYear: number;
  booksCount: number;
  links: AuthorLink[];
  createdAt: string;
  updatedAt: string;
  image?: Image;
};

export const addAuthor = async (hardcoverId: string): Promise<void> => {
  try {
    await api.post('/authors', {
      hardcoverId
    });
  } catch (err: any) {
    throw handleError(err, 'Failed to add author');
  }
}

export const getAuthors = async (): Promise<Array<Author>> => {
  try {
    const res: AxiosResponse<DataResponse<Array<Author>>> = await api.get('/authors');
    return res.data.data!;
  } catch (err: any) {
    throw handleError(err, 'Failed to fetch authors');
  }
}

export const getAuthor = async (id: string): Promise<Author> => {
  try {
    const res: AxiosResponse<DataResponse<Author>> = await api.get(`/authors/${id}`);
    return res.data.data!;
  } catch (err: any) {
    throw handleError(err, 'Failed to fetch author');
  }
}