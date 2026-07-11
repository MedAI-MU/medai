"use client";

import UserRoleManager from "./UserRoleManager";
import { promoteToSecretary } from "@/services/client/manager";

export default function SecretariesManager(props) {
  return (
    <UserRoleManager
      {...props}
      role="secretary"
      promoteFn={promoteToSecretary}
    />
  );
}
