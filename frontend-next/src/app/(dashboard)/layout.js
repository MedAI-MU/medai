import Container from "@/components/ui/Container";
import DashboardHeader from "@/components/ui/DashboardHeader";
import Sidebar from "@/components/sidebar/Sidebar";
import SidebarProvider from "@/contexts/SidebarContext";
import { getUserFromToken } from "@/lib/session";
import { AuthProvider } from "@/contexts/AuthContext";

export default async function DashboardLayout({ children }) {
  const user = await getUserFromToken();

  return (
    <AuthProvider CurrentUser={user}>
      <SidebarProvider>
        <div className="grid h-dvh grid-cols-1 grid-rows-[auto_1fr] lg:grid-cols-[var(--sidebar-width)_1fr]">
          <Sidebar role={user?.role} />
          <DashboardHeader />
          <main className="bg-surface-bg overflow-auto transition-colors duration-300">
            <Container className="py-8">{children}</Container>
          </main>
        </div>
      </SidebarProvider>
    </AuthProvider>
  );
}
