export type MemberProfileResponse = {
  memberId: number;
  email: string;
  nickname: string;
}

export type UpdateMemberRequest = {
  nickname: string;
}

export type ChangePasswordRequest = {
  currentPassword: string;
  newPassword: string;
};
