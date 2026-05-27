import { createFileRoute } from '@tanstack/react-router'
import { useState } from 'react';

export const Route = createFileRoute('/(unauthenticated)/login')({
  component: LoginPage,
})

function LoginPage() {
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');

  return (
    <>
      <form className='space-y-4'>
        <div>
          <label
            className='mb-1.5 block text-sm font-medium text-textDim font-sans'
            htmlFor="email"
          >
            Email
          </label>
          <input
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
            type='password'
            className='bg-surface font-ui border border-border text-ui w-full rounded-md px-3 py-2 transition-colors focus:outline-none focus:border-accent focus:ring-1 focus:ring-accent'
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            autoComplete="password"
          />
        </div>
        <button
          type='submit'
          className='w-full! text-center justify-center inline-flex mt-3 items-center gap-1.5 rounded-sm px-4 py-2.5! text-sm transition-colors bg-accent text-surface font-ui font-medium border border-accent hover:brightness-[1.08] active:brightness-[0.92]]'
        >
          Sign In
        </button>
      </form>
    </>
  );
}
