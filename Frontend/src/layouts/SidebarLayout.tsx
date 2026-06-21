import LeftHandMenu from '#/components/layout/LeftHandMenu.tsx';
import { Outlet } from '@tanstack/react-router';
import TopBar from '#/components/layout/TopBar.tsx';

const SidebarLayout = () => {
  return (
    <div id="sidebar-layout" className="min-h-screen max-h-screen overflow-y-hidden flex flex-row">
      <LeftHandMenu />
      <div className="flex flex-col w-full">
        <TopBar />
        <Outlet />
      </div>
    </div>
  );
};

export default SidebarLayout;