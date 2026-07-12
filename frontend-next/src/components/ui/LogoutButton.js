import { logoutAction } from "@/lib/actions";
import { LogOut } from "lucide-react";
import Button from "./Button";
import { cn } from "@/lib/utils";

function LogoutButton({ className = "" }) {
  return (
    <form action={logoutAction}>
      {/* <button className="text-danger hover:bg-danger-muted flex w-full cursor-pointer items-center gap-3 rounded-lg px-3 py-2 transition-colors"></button> */}
      <Button
        variation="dangerGhost"
        className={cn("w-full justify-start", className)}
      >
        <LogOut size={18} />
        <span className="text-sm font-medium">Log out</span>
      </Button>
    </form>
  );
}

export default LogoutButton;
