import { createFileRoute, Outlet, useNavigate } from '@tanstack/react-router';
import { useAuth } from '#/context/AuthContext.tsx';
import { useEffect } from 'react';

export const Route = createFileRoute('/(authenticated)')({
  component: AuthenticatedRoute,
  head: () => ({
    meta: [
      { title: 'Media Sanctum' },
    ],
  })
})

function AuthenticatedRoute() {
  const { user, accessToken } = useAuth();
  const navigate = useNavigate();

  useEffect(() => {
    if (!user || !accessToken) {
      navigate({
        to: '/login'
      });
    }
  }, [user, accessToken]);

  return <Outlet />;
}