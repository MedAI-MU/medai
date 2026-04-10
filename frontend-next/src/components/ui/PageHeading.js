function PageHeading({ title = "", subtitle = "" }) {
  return (
    <div className="flex flex-col gap-1 capitalize">
      <h2 className="text-text-base text-3xl font-extrabold tracking-tight">
        {title}
      </h2>
      {subtitle && <p className="text-text-muted text-lg">{subtitle}</p>}
    </div>
  );
}

export default PageHeading;
