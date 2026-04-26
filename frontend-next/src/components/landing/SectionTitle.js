function SectionTitle({ title, subTitle }) {
  return (
    <div className="mb-12 text-center">
      <h2 className="mb-4 text-3xl font-bold text-text-base md:text-4xl">
        {title}
      </h2>
      <p className="text-lg text-text-muted">{subTitle}</p>
    </div>
  );
}

export default SectionTitle;
