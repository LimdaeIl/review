import { BrowserRouter, Route, Routes } from 'react-router';
import NavBar from './components/NavBar';
import ProtectedRoute from './components/ProtectedRoute';
import LoginPage from './pages/LoginPage';
import OAuthCallbackPage from './pages/OAuthCallbackPage';
import SignupPage from './pages/SignupPage';
import MyPage from './pages/MyPage';

function App() {
  return (
      <BrowserRouter>
        <NavBar />

        <Routes>
          <Route path="/" element={<LoginPage />} />
          <Route path="/login" element={<LoginPage />} />
          <Route path="/oauth/callback" element={<OAuthCallbackPage />} />
          <Route path="/signup" element={<SignupPage />} />

          <Route
              path="/me"
              element={
                <ProtectedRoute>
                  <MyPage />
                </ProtectedRoute>
              }
          />
        </Routes>
      </BrowserRouter>
  );
}

export default App;
