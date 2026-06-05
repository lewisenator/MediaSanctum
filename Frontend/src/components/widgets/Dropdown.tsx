import type { ReactNode, CSSProperties } from 'react';
import { useState, useRef, useEffect } from 'react';
import { createPortal } from 'react-dom';
import { IoChevronDownOutline } from 'react-icons/io5';

export type Option = [string, string];

export type DropdownProps = {
  label: string;
  options: Array<Option>;
  value: string;
  setValue: (value: string) => void;
  keyFormatter?: (k: string, value: string) => ReactNode;
};

const Dropdown = (
  {
    label,
    options,
    value,
    setValue,
    keyFormatter = (k) => k,
  }: DropdownProps
) => {
  const [open, setOpen] = useState(false);
  const [listStyle, setListStyle] = useState<CSSProperties>({});
  const buttonRef = useRef<HTMLButtonElement>(null);
  const listRef = useRef<HTMLUListElement>(null);

  useEffect(() => {
    const handleClickOutside = (e: MouseEvent) => {
      if (
        buttonRef.current && !buttonRef.current.contains(e.target as Node) &&
        listRef.current && !listRef.current.contains(e.target as Node)
      ) {
        setOpen(false);
      }
    };
    document.addEventListener('mousedown', handleClickOutside);
    return () => document.removeEventListener('mousedown', handleClickOutside);
  }, []);

  const handleOpen = () => {
    if (buttonRef.current) {
      const rect = buttonRef.current.getBoundingClientRect();
      setListStyle({
        position: 'fixed',
        top: rect.bottom + 4,
        left: rect.left,
        width: rect.width,
      });
    }
    setOpen((o) => !o);
  };

  const selected = options.find(([, v]) => v === value);

  return (
    <div className="w-full">
      <div className="text-sm font-medium text-textDim mb-1">
        {label}
      </div>
      <button
        ref={buttonRef}
        type="button"
        onClick={handleOpen}
        className="w-full flex items-center justify-between px-3 py-1.5 text-sm rounded-md border-2 border-accent bg-surface hover:bg-surfaceAlt transition-colors"
      >
        <span>{selected ? keyFormatter(selected[0], selected[1]) : value}</span>
        <IoChevronDownOutline className={`ml-2 shrink-0 transition-transform ${open ? 'rotate-180' : ''}`} />
      </button>
      {open && createPortal(
        <ul
          ref={listRef}
          style={listStyle}
          className="z-50 max-h-56 overflow-y-auto rounded-md border border-border bg-surface shadow-lg"
        >
          {options.map(([k, v], index) => (
            <li
              key={index}
              onClick={() => { setValue(v); setOpen(false); }}
              className={`px-3 py-1.5 text-sm cursor-pointer hover:bg-surfaceAlt transition-colors ${v === value ? 'text-accent' : 'text-text'}`}
            >
              {keyFormatter(k, v)}
            </li>
          ))}
        </ul>,
        document.body
      )}
    </div>
  );
};

export default Dropdown;
