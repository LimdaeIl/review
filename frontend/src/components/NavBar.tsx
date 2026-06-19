import { Link, useNavigate } from 'react-router';
import { logout } from '../features/auth/api/authApi';
import { useAuthStore } from '../features/auth/model/authStore';

export default function NavBar() {
  const navigate = useNavigate();

  const clearAuth = useAuthStore((state) => state.clearAuth);
  const isLoggedIn = useAuthStore((state) => state.isLoggedIn);
  const startLogout = useAuthStore((state) => state.startLogout);

  const handleLogout = async () => {
    try {
      startLogout();

      await logout();

      clearAuth();

      navigate('/login', { replace: true });
    } catch (error) {
      console.error(error);
      clearAuth();
      navigate('/login', { replace: true });
    }
  };

  return (
      <nav>
        <Link to="/login">로그인</Link>
        {' | '}

        <Link to="/signup">회원가입</Link>
        {' | '}

        <Link to="/me">마이페이지</Link>

        {isLoggedIn && (
            <>
              {' | '}
              <button onClick={handleLogout}>
                로그아웃
              </button>
            </>
        )}
      </nav>
  );
}