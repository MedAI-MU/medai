"use client";

import UserRoleManager from "./UserRoleManager";
import { promoteToDoctor } from "@/services/client/manager";

export default function DoctorsManager(props) {
  return (
    <UserRoleManager {...props} role="doctor" promoteFn={promoteToDoctor} />
  );
}
