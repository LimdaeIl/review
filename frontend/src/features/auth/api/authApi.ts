import {api} from '../../../shared/api/axiosInstance'
import type {CommonResponse} from '../../../shared/api/commonResponse'
import type {
  LoginRequest,
  LoginResponse,
  OAuthProvider,
  ReissueResponse,
  SignupRequest,
  SignupResponse
} from '../types/authTypes'

export const login = async (
    request: LoginRequest
): Promise<LoginResponse> => {
  const response =
      await api.post<CommonResponse<LoginResponse>>('/auth/login', request, {
        skipAuthRefresh: true,
      });

  return response.data.data;
};

export const signup = async (
    request: SignupRequest
): Promise<SignupResponse> => {
  const response =
      await api.post<CommonResponse<SignupResponse>>('/auth/signup', request, {
        skipAuthRefresh: true,
      });

  return response.data.data;
};

export const reissue = async (): Promise<ReissueResponse> => {
  const response = await api.post<CommonResponse<ReissueResponse>>(
      '/auth/reissue',
      undefined,
      {
        skipAuthRefresh: true,
        skipAuthHeader: true,
      }
  );

  return response.data.data;
};

export const logout = async (): Promise<void> => {
  await api.post<CommonResponse<null>>('/auth/logout', undefined, {
    skipAuthRefresh: true,
  });
}

export const getOAuthLoginUrl = (provider: OAuthProvider): string =>
    `http://localhost:8080/api/v1/oauth/${provider}/login`;
