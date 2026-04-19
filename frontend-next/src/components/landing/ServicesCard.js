function ServicesCard({ title, text, icon }) {
  return (
    <div className="rounded-xl bg-surface p-8 shadow-md transition-colors duration-300">
      <div className="mb-4 flex h-16 w-16 items-center justify-center rounded-full bg-primary/10">
        {icon}
      </div>
      <h3 className="mb-3 text-xl font-semibold text-text-base">{title}</h3>
      <p className="text-text-muted">{text}</p>
    </div>
  );
}

export default ServicesCard;
