import React from 'react';
import { Link, useNavigate } from '@tanstack/react-router';
import Logo from '#/components/Logo.tsx';
import { IoBookOutline } from "react-icons/io5";
import { PiListLight, PiSignOutLight } from "react-icons/pi";
import { IoPersonOutline } from "react-icons/io5";
import { logout } from '#/client/mediaSanctumClient.ts';
import { useAuth } from '#/context/AuthContext.tsx';


const styles = {
  linkClass: 'flex flex-row items-center gap-0.5 [&.active]:bg-[rgba(255,255,255,0.06)] hover:bg-[rgba(255,255,255,0.06)] hover:text-sidebarAccent rounded-md transition-colors duration-150 py-2 px-3 shrink-0',
};

const LeftHandMenu = () => {
  const navigate = useNavigate();
  const { setUser, setAccessToken } = useAuth();

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

  return (
    <aside className="bg-sidebar text-sidebarText flex flex-col w-16 md:w-64 shadow-[4px_0_6px_-2px_rgba(0,0,0,0.15)] transition-all duration-200 ease-in-out">
      <Link to="/" className="hover:bg-white/5 transition gap-2.5 px-5 flex flex-rows h-14 items-center">
        <span className='shrink-0'>
          <Logo />
        </span>
        <h1 className="font-medium tracking-tight text-lg opacity-0 md:opacity-100">
          Media
          <span className="italic font-normal text-sidebarAccent pl-px">
            Sanctum
          </span>
        </h1>
      </Link>

      <nav className="flex-1 py-4 px-3 space-y-1 overflow-y-auto">
        {menuItems.map((menuItem) => (
          <Link to={menuItem.route} className={styles.linkClass} key={menuItem.name}>
            <span className='shrink-0'>{menuItem.icon}</span>
            <span className='pl-1 font-sans opacity-0 md:opacity-100'>{menuItem.name}</span>
          </Link>
        ))}
      </nav>

      <div className='py-4 px-3 space-y-1 overflow-y-auto'>
        <button onClick={handleLogout} className={`${styles.linkClass} w-full`}>
          <span className='shrink-0 opacity-50'><PiSignOutLight /></span>
          <span className='pl-1 font-sans opacity-0 md:opacity-50'>Sign Out</span>
        </button>
      </div>
    </aside>
  );
};

export default LeftHandMenu;