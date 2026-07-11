import UserSectionTabs from "@/components/manager/UserSectionTabs";

export default function UsersLayout({ children }) {
  return (
    <div className="space-y-6">
      <UserSectionTabs />
      {children}
    </div>
  );
}
