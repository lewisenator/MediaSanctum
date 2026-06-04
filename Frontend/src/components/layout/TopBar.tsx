import { Link } from '@tanstack/react-router';
import Logo from '#/components/layout/Logo.tsx';
import { RxHamburgerMenu } from "react-icons/rx";
import { useState } from 'react';
import LeftHandMenu from '#/components/layout/LeftHandMenu.tsx';

const TopBar = () => {
  const [isOpen, setIsOpen] = useState(false);

  return (
    <>
      {isOpen && (
        <>
          <div
            className="fixed inset-0 z-20 bg-black/50 md:hidden"
            onClick={() => setIsOpen(false)}
          >
          </div>
          <div
            id="left-hand-menu-flyout"
            className="fixed inset-y-0 left-0 z-30 flex w-64 flex-col shadow-[4px_0_6px_-2px_rgba(0,0,0,0.15)]
            dark:shadow-[4px_0_6px_-2px_rgba(0,0,0,0.45)]
            transition-all duration-200 ease-in-out md:static md:translate-x-0 bg-sidebar text-sidebarText"
          >
            <LeftHandMenu flyout={true} />
          </div>
        </>
      )}
      <header
        className='flex h-14 shrink-0 items-center justify-between px-4 md:hidden bg-sidebar text-sidebarText shadow-md'
      >
        <div>
          {/* Left */}
          <button
            className="mr-3 rounded p-1.5 hover:bg-white/10 transition-colors"
            onClick={() => setIsOpen(true)}
          >
            <RxHamburgerMenu />
          </button>
        </div>
        <div>
          {/* Center */}
          <Link to="/" className="hover:bg-white/5 transition gap-2.5 px-5 flex flex-rows h-14 items-center">
            <span className='shrink-0'>
              <Logo />
            </span>
            <h1 className={`font-medium tracking-tight text-md`}>
              Media
              <span className="italic font-normal text-sidebarAccent pl-px">
              Sanctum
            </span>
            </h1>
          </Link>
        </div>
        <div>
          {/* Right */}
        </div>
      </header>
    </>
  );
};

export default TopBar;