import { type RefObject, useRef, useState } from 'react';
import type { Rendition } from 'epubjs';

type ProgressBarProps = {
  currentChapter: number;
  totalChapters: number;
  progress: number;
  setProgress: (value: number) => void;
  rendition: RefObject<Rendition|undefined>;
};

const ProgressBar = (
  {
    currentChapter,
    totalChapters,
    progress,
    setProgress,
    rendition,
  }: ProgressBarProps
) => {
  const [isScrubbing, setIsScrubbing] = useState(false);
  const barRef = useRef<HTMLDivElement>(null);

  const getScrubPct = (clientX: number): number => {
    if (!barRef.current) return 0;
    const { left, width } = barRef.current.getBoundingClientRect();
    return Math.max(0, Math.min(1, (clientX - left) / width));
  };

  return (
    <div className="flex flex-row items-center justify-between w-full gap-3 px-8 py-2 bg-surface border-t border-border z-20">
      <div className="shrink-0">
        <span className="tabular-nums whitespace-nowrap font-text text-textMute text-xs">
          {currentChapter >= 0 && totalChapters > 0 ? `Chapter ${currentChapter} / ${totalChapters}` : '—'}
        </span>
      </div>
      <div
        ref={barRef}
        className="group flex-1 py-2.5 -my-2.5 cursor-pointer data-[scrubbing=true]:cursor-grabbing"
        data-scrubbing={isScrubbing || undefined}
        onPointerDown={(e) => {
          e.currentTarget.setPointerCapture(e.pointerId);
          setIsScrubbing(true);
          setProgress(Math.round(getScrubPct(e.clientX) * 100));
        }}
        onPointerMove={(e) => {
          if (!(e.buttons & 1)) return;
          setProgress(Math.round(getScrubPct(e.clientX) * 100));
        }}
        onPointerUp={(e) => {
          setIsScrubbing(false);
          const pct = getScrubPct(e.clientX);
          setProgress(Math.round(pct * 100));
          const cfi = rendition.current?.book.locations.cfiFromPercentage(pct);
          if (cfi) rendition.current?.display(cfi);
        }}
      >
        <div className="h-0.75 group-hover:h-1.5 group-data-[scrubbing=true]:h-1.5 bg-surfaceAlt rounded-sm
              overflow-hidden transition-[height] duration-120 ease">
          <div className="h-full bg-accent transition-[width] duration-200 ease-out pointer-events-none" style={{width: `${progress}%`}} />
        </div>
      </div>
      <div className="shrink-0">
        { !isNaN(progress) && (
          <span className="tabular-nums whitespace-nowrap font-text text-textMute text-xs">{`${progress} %`}</span>
        )}
      </div>
    </div>
  );
};

export default ProgressBar;