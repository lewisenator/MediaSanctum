import { Outlet } from '@tanstack/react-router';
import Logo from '#/components/Logo.tsx';

const AuthLayout = () => {
  return (
    <div id="auth-layout" className="flex items-center justify-center p-4 min-h-screen">
      <div className="w-full max-w-sm">
        <div className='mb-8 flex flex-col items-center text-center'>
          <Logo size={14} />
          <h1 className="text-3xl font-display font-medium">
            Media
            <span
              className="italic font-normal text-accent"
            >
              Sanctum
            </span>
          </h1>
          <p className='mt-1 text-sm text-textDim font-sans'>Access your library</p>
        </div>
        <div className='p-6 sm:p8 rounded-xl shadow-sm bg-surface border border-border text-text'>
          <Outlet />
        </div>
      </div>
    </div>
  );
};

export default AuthLayout;