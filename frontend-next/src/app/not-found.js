import Button from "@/components/ui/Button";

export default function NotFound() {
  return (
    <div className="flex min-h-[70vh] flex-col items-center justify-center px-6 text-center">
      <h1 className="text-4xl font-bold">Page Not Found</h1>

      <p className="text-muted-foreground mt-4 max-w-md">
        The page you&apos;re looking for doesn&apos;t exist or is no longer
        available.
      </p>

      <Button
        href="/"
        className="bg-primary text-primary-foreground mt-8 rounded-md px-4 py-2"
      >
        Go Home
      </Button>
    </div>
  );
}
