function SectionTitle({ title, subTitle }) {
  return (
    <div className="mb-12 text-center">
      <h2 className="text-primary-dark mb-4 text-3xl font-bold md:text-4xl">
        {title}
      </h2>
      <p className="text-primary-gray text-lg">{subTitle}</p>
    </div>
  );
}

export default SectionTitle;
