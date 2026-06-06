import api from "#/config/axios.ts";
import type { AxiosResponse } from 'axios';
import { handleError, type DataResponse, type Image } from '#/client/shared.ts';
import type { Author } from '#/client/authorClient.ts';

export type Book = {
  id: string;
  hardcoverId: string;
  headline: string;
  title: string;
  slug: string;
  subtitle: string;
  description: string;
  releaseYear: number;
  pages: number;
  audioSeconds: number;
  createdAt: string;
  updatedAt: string;
  ebookEdition?: Edition;
  audiobookEdition?: Edition;
  author: Author;
  featuredSeries: FeaturedSeries;
  ebookFile: BookFile;
  audiobookFile: BookFile;
};

export type Edition = {
  id: string;
  asin: string;
  isbn10: string;
  isbn20: string;
  language: string;
  country: string;
  editionType: string;
  pages?: number;
  audioSeconds?: number;
  image?: Image;
}

export type Series = {
  id: number;
  name: string;
  booksCount: number;
  primaryBooksCount: number;
  slug: string;
  isCompleted: boolean;
};

export type FeaturedSeries = {
  id: string;
  position: number;
  series: Series;
}

export type BookFile = {
  id: string;
  size: number;
  filename: string;
  url: string;
  contentType: string;
  editionType: string;
  createdAt: string;
  updatedAt: string;
};

export const addBook = async (hardcoverId: string): Promise<Book> => {
  try {
    const res: AxiosResponse<DataResponse<Book>> = await api.post('/books', {
      hardcoverId
    });
    return res.data.data!;
  } catch (err: any) {
    throw handleError(err, 'Failed to add book');
  }
}

export const getBooks = async (): Promise<Array<Book>> => {
  try {
    const res: AxiosResponse<DataResponse<Array<Book>>> = await api.get('/books');
    return res.data.data!;
  } catch (err: any) {
    throw handleError(err, 'Failed to fetch books');
  }
}

export const getBook = async (id: string): Promise<Book> => {
  try {
    const res: AxiosResponse<DataResponse<Book>> = await api.get(`/books/${id}`);
    return res.data.data!;
  } catch (err: any) {
    throw handleError(err, 'Failed to fetch book');
  }
}

export const uploadEbook = async (id: string, ebook: File): Promise<Book> => {
  try {
    const formData = new FormData();
    formData.append('file', ebook);
    const res: AxiosResponse<DataResponse<Book>> = await api.post(`/books/${id}/ebook/upload`, formData, {
      headers: {
        'Content-Type': 'multipart/form-data'
      }
    });
    return res.data.data!;
  } catch (err: any) {
    throw handleError(err, 'Failed to fetch book');
  }
};