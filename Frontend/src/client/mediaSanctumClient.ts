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

type DataResponse<T> = {
    data?: T;
    error?: ErrorResponse;
};

type ErrorResponse = {
    error: string;
    message: string;
    timestamp: string;
};

type AuthResponse = {
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
        const { status, statusText, data } = err.response;
        console.log(`Received a ${status} - ${statusText} - `, JSON.stringify(data));
        throw new Error(data?.error?.message || 'Login failed');
    }
};

export const refresh = async (): Promise<AuthResponse> => {
    try {
        const res: AxiosResponse<DataResponse<AuthResponse>> = await api.post('/auth/refresh');
        return res.data.data!;
    } catch (err: any) {
        const { status, statusText, data } = err.response;
        console.log(`Received a ${status} - ${statusText} - `, JSON.stringify(data));
        throw new Error(data?.error?.message || 'Refresh failed');
    }
}

export const logout = async (): Promise<void> => {
    try {
        await api.post('/auth/logout');
    } catch (err: any) {
        const { status, statusText, data } = err.response;
        console.log(`Received a ${status} - ${statusText} - `, JSON.stringify(data));
        throw new Error(data?.error?.message || 'Logout failed');
    }
}
