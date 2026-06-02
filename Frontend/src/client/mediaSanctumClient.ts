import api from "#/config/axios.ts";
import type { AxiosResponse } from 'axios';

export type HealthResponse = {
    groups: string[];
    status: string;
};

export const health = async (): Promise<HealthResponse> => {
    const res = await api.get('/actuator/health');
    return res.data;
};

export type DataResponse<T> = {
    data?: T;
    error?: ErrorResponse;
};

export type ErrorResponse = {
    error: string;
    message: string;
    timestamp: string;
};

export type AuthResponse = {
    user: {
        id: string;
        email: string;
        firstName: string;
        lastName: string;
    },
    accessToken: string;
};

export const login = async (
  credentials: {email: string, password: string}
): Promise<AuthResponse> => {
    try {
        const res: AxiosResponse<DataResponse<AuthResponse>> = await api.post('/auth/login', credentials);
        return res.data.data!;
    } catch (err: any) {
        throw handleError(err, 'Login failed');
    }
};

export const refresh = async (): Promise<AuthResponse> => {
    try {
        const res: AxiosResponse<DataResponse<AuthResponse>> = await api.post('/auth/refresh');
        return res.data.data!;
    } catch (err: any) {
        throw handleError(err, 'Refresh failed');
    }
}

export const logout = async (): Promise<void> => {
    try {
        await api.post('/auth/logout');
    } catch (err: any) {
        throw handleError(err, 'Logout failed');
    }
}

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

export const addBook = async (hardcoverId: string): Promise<void> => {
    try {
        await api.post('/books', {
            hardcoverId
        });
    } catch (err: any) {
        throw handleError(err, 'Failed to add book');
    }
}

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
    contentType: string;
    editionType: string;
    createdAt: string;
    updatedAt: string;
};

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

export type AuthorLink = {
    url: string;
    title: string;
}

export type Image = {
    id: string;
    url: string;
    color: string;
    width: number;
    height: number;
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

const handleError = (err: any, defaultMessage: string): ApiError => {
    if (err instanceof ApiError) return err;
    const { status, statusText, data } = err.response;
    const url: string = err.config?.url ?? '';
    console.log(`Received a ${status} - ${statusText} - `, JSON.stringify(data));
    return new ApiError(data?.error?.message || defaultMessage, status, url);
};
