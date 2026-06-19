import { useEffect, useState } from 'react';
import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';

import {
  getMyProfile,
  updateMyProfile,
  changePassword
} from '../features/member/api/memberApi';

export default function MyPage() {
  const [nickname, setNickname] = useState('');
  const [currentPassword, setCurrentPassword] = useState('');
  const [newPassword, setNewPassword] = useState('');

  const queryClient = useQueryClient();

  const { data: profile, isLoading } = useQuery({
    queryKey: ['myProfile'],
    queryFn: getMyProfile,
  });

  useEffect(() => {
    if (profile) {
      setNickname(profile.nickname);
    }
  }, [profile]);

  const changePasswordMutation = useMutation({
    mutationFn: changePassword,
    onSuccess: () => {
      alert('비밀번호 변경 성공');
      setCurrentPassword('');
      setNewPassword('');
    },
    onError: (error) => {
      console.error(error);
      alert('비밀번호 변경 실패');
    },
  });

  const handleChangePassword = () => {
    changePasswordMutation.mutate({
      currentPassword,
      newPassword,
    });
  };

  const updateProfileMutation = useMutation({
    mutationFn: updateMyProfile,
    onSuccess: async () => {
      alert('회원정보 수정 성공');

      await queryClient.invalidateQueries({
        queryKey: ['myProfile'],
      });
    },
    onError: (error) => {
      console.error(error);
      alert('회원정보 수정 실패');
    },
  });

  const handleUpdateProfile = () => {
    updateProfileMutation.mutate({
      nickname,
    });
  };

  if (isLoading) {
    return <div>내 정보 불러오는 중...</div>;
  }

  if (!profile) {
    return <div>회원 정보를 불러오지 못했습니다.</div>;
  }

  return (
      <div>
        <h1>마이페이지</h1>

        <p>회원 ID: {profile.memberId}</p>
        <p>이메일: {profile.email}</p>

        <div>
          <label>닉네임</label>
          <input
              value={nickname}
              onChange={(e) => setNickname(e.target.value)}
          />

          <button
              onClick={handleUpdateProfile}
              disabled={updateProfileMutation.isPending}
          >
            {updateProfileMutation.isPending ? '수정 중...' : '수정'}
          </button>
        </div>
        <hr />

        <h2>비밀번호 변경</h2>

        <input
            placeholder="현재 비밀번호"
            type="password"
            value={currentPassword}
            onChange={(e) => setCurrentPassword(e.target.value)}
        />

        <input
            placeholder="새 비밀번호"
            type="password"
            value={newPassword}
            onChange={(e) => setNewPassword(e.target.value)}
        />

        <button
            onClick={handleChangePassword}
            disabled={changePasswordMutation.isPending}
        >
          {changePasswordMutation.isPending ? '변경 중...' : '비밀번호 변경'}
        </button>
      </div>
  );
}
