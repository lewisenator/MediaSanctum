import type { Chapter } from '#/client/bookClient.ts';
import Time from '#/components/formatting/Time.tsx';
import { IoMdCheckmark, IoMdPlay } from "react-icons/io";

type ChaptersProps = {
  chapters?: Chapter[];
  currentChapter?: Chapter;
  setCurrentChapter: (chapter: Chapter) => void;
  setPosition: (position: number) => void;
};

const Chapters = (
  { chapters, currentChapter, setCurrentChapter, setPosition }: ChaptersProps
) => {
  if (!chapters || !chapters.length) return (
    <div>
      No Chapters Found
    </div>
  );

  return (
    <div className="pt-3">
      { chapters.map((chapter, index) => {
        const current = chapter.id === currentChapter?.id;
        const completed = currentChapter?.id != undefined && chapter.id < currentChapter?.id;

        return (
          <a
            key={chapter.id}
            className={`
              flex cursor-pointer gap-3 py-4 px-4 hover:bg-surfaceAlt text-xs
              ${completed && "text-textMute"}
              ${current && "border-l-2 border-accent bg-accent/15 font-semibold"}
            `}
            onClick={() => {
              setPosition(chapter.start / 1000);
              setCurrentChapter(chapter);
            }}
          >
            <div className="text-right min-w-6">
              {index + 1}
            </div>
            {completed
              ? <div className="min-w-3 mt-0.5"><IoMdCheckmark /></div>
              : current
                ? <div className="min-w-3 mt-0.5 text-accent"><IoMdPlay /></div>
                : <div className="min-w-3 mt-0.5"></div>
            }

            <div className="grow capitalize">
              {chapter.title.toLowerCase()}
            </div>
            <Time
              className=""
              milliseconds={chapter.end - chapter.start}
            />
          </a>
      )})}
    </div>
  );
};

export default Chapters;