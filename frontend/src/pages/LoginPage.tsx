import {useState} from 'react';
import {getOAuthLoginUrl, login} from '../features/auth/api/authApi'
import {useAuthStore} from "../features/auth/model/authStore";
import {useNavigate} from "react-router";
import type { OAuthProvider } from '../features/auth/types/authTypes';

const OAUTH_PROVIDERS: Array<{ label: string; provider: OAuthProvider }> = [
  { label: 'Google로 로그인', provider: 'google' },
  { label: 'Kakao로 로그인', provider: 'kakao' },
  { label: 'GitHub로 로그인', provider: 'github' },
];

export default function LoginPage() {
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const setAccessToken =
      useAuthStore((state) => state.setAccessToken);

  const navigate = useNavigate();

  const handleOAuthLogin = (provider: OAuthProvider) => {
    window.location.href = getOAuthLoginUrl(provider);
  };

  const handleLogin = async () => {
    try {
      const result = await login({email, password,});

      setAccessToken(result.accessToken);

      navigate('/me');
      alert('로그인 성공');
    } catch (error) {
      console.error(error);
      alert('로그인 실패');
    }
  };

  return (
      <div>
        <h1>로그인</h1>
        <input placeholder="이메일"
               value={email}
               onChange={(e) => setEmail(e.target.value)}
        />
        <input placeholder="비밀번호"
               type="password"
               value={password}
               onChange={(e) => setPassword(e.target.value)}
        />
        <button onClick={handleLogin}>로그인</button>
        <hr />
        {OAUTH_PROVIDERS.map(({ label, provider }) => (
            <button
                key={provider}
                onClick={() => handleOAuthLogin(provider)}
                type="button"
            >
              {label}
            </button>
        ))}
      </div>
  );
}
