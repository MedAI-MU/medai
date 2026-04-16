function ActionButtons({ position = "top-4 right-4", children }) {
  return <div className={`absolute ${position} flex gap-1`}>{children}</div>;
}

export default ActionButtons;
