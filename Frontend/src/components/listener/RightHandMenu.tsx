import { useState } from 'react';
import { type Book, type Chapter } from '#/client/bookClient.ts';
import Chapters from '#/components/listener/Chapters.tsx';
import Bookmarks from '#/components/listener/Bookmarks.tsx';
import History from '#/components/listener/History.tsx';

type RightHandMenuProps = {
  book: Book;
  currentChapter?: Chapter;
  setCurrentChapter: (chapter: Chapter) => void;
  setPosition: (position: number) => void;
  className?: string;
};

const RightHandMenu = (
  { book, setPosition, currentChapter, setCurrentChapter, className }: RightHandMenuProps
) => {
  const [selectedMenu, setSelectedMenu] = useState("chapters");
  const numChapters = book.audiobookFile?.ffProbe?.chapters.length;

  const tabs = [
    ['chapters', `Chapters`, `${numChapters}`],
    ['bookmarks', 'Bookmarks', "0"],
    ['history', 'History'],
  ];

  return (
    <div id="audiobook-player-rhm" className={`flex flex-col bg-surface min-w-87.5 border-l border-border ${className}`}>
      <div className="flex items-baseline">
        {tabs.map(([key, display, count]) => (
          <a
            key={key}
            onClick={() => setSelectedMenu(key)}
            className={`flex flex-row items-center justify-center grow border-b-2 py-4 text-sm cursor-pointer
                hover:bg-surfaceAlt
                ${key === selectedMenu ? "text-text border-accent" : "text-textMute border-border"}`}
          >
            {display}
            <span className="text-textMute tabular-nums text-xs pl-1 pt-0.5">
              {count}
            </span>
          </a>
        ))}
      </div>

      {selectedMenu === "chapters" && (
        <Chapters
          chapters={book.audiobookFile?.ffProbe?.chapters}
          currentChapter={currentChapter}
          setPosition={setPosition}
          setCurrentChapter={setCurrentChapter}
        />
      )}

      {selectedMenu === "bookmarks" && (
        <Bookmarks />
      )}

      {selectedMenu === "history" && (
        <History />
      )}
    </div>
  );
};

export default RightHandMenu;