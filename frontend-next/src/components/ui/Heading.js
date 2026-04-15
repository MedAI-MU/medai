/**
 * Can Accept action button too
 */

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
    subtitle: "text-sm",
  },
};

function Heading({
  title = "",
  subtitle = "",
  size = "xl",
  className = "",
  Tag = "h1",
  children,
}) {
  const Header = (
    <div className={`flex flex-col gap-1 ${className}`}>
      <Tag
        className={`text-text-base tracking-tight capitalize ${SIZES[size].title}`}
      >
        {title}
      </Tag>

      {subtitle && (
        <p className={`text-text-muted ${SIZES[size].subtitle}`}>{subtitle}</p>
      )}
    </div>
  );

  if (!children) return Header;

  return (
    <div className="flex items-center justify-between">
      {Header}
      {children}
    </div>
  );
}

export default Heading;
