import { cookies } from "next/headers";

export async function getUserFromToken() {
  try {
    const cookieStore = await cookies();
    const token =
      cookieStore.get("Refresh")?.value ||
      cookieStore.get("Authentication")?.value;

    const payload = token?.split(".")[1] || null;
    return payload ? JSON.parse(atob(payload)) : null;
  } catch (err) {
    console.error(err?.message);
    return null;
  }
}
