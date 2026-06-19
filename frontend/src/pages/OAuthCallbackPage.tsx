import { useEffect, useState } from 'react';
import { useNavigate } from 'react-router';
import { useAuthStore } from '../features/auth/model/authStore';

export default function OAuthCallbackPage() {
  const navigate = useNavigate();
  const [errorMessage, setErrorMessage] = useState<string | null>(null);
  const setAccessToken = useAuthStore((state) => state.setAccessToken);
  const clearAuth = useAuthStore((state) => state.clearAuth);

  useEffect(() => {
    const params = new URLSearchParams(window.location.search);
    const accessToken = params.get('accessToken');

    if (!accessToken) {
      clearAuth();
      setErrorMessage('OAuth 로그인에 실패했습니다.');
      return;
    }

    setAccessToken(accessToken);
    navigate('/me', { replace: true });
  }, [clearAuth, navigate, setAccessToken]);

  if (errorMessage) {
    return <div>{errorMessage}</div>;
  }

  return <div>로그인 처리 중...</div>;
}
