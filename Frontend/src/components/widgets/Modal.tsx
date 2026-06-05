import { type ReactNode } from 'react';
import { createPortal } from 'react-dom';

type ModalProps = {
  children: ReactNode;
  title: string;
  onClose?: () => void;
  isOpen: boolean;
};

const Modal = (
  {
    children,
    isOpen,
    title,
    onClose,
  }: ModalProps
) => {
  if (!isOpen) return;

  const portal = document.getElementById("portal");
  if (!portal) {
    console.error('Could not find portal element');
    return;
  }

  return createPortal(
    <>
      <div className="fixed top-0 left-0 right-0 bottom-0 bg-black opacity-20 z-999">
      </div>
      <div
        className="fixed top-[50%] left-[50%] z-1000 -translate-x-1/2 -translate-y-1/2 bg-surface shadow-xl text-text
        border border-border rounded-xl min-w-80 w-[min(48rem,calc(100%-2rem))]"
      >
        <div className="flex flex-col">
          <div className="flex flex-row justify-between items-center border-b border-border py-4 px-5">
            {/* Title */}
            <h1
              className="font-display font-semibold text-lg"
            >
              {title}
            </h1>
            <button onClick={onClose} className="hover:cursor-pointer h-5 w-5 flex flex-row items-center justify-center
            text-2xl stroke-[linecap-round] stroke-[linejoin-round]] p-1 rounded hover:bg-black/5 ">
              <svg className="h-10 w-10" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"
                   stroke-linecap="round" stroke-linejoin="round">
                <path d="m6 6 12 12M18 6 6 18"></path>
              </svg>
            </button>
          </div>
          {children}
        </div>

      </div>
    </>
    , portal);
};

export default Modal;