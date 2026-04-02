import Button from "../ui/Button";

function ActionButtons() {
  return (
    <div className="hidden items-center gap-3 md:flex">
      <Button variation="secondary" href="/auth/login">
        Login
      </Button>
      <Button href="/auth/signup">Sign Up</Button>
    </div>
  );
}

export default ActionButtons;
