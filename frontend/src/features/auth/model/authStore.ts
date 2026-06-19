import { create } from 'zustand';

type AuthState = {
  accessToken: string | null;
  isLoggedIn: boolean;
  isLoggingOut: boolean;
  isInitialized: boolean;
  setAccessToken: (accessToken: string) => void;
  clearAuth: () => void;
  startLogout: () => void;
};

export const useAuthStore = create<AuthState>((set) => ({
  accessToken: null,
  isLoggedIn: false,
  isLoggingOut: false,
  isInitialized: false,

  setAccessToken: (accessToken) =>
      set({
        accessToken,
        isLoggedIn: true,
        isLoggingOut: false,
        isInitialized: true,
      }),

  clearAuth: () =>
      set({
        accessToken: null,
        isLoggedIn: false,
        isLoggingOut: false,
        isInitialized: true,
      }),

  startLogout: () =>
      set({
        isLoggingOut: true,
        isInitialized: true,
      }),
}));
