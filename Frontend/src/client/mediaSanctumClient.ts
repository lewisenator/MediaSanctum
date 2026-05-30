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
        const authResponse = res?.data?.data;
        if (!authResponse) {
            throw new Error("Did not receive auth response");
        }
        return authResponse;
    } catch (err: any) {
        console.log(err.response);
        const message = err?.response?.data?.message || 'Failed to login';
        console.error(message);
        throw new Error(message);
    }
};

export const refresh = async (): Promise<AuthResponse> => {
    try {
        const res: AxiosResponse<DataResponse<AuthResponse>> = await api.post('/auth/refresh');
        const authResponse = res?.data?.data;
        if (!authResponse) {
            throw new Error("Did not receive auth response");
        }
        return authResponse;
    } catch (err: any) {
        const message = err?.response?.data?.error?.message || 'Failed to refresh access token';
        console.error(message);
        throw new Error(message);
    }
}

export const logout = async (): Promise<void> => {
    try {
        await api.post('/auth/logout');
    } catch (err: any) {
        const message = err?.response?.data?.message || 'Failed to logout';
        console.error(message);
        throw new Error(message);
    }
}
