import { createFileRoute, useNavigate } from '@tanstack/react-router';
import { useState } from 'react';
import { useAuth } from '#/context/AuthContext.tsx';
import { useMutation } from '@tanstack/react-query';
import { login } from '#/client/mediaSanctumClient.ts';

export const Route = createFileRoute('/(unauthenticated)/login')({
  component: LoginPage,
});

function LoginPage() {
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState('');

  const navigate = useNavigate();
  const { setAccessToken, setUser } = useAuth();

  const { mutateAsync, isPending } = useMutation({
    mutationFn: () => login({email, password}),
    onSuccess: (data) => {
      setAccessToken(data.accessToken);
      setUser(data.user);
      navigate({
        to: '/books'
      });
    },
    onError: (error) => {
      setError(error.message);
    }
  });

  const handleSubmit = async (e: React.SubmitEvent<HTMLFormElement>) => {
    e.preventDefault();
    if (!email || !password) {
      setError("Email and password are required");
      return;
    }
    try {
      await mutateAsync();
    } catch (err: any) {
      const message = err?.message || 'Failed to submit login form';
      setError(message);
    }
  };

  return (
    <>
      <form className='space-y-4' onSubmit={handleSubmit}>
        { error && (
          <div className="bg-surfaceAlt text-danger px-4 py-2 rounded mb-4">
            { error }
          </div>
        )}
        <div>
          <label
            className='mb-1.5 block text-sm font-medium text-textDim font-sans'
            htmlFor="email"
          >
            Email
          </label>
          <input
            id="email"
            type='email'
            className='bg-surface font-ui border border-border text-ui w-full rounded-md px-3 py-2 transition-colors focus:outline-none focus:border-accent focus:ring-1 focus:ring-accent'
            value={email}
            onChange={(e) => setEmail(e.target.value)}
            autoComplete="email"
          />
        </div>
        <div>
          <label
            className='mb-1.5 block text-sm font-medium text-textDim font-sans'
            htmlFor="password"
          >
            Password
          </label>
          <input
            id="password"
            type='password'
            className='bg-surface font-ui border border-border text-ui w-full rounded-md px-3 py-2 transition-colors focus:outline-none focus:border-accent focus:ring-1 focus:ring-accent'
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            autoComplete="current-password"
          />
        </div>
        <button
          type='submit'
          className='btn btn-primary w-full! mt-3'
        >
          { isPending ? 'Signing in...' : 'Sign In' }
        </button>
      </form>
    </>
  );
}
