"use client";

function Overlay({ onClick, className }) {
  return (
    <div
      onClick={onClick}
      className={`fixed inset-0 z-40 bg-black/20 backdrop-blur-sm ${className}`}
    />
  );
}

export default Overlay;
