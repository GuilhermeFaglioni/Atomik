export type SessionRecord = {
  accessToken: string;
  refreshToken: string;
  tokenType: string;
  expiresIn: number;
  expiresAt: number;
  userId: string;
  email: string | null;
};

export type AuthResponse = {
  accessToken: string;
  refreshToken: string;
  tokenType: string;
  expiresIn: number;
};

export type RegisterPayload = {
  name: string;
  email: string;
  password: string;
  preferredCurrency: string;
};

export type LoginPayload = {
  email: string;
  password: string;
};

export type CurrentUserResponse = {
  id: string;
  name: string;
  email: string;
  preferredCurrency: string;
};
