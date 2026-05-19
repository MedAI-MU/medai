/**
 * Can Accept action button too.
 */

const SIZES = {
  xl: {
    title: "text-2xl sm:text-3xl font-extrabold",
    subtitle: "text-lg",
  },
  lg: {
    title: "text-xl sm:text-2xl font-bold",
    subtitle: "text-sm",
  },
  sm: {
    title: "text-base sm:text-lg font-bold",
    subtitle: "text-sm",
  },
};

function Heading({
  title = "",
  subtitle = "",
  size = "xl",
  className = "",
  Tag = "h1",
  capitalizeSubtitle = false,
  hideSubtitleOnMobile = false,
  rowOnMobile = false,
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
        <p
          className={`text-text-muted ${SIZES[size].subtitle} ${
            capitalizeSubtitle ? "capitalize" : ""
          } ${hideSubtitleOnMobile ? "hidden md:block" : ""}`}
        >
          {subtitle}
        </p>
      )}
    </div>
  );

  if (!children) return Header;

  return (
    <div
      className={`flex justify-between gap-4 sm:flex-row sm:items-center ${
        rowOnMobile ? "flex-row items-center" : "flex-col"
      }`}
    >
      {Header}
      {children}
    </div>
  );
}

export default Heading;
