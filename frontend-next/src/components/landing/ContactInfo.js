function ContactInfo({ icon, label, value }) {
  return (
    <div className="mb-6 flex items-start gap-4">
      <div className="rounded-lg bg-[#E3F2FD] p-3">{icon}</div>
      <div>
        <h4 className="text-primary-dark mb-1 font-semibold">{label}</h4>
        <p className="text-primary-gray">{value}</p>
      </div>
    </div>
  );
}

export default ContactInfo;
