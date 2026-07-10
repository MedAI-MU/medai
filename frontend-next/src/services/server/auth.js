import "server-only";

import { apiServer } from "@/lib/api/apiFetchServer";

export function getUser() {
  return apiServer.get("api/users/me");
}
