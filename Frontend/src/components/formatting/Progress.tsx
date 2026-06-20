type ProgressProps = {
  percent?: number;
  className?: string;
  heightPx?: number;
};

const Progress = (
  {percent = 0, className = "", heightPx = 3}: ProgressProps
) => {
  if (percent === undefined || percent < 0 || percent > 100) return;

  return (
    <div className={className}>
      <div className="group-hover:h-1.5 bg-surfaceAlt
          rounded-sm overflow-hidden transition-[height] duration-120 ease"
        style={{ height: `${heightPx}px` }}
      >
        <div className="h-full bg-accent transition-[width] duration-200 ease-out
          pointer-events-none" style={{width: `${percent}%`}} />
      </div>
    </div>
  );
};

export default Progress;