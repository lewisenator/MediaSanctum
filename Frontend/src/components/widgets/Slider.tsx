type SliderProps = {
  value: number;
  setValue: (value: number) => void;
  label: string;
  min?: number;
  max?: number;
  stepSize?: number;
  valueFormatter?: (value: number) => string;
};

const Slider = (
  {
    value,
    setValue,
    label,
    min = 0,
    max = 100,
    stepSize = 1,
    valueFormatter = (value) => `${value}`,
  }: SliderProps
) => {

  const pct = ((value - min) / (max - min)) * 100;

  const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const value: number = Number(e.target.value);
    if (isNaN(value)) {
      setValue(0);
      return;
    }
    setValue(value);
  };

  return (
    <div>
      <div
        className="flex flex-row justify-between items-center text-sm font-medium text-textDim"
      >
        <div>
          {label}
        </div>
        <div>
          {valueFormatter(value)}
        </div>
      </div>
      <input
        type="range"
        min={min}
        max={max}
        step={stepSize}
        value={value}
        onChange={handleChange}
        style={{ '--fill-pct': `${pct}%` } as React.CSSProperties}
        className="w-full mt-2 cursor-pointer appearance-none
          [&::-webkit-slider-runnable-track]:h-1.5 [&::-webkit-slider-runnable-track]:rounded-full
          [&::-webkit-slider-runnable-track]:[background:linear-gradient(to_right,var(--c-accent)_var(--fill-pct),var(--c-surfaceAlt)_var(--fill-pct))]
          [&::-webkit-slider-thumb]:appearance-none [&::-webkit-slider-thumb]:mt-[-9px]
          [&::-webkit-slider-thumb]:h-6 [&::-webkit-slider-thumb]:w-6
          [&::-webkit-slider-thumb]:rounded-full [&::-webkit-slider-thumb]:bg-accent
          [&::-webkit-slider-thumb]:box-border [&::-webkit-slider-thumb]:border-[5px]
          [&::-webkit-slider-thumb]:border-transparent [&::-webkit-slider-thumb]:bg-clip-content
          [&::-moz-range-track]:h-1.5 [&::-moz-range-track]:rounded-full [&::-moz-range-track]:bg-surfaceAlt
          [&::-moz-range-progress]:h-1.5 [&::-moz-range-progress]:rounded-full [&::-moz-range-progress]:bg-accent
          [&::-moz-range-thumb]:h-6 [&::-moz-range-thumb]:w-6 [&::-moz-range-thumb]:rounded-full
          [&::-moz-range-thumb]:bg-accent [&::-moz-range-thumb]:box-border [&::-moz-range-thumb]:border-[5px]
          [&::-moz-range-thumb]:border-transparent [&::-moz-range-thumb]:bg-clip-content"
      />
    </div>
  );
};

export default Slider;