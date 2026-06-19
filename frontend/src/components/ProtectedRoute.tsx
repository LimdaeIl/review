import * as React from 'react';
import {Navigate} from 'react-router';
import {reissue} from '../features/auth/api/authApi';
import {useAuthStore} from '../features/auth/model/authStore';

type ProtectedRouteProps = {
  children: React.ReactNode;
};

export default function ProtectedRoute({children}: ProtectedRouteProps) {
  const isInitialized = useAuthStore((state) => state.isInitialized);
  const accessToken = useAuthStore((state) => state.accessToken);
  const setAccessToken = useAuthStore((state) => state.setAccessToken);
  const clearAuth = useAuthStore((state) => state.clearAuth);
  const isLoggingOut = useAuthStore((state) => state.isLoggingOut);

  React.useEffect(() => {
    if (isInitialized || isLoggingOut || accessToken) {
      return;
    }

    let isMounted = true;

    const restoreSession = async () => {
      try {
        const result = await reissue();

        if (!isMounted) {
          return;
        }

        setAccessToken(result.accessToken);
      } catch {
        if (!isMounted) {
          return;
        }

        clearAuth();
      }
    };

    void restoreSession();

    return () => {
      isMounted = false;
    };
  }, [accessToken, clearAuth, isInitialized, isLoggingOut, setAccessToken]);

  if (!isInitialized) {
    return <div>로그인 상태 확인 중...</div>;
  }

  if (isLoggingOut || !accessToken) {
    return <Navigate to="/login" replace/>;
  }

  return <>{children}</>;
}
