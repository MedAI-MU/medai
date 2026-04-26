function ServicesCard({ title, text, icon }) {
  return (
    <div className="rounded-xl bg-white p-8 shadow-[0_4px_6px_rgba(0,0,0,0.1)]">
      <div className="mb-4 flex h-16 w-16 items-center justify-center rounded-full bg-[#E3F2FD]">
        {icon}
      </div>
      <h3 className="text-primary-dark mb-3 text-xl font-semibold">{title}</h3>
      <p className="text-primary-gray">{text}</p>
    </div>
  );
}

export default ServicesCard;
