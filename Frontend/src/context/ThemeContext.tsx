import { createContext, useContext, useState, useEffect } from 'react';

const DEFAULT_THEME = 'A-linen';

type Theme = {
  id: string;
  name: string;
  direction: string;
  dark: boolean;
  light: boolean;
  swatch: string;
  bg: string;
  text: string;
};

const themes: Theme[] = [
  // ── Stacks · Light ─────────────────────────────────────────────────
  { id: 'A-paper', name: 'Paper', direction: 'Stacks · Light',
    dark: false, swatch: '#9c4a2a', bg: '#f5f1e8', text: '#2a2118', light: true },
  { id: 'A-linen', name: 'Linen', direction: 'Stacks · Light',
    dark: false, swatch: '#3d5a3c', bg: '#eae6dc', text: '#211a12', light: true },
  { id: 'A-sage',  name: 'Sage',  direction: 'Stacks · Light',
    dark: false, swatch: '#5a7a4a', bg: '#eef0e7', text: '#1a2218', light: true },
  { id: 'A-rose',  name: 'Rose',  direction: 'Stacks · Light',
    dark: false, swatch: '#b04860', bg: '#f5ece6', text: '#2a1a18', light: true },
  // ── Stacks · Dark ──────────────────────────────────────────────────
  { id: 'A-night', name: 'Midnight', direction: 'Stacks · Dark',
    dark: true,  swatch: '#e8a87c', bg: '#181410', text: '#ede2cf', light: false },
  // ── Reverie · Light ────────────────────────────────────────────────
  { id: 'B-bone',    name: 'Bone',    direction: 'Reverie · Light',
    dark: false, swatch: '#1a1a1a', bg: '#e8e4dc', text: '#0e0c08', light: true },
  { id: 'B-glacier', name: 'Glacier', direction: 'Reverie · Light',
    dark: false, swatch: '#0d4a6b', bg: '#f4f5f7', text: '#0f1218', light: true },
  { id: 'B-mint',    name: 'Mint',    direction: 'Reverie · Light',
    dark: false, swatch: '#1a7a5a', bg: '#eef4ef', text: '#0e1a14', light: true },
  // ── Reverie · Dark ─────────────────────────────────────────────────
  { id: 'B-cinema',  name: 'Cinema',  direction: 'Reverie · Dark',
    dark: true,  swatch: '#d4a574', bg: '#0a0a0c', text: '#f4f3f0', light: false },
  { id: 'B-iris',    name: 'Iris',    direction: 'Reverie · Dark',
    dark: true,  swatch: '#a78bfa', bg: '#0c0a14', text: '#f0eef8', light: false },
  { id: 'B-aurora',  name: 'Aurora',  direction: 'Reverie · Dark',
    dark: true,  swatch: '#7fd4ff', bg: '#070a14', text: '#e8eef8', light: false },
  { id: 'B-ember',   name: 'Ember',   direction: 'Reverie · Dark',
    dark: true,  swatch: '#ff7a3c', bg: '#0c0806', text: '#f5e2d2', light: false },
  // ── Reader · Common ────────────────────────────────────────────────
  { id: 'C-sepia',     name: 'Sepia',           direction: 'Reader · Common',
    dark: false, swatch: '#8a614c', bg: '#fbf0d9', text: '#5f4b32', light: true },
  { id: 'C-sol-light', name: 'Solarized Light', direction: 'Reader · Common',
    dark: false, swatch: '#268bd2', bg: '#fdf6e3', text: '#586e75', light: true },
  { id: 'C-sol-dark',  name: 'Solarized Dark',  direction: 'Reader · Common',
    dark: true,  swatch: '#2aa198', bg: '#002b36', text: '#93a1a1', light: false },
  { id: 'C-night',     name: 'Night',           direction: 'Reader · Common',
    dark: true,  swatch: '#60a5fa', bg: '#1a1a1a', text: '#e5e7eb', light: false },
  { id: 'C-contrast',  name: 'High Contrast',   direction: 'Reader · Common',
    dark: true,  swatch: '#ffd60a', bg: '#000000', text: '#ffffff', light: false }
];

type ThemeContextType = {
  theme: string;
  themes: Theme[];
  setTheme: (theme: string) => void;
};

const ThemeContext = createContext<ThemeContextType | undefined>(undefined);

export const ThemeProvider = ({children}: { children: React.ReactNode }) => {
  const [theme, setTheme] = useState(() => {
    const data: string = localStorage.getItem('theme') || DEFAULT_THEME;
    return data;
  });

  useEffect(() => {
    document.documentElement.setAttribute('data-theme', theme);
    localStorage.setItem('theme', theme);
  }, [theme]);


  return (
    <ThemeContext.Provider value={{
      theme,
      themes,
      setTheme,
    }}>
      {children}
    </ThemeContext.Provider>
  );
};

export const useTheme = () => {
  const context = useContext(ThemeContext);
  if (!context) {
    throw new Error('useTheme must be used within an ThemeContextProvider');
  }
  return context;
};