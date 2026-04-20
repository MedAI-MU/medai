import Container from "@/components/ui/Container";
import DashboardHeader from "@/components/ui/DashboardHeader";
import Sidebar from "@/components/ui/Sidebar";
import SidebarProvider from "@/contexts/SidebarContext";

export default function Layout({ children }) {
  return (
    <SidebarProvider>
      <div className="grid h-dvh grid-cols-1 grid-rows-[auto_1fr] lg:grid-cols-[var(--sidebar-width)_1fr]">
        <Sidebar />
        <DashboardHeader />
        <main className="bg-surface-bg overflow-auto transition-colors duration-300">
          <Container className="py-8">{children}</Container>
        </main>
      </div>
    </SidebarProvider>
  );
}
