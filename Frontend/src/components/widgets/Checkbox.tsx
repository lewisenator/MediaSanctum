import { IoCheckmark } from 'react-icons/io5';

type CheckboxProps = {
  label: string,
  value: boolean,
  setValue: (value: boolean) => void,
};

const Checkbox = (
  {
    label,
    value,
    setValue,
  }: CheckboxProps,
) => {

  return (
    <label className="flex flex-row justify-between items-center text-sm font-medium text-textDim cursor-pointer">
      <span>{label}</span>
      <span className="relative">
        <input
          type="checkbox"
          checked={value}
          onChange={(e) => setValue(e.target.checked)}
          className="sr-only"
        />
        <span
          className={`flex items-center justify-center w-4.5 h-4.5 rounded border transition-colors duration-150
            ${value
              ? 'bg-accent border-accent text-surface'
              : 'bg-surface border-border text-transparent'
            }`}
        >
          <IoCheckmark className="w-3 h-3 stroke-1" />
        </span>
      </span>
    </label>
  );
};

export default Checkbox;
