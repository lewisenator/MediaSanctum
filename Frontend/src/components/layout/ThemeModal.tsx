import { useTheme } from '#/context/ThemeContext.tsx';
import Modal from '#/components/widgets/Modal.tsx';

type ThemeModalProps = {
  isOpen: boolean;
  setIsOpen: (isOpen: boolean) => void;
};

const ThemeModal = (
  {
    isOpen,
    setIsOpen,
  }: ThemeModalProps
) => {
  const { theme, themes, setTheme } = useTheme();

  return (
    <Modal title="Choose A Theme" isOpen={isOpen} onClose={() => setIsOpen(false)}>
      <div
        className="grid grid-cols-2 sm:grid-cols-3 gap-3 p-5 max-h-[70vh] overflow-y-auto"
      >
        {themes.map((item) => (
          <button
            key={item.id}
            onClick={() => {
              setTheme(item.id);
              setIsOpen(false);
            }}
            className="flex flex-col text-left rounded-[10px] p-3.5 cursor-pointer min-h-24 gap-2"
            style={{
              backgroundColor: item.bg,
              color: item.text,
              border: `${theme === item.id ? `2px solid ${item.swatch}` : '1px solid var(--c-border)'}`,
              boxShadow: `${theme === item.id ? '0 4px 14px rgba(0,0,0,0.18)' : 'none'}`,
            }}
          >
            <div className="flex items-center gap-2">
                    <span
                      className="w-3.5 h-3.5 rounded-[50%]"
                      style={{backgroundColor: item.swatch}}
                    >
                    </span>
              <span
                className="font-display font-semibold text-[16px]"
                style={{
                  color: item.light ? "#1a1a1a" : "#f4f3f0",
                }}
              >
                {item.name}
              </span>
            </div>
            <div
              className="font-mono text-[10.5px] uppercase tracking-wider"
              style={{
                color: item.light ? "#5a5240" : "#a89878",
              }}
            >
              {item.direction}
            </div>
          </button>
        ))}
      </div>
    </Modal>
  );
};

export default ThemeModal;