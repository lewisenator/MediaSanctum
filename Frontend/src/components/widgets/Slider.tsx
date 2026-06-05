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
        className="w-full mt-2 cursor-pointer appearance-none accent-accent
          [&::-webkit-slider-runnable-track]:h-1.5 [&::-webkit-slider-runnable-track]:rounded-full
          [&::-webkit-slider-runnable-track]:bg-surfaceAlt
          [&::-webkit-slider-thumb]:appearance-none [&::-webkit-slider-thumb]:mt-[-3px] [&::-webkit-slider-thumb]:h-3.5
          [&::-webkit-slider-thumb]:w-3.5 [&::-webkit-slider-thumb]:rounded-full [&::-webkit-slider-thumb]:bg-accent
          [&::-webkit-slider-thumb]:border-0
          [&::-moz-range-track]:h-1.5 [&::-moz-range-track]:rounded-full [&::-moz-range-track]:bg-surfaceAlt
          [&::-moz-range-thumb]:h-3.5 [&::-moz-range-thumb]:w-3.5 [&::-moz-range-thumb]:rounded-full
          [&::-moz-range-thumb]:bg-accent [&::-moz-range-thumb]:border-0"
      />
    </div>
  );
};

export default Slider;