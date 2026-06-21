import { type ReactNode } from 'react';

type MainProps = {
  children: ReactNode | ReactNode[]
};

const Main = ({children}: MainProps) => {
  return (
    <main className='flex flex-row flex-1 p-4 sm:p-5 md:p-6 lg:p-8 overflow-y-auto w-full h-screen'>
      {children}
    </main>
  );
};

export default Main;