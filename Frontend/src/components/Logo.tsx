export type LogoProps = {
  size?: number;
};
const Logo = ({
  size = 7,
}: LogoProps) => {
  return (
    <svg className={`h-${size} w-${size} shrink-0`} viewBox="0 0 130 130" fill="none">
      <path d="M48 36 L65 12 L82 36 Z" fill="#6b1f1a"/>
      <line x1="65" y1="12" x2="65" y2="6"
            stroke="currentColor" strokeWidth="1.5"/>
      <circle cx="65" cy="5" r="1.8" fill="var(--c-sidebarAccent)"/>
      <path d="M44 40 V36 H50 V40 H56 V36 H62 V40 H68 V36 H74 V40 H80 V36 H86 V40 Z"
            fill="currentColor"/>
      <line x1="44" y1="44" x2="86" y2="44" stroke="currentColor" strokeWidth="1"/>
      <rect x="48" y="44" width="34" height="64" stroke="currentColor"
            strokeWidth="2" fill="currentColor" fillOpacity="0.04"/>
      <path d="M56 58 Q56 48, 65 48 T74 58 V76 H56 Z"
            fill="var(--c-sidebarAccent)"/>
      <g fill="var(--c-sidebar)">
        <path d="M65 53 L60 64 L70 64 Z"/>
        <rect x="58.5" y="63.5" width="13" height="1.6"/>
        <path d="M65 65 L58 76 L72 76 Z"/>
      </g>
      <rect x="63" y="82" width="4" height="9" fill="currentColor"/>
      <rect x="63" y="95" width="4" height="7" fill="currentColor"/>
      <rect x="42" y="108" width="46" height="8" stroke="currentColor"
            strokeWidth="2" fill="none"/>
      <line x1="38" y1="116" x2="92" y2="116" stroke="currentColor" strokeWidth="2"/>
    </svg>
  );
};

export default Logo;