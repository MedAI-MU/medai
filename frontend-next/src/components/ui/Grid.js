const GRID_SHAPE = {
  two: "md:grid-cols-2",
  four: "md:grid-cols-2 lg:grid-cols-4",
};

function Grid({ cols = "four", gap = "gap-6", className, children }) {
  return (
    <div className={`grid grid-cols-1 ${gap} ${GRID_SHAPE[cols]} ${className}`}>
      {children}
    </div>
  );
}

export default Grid;
