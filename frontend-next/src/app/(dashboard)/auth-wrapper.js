import { AuthProvider } from "@/contexts/AuthContext";
import { getUserFromToken } from "@/lib/session";

export default async function AuthWrapper({ children }) {
  const user = await getUserFromToken();
  return <AuthProvider CurrentUser={user}>{children}</AuthProvider>;
}
