function Section({ id, bgColor = "bg-white", children }) {
  return (
    <section id={id} className={`px-6 py-20 md:py-32 ${bgColor}`}>
      {children}
    </section>
  );
}

export default Section;
