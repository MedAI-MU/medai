import "server-only";

import { apiServer } from "@/lib/api/apiFetchServer";
import { getUserFromToken } from "@/lib/session";
import { redirect } from "next/navigation";

export async function getScheduleTemplates(name, pageSize, pageNo) {
  const user = await getUserFromToken();
  if (!user) redirect("/auth/login");

  const params = new URLSearchParams();

  if (name) params.set("name", name);
  if (pageSize) params.set("pageSize", pageSize);
  if (pageNo) params.set("pageNo", pageNo);

  const response = await apiServer.get(
    `api/doctors/${user?.sub}/schedule-templates${params.toString() ? `?${params.toString()}` : ""}`,
  );
  return response;
}
