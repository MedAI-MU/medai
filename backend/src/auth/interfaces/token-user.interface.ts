import { UserRoles } from 'src/users/types/role.types';

export interface TokenUser {
  id: number;
  email: string;
  role: UserRoles;
}
