function InfoRow({ icon: Icon, value }) {
  return (
    <div className="text-text-muted flex items-center gap-3 text-sm">
      <Icon size={18} className="text-text-subtle" />
      {value}
    </div>
  );
}

export default InfoRow;
