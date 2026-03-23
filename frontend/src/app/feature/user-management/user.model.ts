export type UserRole = 'SUPER_ADMIN' | 'ADMIN' | 'VIEWER';

export interface AppUser {
  id: number | null;
  username: string;
  email: string | null;
  fullName: string;
  userRole: UserRole;
  employeeId: number | null;
  isActive?: boolean;
  createdAt?: string;
  updatedAt?: string;
}

export interface AppUserDto {
  username: string;
  password: string;
  email: string | null;
  fullName: string;
  userRole: UserRole;
  employeeId: number | null;
}
