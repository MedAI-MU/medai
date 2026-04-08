const Title = {
  hero: "text-5xl font-extrabold leading-tight mb-6",
  form: "text-3xl font-bold text-text-base mb-2",
};

const SubTitle = {
  hero: "text-xl text-white/90 max-w-md leading-relaxed",
  form: "text-text-muted",
};

function SectionHeader({
  title,
  subTitle,
  as: Tag = "h1",
  variant,
  className = "",
}) {
  return (
    <div className={className}>
      <Tag className={Title[variant]}>{title}</Tag>
      {subTitle && <p className={SubTitle[variant]}>{subTitle}</p>}
    </div>
  );
}

export default SectionHeader;
