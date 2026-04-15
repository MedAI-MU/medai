function Card({ className = "", children }) {
  return (
    <div
      className={`group border-border bg-surface hover:border-primary relative rounded-xl border p-5 shadow-sm transition-all ${className}`}
    >
      {children}
    </div>
  );
}

export default Card;
