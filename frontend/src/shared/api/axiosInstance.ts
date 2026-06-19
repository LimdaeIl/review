import axios, { type InternalAxiosRequestConfig } from 'axios'
import type { CommonResponse } from './commonResponse';
import {useAuthStore} from '../../features/auth/model/authStore'

declare module 'axios' {
  export interface AxiosRequestConfig {
    skipAuthHeader?: boolean;
    skipAuthRefresh?: boolean;
  }

  export interface InternalAxiosRequestConfig {
    skipAuthHeader?: boolean;
    skipAuthRefresh?: boolean;
  }
}

type AuthRequestConfig = InternalAxiosRequestConfig & {
  _retry?: boolean;
};

type ReissueResponse = {
  accessToken: string;
};

export const api = axios.create({
  baseURL: 'http://localhost:8080/api/v1',
  withCredentials: true
});

let refreshPromise: Promise<string | null> | null = null;

api.interceptors.request.use((config: AuthRequestConfig) => {
  const accessToken = useAuthStore.getState().accessToken;

  if (accessToken && !config.skipAuthHeader) {
    config.headers.Authorization = `Bearer ${accessToken}`
  }

  return config;
})

const refreshAccessToken = async (): Promise<string | null> => {
  if (!refreshPromise) {
    refreshPromise = (async () => {
      try {
        const response = await api.post<CommonResponse<ReissueResponse>>(
            '/auth/reissue',
            undefined,
            {
              skipAuthRefresh: true,
              skipAuthHeader: true,
            }
        );
        const accessToken = response.data.data.accessToken;

        useAuthStore.getState().setAccessToken(accessToken);

        return accessToken;
      } catch {
        useAuthStore.getState().clearAuth();
        return null;
      } finally {
        refreshPromise = null;
      }
    })();
  }

  return refreshPromise;
};

api.interceptors.response.use(
    (response) => response,
    async (error) => {
      const originalRequest = error.config as AuthRequestConfig | undefined;

      if (!originalRequest || originalRequest.skipAuthRefresh) {
        return Promise.reject(error);
      }

      if (error.response?.status !== 401 || originalRequest._retry) {
        return Promise.reject(error);
      }

      if (useAuthStore.getState().isLoggingOut) {
        return Promise.reject(error);
      }

      originalRequest._retry = true;

      const newAccessToken = await refreshAccessToken();

      if (!newAccessToken) {
        return Promise.reject(error);
      }

      originalRequest.headers.Authorization = `Bearer ${newAccessToken}`;

      return api(originalRequest);
    }
);
