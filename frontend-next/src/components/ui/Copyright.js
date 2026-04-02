function Copyright({ className = "" }) {
  return (
    <p className={`text-sm ${className}`}>
      &copy; {new Date().getFullYear()} MedAI Platforms Inc. All rights
      reserved.
    </p>
  );
}

export default Copyright;
