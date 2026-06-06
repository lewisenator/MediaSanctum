import api from "#/config/axios.ts";
import type { AxiosResponse } from 'axios';
import { handleError, type DataResponse } from '#/client/shared.ts';

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