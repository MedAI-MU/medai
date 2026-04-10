import "server-only";

import { apiServer } from "@/lib/api/apiFetchServer";
import { getUserFromToken } from "@/lib/session";
import { redirect } from "next/navigation";

export async function getPatient() {
  const user = await getUserFromToken();
  if (!user) redirect("/auth/login");

  const response = await apiServer.get(`api/patients/${user?.sub}`);
  return response;
}
