function Section({ id, bgColor = "bg-surface", children }) {
  return (
    <section
      id={id}
      className={`py-20 transition-colors duration-300 md:py-32 ${bgColor}`}
    >
      {children}
    </section>
  );
}

export default Section;
