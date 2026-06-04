import { Link } from '@tanstack/react-router';
import { IoIosArrowBack } from "react-icons/io";
import { RxHamburgerMenu } from "react-icons/rx";
import type { Book } from '#/client/mediaSanctumClient.ts';
import type { NavItem } from 'epubjs';

type TitleBarProps = {
  bookId: string;
  book: Book;
  tocClicked: () => void;
  toc: NavItem[];
};

const TitleBar = (
  {
    bookId,
    book,
    tocClicked,
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
            hover:bg-surfaceAlt hover:text-text px-2.5 py-1 text-textDim border border-transparent">
          <IoIosArrowBack /> Back
        </Link>
        { toc && toc.length > 0 && (
          <a onClick={() => {
            tocClicked();
          }} className="flex flex-row font-ui items-center gap-2 rounded-md text-sm transition-colors
            hover:bg-surfaceAlt hover:text-text px-2.5 py-1 text-textDim border border-transparent">
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
        Right
      </div>
    </div>
  );
};

export default TitleBar;