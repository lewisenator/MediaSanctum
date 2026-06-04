import type { NavItem } from 'epubjs';
import { IoCloseOutline } from "react-icons/io5";

type TOCProps = {
  toc: NavItem[];
  dismiss: () => void;
  clickedItem: (item: string) => void;
};


const TOC = (
  {
    toc,
    dismiss,
    clickedItem,
  }: TOCProps
) => {
  return (
    <>
      <div
        className="fixed top-11.75 bottom-10.25 left-0 z-30 flex w-full bg-black/10"
        onClick={dismiss}
      ></div>
      <div
        className="fixed top-11.75 bottom-10.25 left-0 z-30 flex w-64 flex-col
          shadow-[4px_0_6px_-2px_rgba(0,0,0,0.15)]
          transition-all duration-200 ease-in-out
          bg-surface border-b border-border
          "
      >
        <div className="px-5 py-3 flex flex-row justify-between items-center border-b border-border">
          <h1 className="font-display text-text font-semibold">
            Table of Contents
          </h1>
          <div>
            <button
              onClick={dismiss}
              className="inline-flex items-center justify-center text-lg font-bold bg-surface hover:bg-surfaceAlt transition w-7.5 h-7.5 rounded-md border border-border"
            >
              <IoCloseOutline />
            </button>
          </div>
        </div>
        <nav className="overflow-y-scroll divide-y divide-surfaceAlt">
          { toc.map((item) => (
            <a
              key={item.id}
              className="px-5 py-3 flex flex-row hover:bg-surfaceAlt cursor-pointer"
              onClick={() => clickedItem(item.href)}
            >
              {item.label}
            </a>
          ))}
        </nav>
      </div>
    </>
  );
};

export default TOC;