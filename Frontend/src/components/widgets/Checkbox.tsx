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
    <div
      className="flex flex-row justify-between items-center text-sm font-medium text-textDim"
    >
      <label>
        {label}
      </label>
      <input
        type="checkbox"
        checked={value}
        onChange={(e) => setValue(e.target.checked)}
      />
    </div>
  );
};

export default Checkbox;