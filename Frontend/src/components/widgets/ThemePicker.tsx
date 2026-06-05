import { useTheme } from '#/context/ThemeContext.tsx';

const ThemePicker = () => {
  const {theme, setTheme, themes} = useTheme();
  return (
    <div className="grid grid-cols-4">
      {themes.map((item) => (
        <a
          style={{
            backgroundColor: item.bg,
            color: item.text,
            border: `${item.id === theme ? 3 : 1}px solid ${item.swatch}`,
          }}
          title={item.name}
          className="py-2 rounded-md font-display flex items-center justify-center m-1
          hover:cursor-pointer hover:-translate-y-px transition-colors
          duration-200"
          onClick={() => setTheme(item.id)}
        >
          Aa
        </a>
      ))}
    </div>
  );
};

export default ThemePicker;