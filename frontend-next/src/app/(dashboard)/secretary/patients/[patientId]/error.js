"use client";

import ErrorState from "@/components/ui/ErrorState";

function Error({ error, reset }) {
  return (
    <ErrorState
      title="Failed to load patient details"
      description={
        error?.statusCode === 404
          ? "The patient you're looking for doesn't exist."
          : "Something went wrong while loading this patient's information. Please try again."
      }
      onRetry={reset}
    />
  );
}

export default Error;
