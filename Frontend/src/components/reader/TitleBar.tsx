import { Link } from '@tanstack/react-router';
import { IoIosArrowBack } from "react-icons/io";
import { RxHamburgerMenu } from "react-icons/rx";
import { CiSettings } from "react-icons/ci";
import type { Book } from '#/client/bookClient.ts';
import type { NavItem } from 'epubjs';

type TitleBarProps = {
  bookId: string;
  book: Book;
  tocClicked: () => void;
  settingsClicked: () => void;
  toc: NavItem[];
};

const TitleBar = (
  {
    bookId,
    book,
    tocClicked,
    settingsClicked,
    toc,
  }: TitleBarProps
) => {
  return (
    <div
      id="reader-title-bar"
      className="flex flex-row items-center justify-between w-full gap-3 px-3 py-2 bg-surface border-b border-border"
    >
      <div className="flex flex-row items-center">
        <Link to="/books/$bookId" params={{bookId}} className="flex flex-row font-ui items-center gap-2 rounded-md text-sm transition-colors
            hover:bg-surfaceAlt hover:text-text px-2.5 py-1 text-textDim border border-transparent hover:cursor-pointer">
          <IoIosArrowBack /> Back
        </Link>
        { toc && toc.length > 0 && (
          <a onClick={() => {
            tocClicked();
          }} className="flex flex-row font-ui items-center justify-center gap-2 rounded-md text-sm transition-colors
            hover:bg-surfaceAlt hover:text-text w-7 h-7 text-textDim border border-transparent hover:cursor-pointer">
            <RxHamburgerMenu />
          </a>
        )}

      </div>
      <div className="truncate">
        <span className="font-text text-text">{book.title} </span>
        ·
        <span className="font-ui text-xs text-textDim ml-1 italic">{book.author.name}</span>
      </div>
      <div>
        <a
          onClick={settingsClicked}
          className="flex flex-row font-ui items-center justify-center rounded-md
            hover:bg-surfaceAlt hover:text-text hover:cursor-pointer w-7 h-7 text-textDim border border-border"
        >
          <CiSettings/>
        </a>
      </div>
    </div>
  );
};

export default TitleBar;