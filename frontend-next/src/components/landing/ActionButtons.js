import Button from "../ui/Button";

function ActionButtons() {
  return (
    <div className="hidden items-center gap-3 md:flex">
      <Button variation="secondary">Login</Button>
      <Button>Sign Up</Button>
    </div>
  );
}

export default ActionButtons;
