const SIZES = {
  xl: {
    title: "text-3xl font-extrabold",
    subtitle: "text-lg",
  },
  lg: {
    title: "text-2xl font-bold",
    subtitle: "text-sm",
  },
  sm: {
    title: "text-lg font-bold",
    subtitle: "text-xsm",
  },
};

function Heading({
  title = "",
  subtitle = "",
  size = "xl",
  className = "",
  Tag = "h1",
}) {
  return (
    <div className={`flex flex-col gap-1 ${className}`}>
      <Tag className={`text-text-base tracking-tight ${SIZES[size].title}`}>
        {title}
      </Tag>

      {subtitle && (
        <p className={`text-text-muted ${SIZES[size].subtitle}`}>{subtitle}</p>
      )}
    </div>
  );
}

export default Heading;
