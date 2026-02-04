import { UserRoles } from 'src/users/types/role.types';

export interface TokenPayload {
  sub: number;
  email: string;
  role: UserRoles;
}
