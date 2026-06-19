export type LoginRequest = {
  email: string;
  password: string;
}

export type OAuthProvider = 'google' | 'kakao' | 'github';

export type LoginResponse = {
  accessToken: string;
}

export type SignupRequest = {
  email: string;
  password: string;
  phone: string;
  nickname: string;
}

export type SignupResponse = {
  memberId: number;
}

export type ReissueResponse = {
  accessToken: string;
};
