function Logo() {
  return (
    <div className="flex items-center gap-2">
      <svg width="32" height="32" viewBox="0 0 32 32" fill="none">
        <rect x="13" y="6" width="6" height="20" fill="#4A90E2" rx="1" />
        <rect x="6" y="13" width="20" height="6" fill="#4A90E2" rx="1" />
      </svg>
      <span className="text-primary-dark text-xl font-semibold">MedAI</span>
    </div>
  );
}

export default Logo;
