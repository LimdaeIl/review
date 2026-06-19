import { api } from '../../../shared/api/axiosInstance';
import type { CommonResponse } from '../../../shared/api/commonResponse';
import type {
  MemberProfileResponse,
  UpdateMemberRequest,
  ChangePasswordRequest,
} from '../types/memberTypes';

export const getMyProfile = async (): Promise<MemberProfileResponse> => {
  const response = await api.get<CommonResponse<MemberProfileResponse>>(
      '/members/me'
  );

  return response.data.data;
};

export const updateMyProfile = async (
    request: UpdateMemberRequest
): Promise<void> => {
  await api.patch<CommonResponse<null>>('/members/me', request);
};

export const changePassword = async (
    request: ChangePasswordRequest
): Promise<void> => {
  await api.patch<CommonResponse<null>>('/members/me/password', request);
};
