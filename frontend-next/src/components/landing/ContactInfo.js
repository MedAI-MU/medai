function ContactInfo({ icon, label, value }) {
  return (
    <div className="mb-6 flex items-start gap-4">
      <div className="rounded-lg bg-primary/10 p-3">{icon}</div>
      <div>
        <h4 className="mb-1 font-semibold text-text-base">{label}</h4>
        <p className="text-text-muted">{value}</p>
      </div>
    </div>
  );
}

export default ContactInfo;
