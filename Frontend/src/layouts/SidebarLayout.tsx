import LeftHandMenu from '#/components/LeftHandMenu.tsx';
import { Outlet } from '@tanstack/react-router';

const SidebarLayout = () => {
  return (
    <div id="sidebar-layout" className="min-h-screen max-h-screen overflow-y-hidden flex flex-row">
      <LeftHandMenu />
      <main className='flex flex-row flex-1 p-4 md:p-6 lg:p-8 overflow-y-auto w-full h-screen'>
        <Outlet />
      </main>
    </div>
  );
};

export default SidebarLayout;