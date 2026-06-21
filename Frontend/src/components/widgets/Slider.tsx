import { useLayoutEffect, useRef, useState } from 'react';

type SliderProps = {
  value: number;
  setValue: (value: number) => void;
  label?: string;
  min?: number;
  max?: number;
  stepSize?: number;
  valueFormatter?: (value: number) => string;
  className?: string;
};

const THUMB_W = 24;

const Slider = (
  {
    value,
    setValue,
    label,
    min = 0,
    max = 100,
    stepSize = 1,
    valueFormatter = (value) => `${value}`,
    className = "",
  }: SliderProps
) => {

  const pct = ((value - min) / (max - min)) * 100;
  const inputRef = useRef<HTMLInputElement>(null);
  const [inputWidth, setInputWidth] = useState(0);

  useLayoutEffect(() => {
    const el = inputRef.current;
    if (!el) return;
    const ro = new ResizeObserver(() => setInputWidth(el.offsetWidth));
    ro.observe(el);
    setInputWidth(el.offsetWidth);
    return () => ro.disconnect();
  }, []);

  // Thumb center travels from THUMB_W/2 to (inputWidth - THUMB_W/2)
  // Compute fill as an exact pixel value using the measured width
  const fillPx = inputWidth > 0.
    ? (pct / 100) * (inputWidth - THUMB_W) + THUMB_W / 2
    : null;

  const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const value: number = Number(e.target.value);
    if (isNaN(value)) {
      setValue(0);
      return;
    }
    setValue(value);
  };

  return (
    <div className={className}>
      {label && (
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
      )}

      <div className="relative flex items-center w-full mt-2">
        <div className="absolute w-full h-1.5 rounded-full bg-surfaceAlt" />
        <div
          className="absolute h-1.5 rounded-full bg-accent"
          style={{ width: fillPx != null ? `${fillPx}px` : `${pct}%` }}
        />
        <input
          ref={inputRef}
          type="range"
          min={min}
          max={max}
          step={stepSize}
          value={value}
          onChange={handleChange}
          className="relative w-full cursor-pointer appearance-none bg-transparent
            [&::-webkit-slider-runnable-track]:h-1.5
            [&::-webkit-slider-thumb]:appearance-none [&::-webkit-slider-thumb]:-mt-2.25
            [&::-webkit-slider-thumb]:h-6 [&::-webkit-slider-thumb]:w-6
            [&::-webkit-slider-thumb]:rounded-full [&::-webkit-slider-thumb]:bg-accent
            [&::-webkit-slider-thumb]:box-border [&::-webkit-slider-thumb]:border-[5px]
            [&::-webkit-slider-thumb]:border-transparent [&::-webkit-slider-thumb]:bg-clip-content
            [&::-moz-range-thumb]:h-6 [&::-moz-range-thumb]:w-6 [&::-moz-range-thumb]:rounded-full
            [&::-moz-range-thumb]:bg-accent [&::-moz-range-thumb]:box-border [&::-moz-range-thumb]:border-[5px]
            [&::-moz-range-thumb]:border-transparent [&::-moz-range-thumb]:bg-clip-content"
        />
      </div>
    </div>
  );
};

export default Slider;