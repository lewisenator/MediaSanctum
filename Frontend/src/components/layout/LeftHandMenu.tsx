import React, { useState } from 'react';
import { Link, useNavigate } from '@tanstack/react-router';
import Logo from '#/components/layout/Logo.tsx';
import { IoBookOutline, IoPersonOutline } from 'react-icons/io5';
import { PiListLight, PiSignOutLight, PiPaletteThin } from 'react-icons/pi';
import { logout } from '#/client/authClient.ts';
import { useAuth } from '#/context/AuthContext.tsx';
import { IoIosArrowBack, IoIosArrowForward } from 'react-icons/io';
import { useTheme } from '#/context/ThemeContext.tsx';
import ThemeModal from '#/components/layout/ThemeModal.tsx';

type LeftHandMenuProps = {
  flyout?: Boolean;
};

const styles = {
  linkClass: 'flex flex-row items-center gap-0.5 [&.active]:bg-[rgba(255,255,255,0.06)] hover:bg-[rgba(255,255,255,0.06)] hover:text-sidebarAccent rounded-md transition-colors duration-150 py-2 px-3 shrink-0',
};

const LeftHandMenu = (
  {flyout = false}: LeftHandMenuProps
) => {
  const navigate = useNavigate();
  const { setUser, setAccessToken } = useAuth();
  const [collapse, setCollapse] = useState(false);
  const [themePickerOpen, setThemePickerOpen] = useState(false);
  const { themeObject } = useTheme();

  const menuItems: {
    name: string;
    route: string;
    icon: React.ReactNode;
  }[] = [
    {name: 'Books', route: '/books', icon: <IoBookOutline />},
    {name: 'Series', route: '/series', icon: <PiListLight />},
    {name: 'Authors', route: '/authors', icon: <IoPersonOutline />},
  ];

  const handleLogout = async () => {
    try {
      try {
        await logout();
      } catch (_) {}
      setAccessToken(null);
      setUser(null);
      navigate({
        to: '/login'
      });
    } catch (err) {
      console.error("Logout failed: ", err);
    }
  };

  const toggleCollapse = () => {
    setCollapse(!collapse);
  };

  return (
    <aside className={`bg-sidebar text-sidebarText flex flex-col ${flyout ? 'w-64 h-screen' : 'w-0'} md:w-64 shadow-[4px_0_6px_-2px_rgba(0,0,0,0.15)] transition-all duration-200 ease-in-out ${collapse ? 'max-w-16' : ''}`}>
      <Link to="/" className="hover:bg-white/5 transition gap-2.5 px-5 flex flex-rows h-14 items-center">
        <span className='shrink-0'>
          <Logo />
        </span>
        <h1 className={`font-medium tracking-tight text-lg ${flyout ? 'opacity-100' : 'opacity-0'} ${collapse ? 'md:opacity-0' : 'md:opacity-100'}`}>
          Media
          <span className="italic font-normal text-sidebarAccent pl-px">
            Sanctum
          </span>
        </h1>
      </Link>

      <nav className="flex-1 py-4 px-3 space-y-1 overflow-y-auto">
        {menuItems.map((menuItem) => (
          <Link to={menuItem.route} className={styles.linkClass} key={menuItem.name} title={menuItem.name}>
            <span className='shrink-0'>{menuItem.icon}</span>
            <span className={`pl-1 text-[15px] font-sans ${flyout ? 'opacity-100' : 'opacity-0'} ${collapse && !flyout ? 'md:opacity-0' : 'md:opacity-100'}`}>{menuItem.name}</span>
          </Link>
        ))}
      </nav>

      <div className='py-4 px-3 space-y-1 overflow-y-auto'>
        <button onClick={() => setThemePickerOpen(!themePickerOpen)} className={`${styles.linkClass} w-full flex flex-row justify-between items-center flex-nowrap`} title="Theme">
          <ThemeModal
            isOpen={themePickerOpen}
            setIsOpen={setThemePickerOpen}
          />
          <span className="flex flex-row items-center">
            <span className="shrink-0 opacity-50">
              <PiPaletteThin />
            </span>
            <span className={`pl-1 font-sans text-[15px] ${flyout ? 'opacity-50' : 'opacity-0'} ${collapse ? 'md:opacity-0' : 'md:opacity-50'}`}>Theme</span>
          </span>

          <span className={`pl-1 text-xs font-mono text-sidebarAccent ${flyout ? 'opacity-50' : 'opacity-0'} 
            ${collapse ? 'md:opacity-0' : 'md:opacity-50'}`}>
            {themeObject.name}
          </span>
        </button>
        { !flyout && (
          <button onClick={toggleCollapse} className={`${styles.linkClass} w-full`} title={collapse ? 'Expand' : 'Collapse'}>
          <span className='shrink-0 opacity-50'>
            { collapse ? <IoIosArrowForward /> : <IoIosArrowBack />}
          </span>
            <span className={`pl-1 font-sans text-[15px] opacity-0 ${collapse ? 'md:opacity-0' : 'md:opacity-50'}`}>
            {collapse ? 'Expand' : 'Collapse'}
          </span>
          </button>
        )}
        <button onClick={handleLogout} className={`${styles.linkClass} w-full`} title="Sign Out">
          <span className='shrink-0 opacity-50'>
            <PiSignOutLight />
          </span>
          <span className={`pl-1 font-sans text-[15px] ${flyout ? 'opacity-50' : 'opacity-0'} ${collapse ? 'md:opacity-0' : 'md:opacity-50'}`}>
            Sign Out
          </span>
        </button>
      </div>
    </aside>
  );
};

export default LeftHandMenu;