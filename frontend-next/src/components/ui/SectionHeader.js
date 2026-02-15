const Title = {
  hero: "text-5xl font-extrabold leading-tight mb-6",
  form: "text-3xl font-bold text-gray-900 mb-2",
};

const SubTitle = {
  hero: "text-xl text-white/90 max-w-md leading-relaxed",
  form: "text-gray-500",
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
