import { Outlet } from '@tanstack/react-router';

const FullPageLayout = () => {
  return (
    <div id="full-page-layout" className="min-h-screen max-h-screen overflow-y-hidden flex flex-row w-full">
      <Outlet />
    </div>
  );
};

export default FullPageLayout;