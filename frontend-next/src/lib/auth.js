import { cookies } from "next/headers";

export async function getUserFromToken() {
  const cookieStore = await cookies();
  const payload = cookieStore.get("Authentication")?.value.split(".")[1] || null;
  return payload ? JSON.parse(atob(payload)): null;
}
