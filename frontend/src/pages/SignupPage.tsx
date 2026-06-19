import {useState} from 'react';
import {signup} from '../features/auth/api/authApi'

export default function SignupPage() {
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [phone, setPhone] = useState('');
  const [nickname, setNickname] = useState('');

  const handleSignup = async () => {
    try {
      await signup({
        email,
        password,
        phone,
        nickname,
      });

      alert("회원가입 성공");
    } catch (error) {
      alert("회원가입 실패");
    }
  };

  return (
      <div>
        <h1>회원가입</h1>
        <input
            placeholder="이메일"
            value={email}
            onChange={(e) => setEmail(e.target.value)}
        />
        <input
            placeholder="비밀번호"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
        />
        <input
            placeholder="휴대전화번호"
            value={phone}
            onChange={(e) => setPhone(e.target.value)}
        />
        <input
            placeholder="닉네임"
            value={nickname}
            onChange={(e) => setNickname(e.target.value)}
        />
        <button onClick={handleSignup}>회원가입</button>
      </div>
  )
}