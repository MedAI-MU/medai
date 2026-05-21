import { Trash2 } from "lucide-react";
import ButtonIcon from "./ButtonIcon";

function DeleteButtonIcon({
  Icon = Trash2,
  size = 16,
  className = "",
  ...props
}) {
  return (
    <ButtonIcon
      type="button"
      className={`hover:bg-danger-muted text-danger shrink-0 ${className}`}
      {...props}
    >
      <Icon size={size} />
    </ButtonIcon>
  );
}

export default DeleteButtonIcon;
