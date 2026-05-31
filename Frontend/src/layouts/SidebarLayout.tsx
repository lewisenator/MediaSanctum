import LeftHandMenu from '#/components/LeftHandMenu.tsx';
import { Outlet } from '@tanstack/react-router';

const SidebarLayout = () => {
  return (
    <div id="sidebar-layout" className="min-h-screen flex flex-row">
      <LeftHandMenu />
      <main className='flex-1 p-4 md:p-6 lg:p-8 overflow-y-auto'>
        <div className="w-full">
          <Outlet />
        </div>
      </main>
    </div>
  );
};

export default SidebarLayout;