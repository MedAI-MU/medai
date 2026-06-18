import { Plus } from "lucide-react";
import Button from "@/components/ui/Button";

function AddButton({ children, ...props }) {
  return (
    <Button startIcon={<Plus />} {...props}>
      {children && <span className="hidden sm:inline">{children}</span>}
    </Button>
  );
}

export default AddButton;
