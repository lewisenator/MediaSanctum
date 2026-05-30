import axios, { type AxiosInstance, type InternalAxiosRequestConfig } from 'axios';
import { refresh } from "#/client/mediaSanctumClient.ts";

const api: AxiosInstance = axios.create({
    baseURL: '/api',
    withCredentials: true,
    headers: {
        'Content-Type': 'application/json',
    }
});

// Attach token on refresh
api.interceptors.request.use((config: InternalAxiosRequestConfig) => {
    const token = getStoredAccessToken();
    if (token) {
        config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
});

let accessToken: string | null = null;

export const setStoredAccessToken = (token: string | null) => {
    accessToken = token;
};

export const getStoredAccessToken = (): string | null => {
    return accessToken;
}

// Refresh token after auth error. The repsonse.use function takes (okFn, errorFn)
api.interceptors.response.use((res) => res, async (error) => {
    const originalRequest = error.config;
    if (error.response?.status === 401
      && !originalRequest._retry
      && !originalRequest.url.includes('/auth/refresh')
    ) {
        originalRequest._retry = true;
        try {
            const { accessToken } = await refresh();
            setStoredAccessToken(accessToken);
            originalRequest.headers.Authorization = `Bearer ${accessToken}`;
            console.log('Automatically retrying with new access token');
            return api(originalRequest);
        } catch (err) {
            console.error('Refresh token failed', err);
        }
        return Promise.reject(error);
    }
    return Promise.reject(error);
});

export default api;