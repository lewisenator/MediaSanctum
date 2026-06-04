import { createFileRoute, Link } from '@tanstack/react-router';
import { useEffect, useRef, useState } from 'react';
import { type IReactReaderStyle, ReactReader, ReactReaderStyle } from 'react-reader';
import { queryOptions, useSuspenseQuery } from '@tanstack/react-query';
import { getBook } from '#/client/mediaSanctumClient.ts';
import { Rendition } from 'epubjs';
import { IoIosArrowBack } from "react-icons/io";
import { RxHamburgerMenu } from "react-icons/rx";

const bookQueryOptions = (bookId: string) => queryOptions({
  queryKey: ['book', bookId],
  queryFn: async () => {
    console.log('Fetching book ', bookId);
    const res = await getBook(bookId)
    console.log('Fetched book book ', bookId);
    return res;
  }
});

export const Route = createFileRoute('/(authenticated)/books/reader/$bookId')({
  component: EbookReaderPage,
  loader: async ({params: {bookId}, context: {queryClient}}) => {
    return queryClient.ensureQueryData(bookQueryOptions(bookId));
  }
});

function EbookReaderPage() {
  const { bookId } = Route.useParams();
  const { data: book } = useSuspenseQuery(bookQueryOptions(bookId));

  const [progress, setProgress] = useState<number>(0);

  const localStorageKey = `epub-location-${bookId}`;
  const [location, setLocation] = useState<string|number>(() => {
    const storage = localStorage.getItem(localStorageKey);
    return storage ? JSON.parse(storage) : 0;
  });
  useEffect(() => {
    localStorage.setItem(localStorageKey, JSON.stringify(location));
    console.log('location: ', location);
  }, [location]);

  const rendition = useRef<Rendition | undefined>(undefined);
  const readerRef = useRef<HTMLDivElement>(null);
  const barRef = useRef<HTMLDivElement>(null);
  const lastCfiRef = useRef<string>('');
  const [isScrubbing, setIsScrubbing] = useState(false);
  const [currentChapter, setCurrentChapter] = useState(0);
  const [totalChapters, setTotalChapters] = useState(0);

  const getScrubPct = (clientX: number): number => {
    if (!barRef.current) return 0;
    const { left, width } = barRef.current.getBoundingClientRect();
    return Math.max(0, Math.min(1, (clientX - left) / width));
  };

  const [fontSize, setFontSize] = useState<number>(100);
  const [spread, setSpread] = useState<string>("none"); // "none" or "auto"
  const applyTheme = (rendition?: Rendition) => {
    if (!rendition) return;
    console.log('applying theme');
    rendition.themes.default({
      "body": {
        "font-size": `${Math.round(fontSize * 16.0 / 100.0)}px !important`,
        "margin-bottom": "25px",
      }
    });
  };
  useEffect(() => {
    applyTheme(rendition?.current);
  }, [fontSize]);

  const cacheLocations = (rendition: Rendition) => {
    const locKey = `epub-locations-${book.ebookFile.id}`;
    const renditionBook = rendition.book;
    const saved = localStorage.getItem(locKey);

    if (saved) {
      renditionBook.locations.load(saved);
    } else {
      renditionBook.locations.generate(1024).then(() => {
        localStorage.setItem(locKey, renditionBook.locations.save());
        if (lastCfiRef.current) locationChanged(lastCfiRef.current);
      });
    }
  };

  const locationChanged = (epubcfi: string) => {
    lastCfiRef.current = epubcfi;
    setLocation(epubcfi);
    if (!rendition.current) return;

    const pct = rendition.current.book.locations.percentageFromCfi(epubcfi);
    if (pct != null && !isNaN(pct)) setProgress(Math.round(pct * 100));

    const toc = rendition.current.book.navigation?.toc ?? [];
    const spineItem = rendition.current.book.spine.get(epubcfi);
    if (spineItem && toc.length > 0) {
      const spineFile = spineItem.href.split('/').pop()?.split('#')[0] ?? '';
      for (let i = toc.length - 1; i >= 0; i--) {
        if (toc[i].href.split('/').pop()?.split('#')[0] === spineFile) {
          setCurrentChapter(i + 1);
          break;
        }
      }
    }
  };

  console.log(`Rendering epub at url ${book.ebookFile.url}`);

  const showToc = () => {
    if (!rendition?.current) return false;

  };

  const readerStyles: IReactReaderStyle = {
    ...ReactReaderStyle,
    titleArea: {
      ...ReactReaderStyle.titleArea,
      background: "var(--c-surfaceAlt)",
      color: "var(--c-text)",
      display: "none",
    },
    container: {
      ...ReactReaderStyle.container,
    },
    arrow: {
      ...ReactReaderStyle.arrow,
      display: "none",
    },
    arrowHover: {
      ...ReactReaderStyle.arrowHover,
      display: "none",
    },
    reader: {
      ...ReactReaderStyle.reader,
      color: "var(--c-text)",
      background: "var(--c-bg)",
      inset: "0"
    },
    readerArea: {
      ...ReactReaderStyle.readerArea,
      backgroundColor: "var(--c-surface)",
      transition: undefined,
    },
    tocArea: {
      ...ReactReaderStyle.tocArea,
      background: "var(--c-surface)",
      color: "var(--c-text)",
    },
    tocButtonExpanded: {
      ...ReactReaderStyle.tocButtonExpanded,
      background: "var(--c-surfaceAlt)",
    },
    tocButtonBar: {
      ...ReactReaderStyle.tocButtonBar,
      background: "var(--c-text)",
    },
    tocButton: {
      ...ReactReaderStyle.tocButton,
      background: "var(--c-surfaceAlt)",
    },
    tocBackground: {
      ...ReactReaderStyle.tocBackground,

    },
    toc: {
      ...ReactReaderStyle.toc,
      background: "var(--c-surfaceAlt)",
    },
  };

  return (
    <div className="flex flex-col flex-1 overflow-hidden mx-auto w-full p-[-4] md:p-[-6] lg:p-[-8]">
      <div className="flex-1 flex flex-col w-full">
        <div
          id="reader-title-bar"
          className="flex flex-row items-center justify-between w-full gap-3 px-3 py-2 bg-surface border-b border-border"
        >
          <div className="flex flex-row items-center">
            <Link to="/books/$bookId" params={{bookId}} className="flex flex-row font-ui items-center gap-2 rounded-md text-sm transition-colors
            hover:bg-surfaceAlt hover:text-text px-2.5 py-1 text-textDim border border-transparent">
              <IoIosArrowBack /> Back
            </Link>
            <a onClick={() => {
              showToc();
            }} className="flex flex-row font-ui items-center gap-2 rounded-md text-sm transition-colors
            hover:bg-surfaceAlt hover:text-text px-2.5 py-1 text-textDim border border-transparent">
              <RxHamburgerMenu />
            </a>
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

        <div ref={readerRef} className="relative flex-1">
        <ReactReader
          url={book.ebookFile.url}
          title={`${book.title} · <span>${book.author.name}</span>`}
          location={location}
          readerStyles={readerStyles}
          epubInitOptions={{
            openAs: 'epub',
          }}
          epubOptions={{
            spread,
          }}
          locationChanged={() => {}}
          getRendition={(_rendition: Rendition) => {
            rendition.current = _rendition;
            applyTheme(rendition.current);
            cacheLocations(rendition.current);
            _rendition.on('relocated', (loc: any) => locationChanged(loc.start.cfi));
          }}
          tocChanged={(chapters) => {
            setTotalChapters(chapters.length);
            if (lastCfiRef.current) locationChanged(lastCfiRef.current);
          }}
          showToc={true}
        />
        {/* Nav zones: catch left/right clicks; yield to epub links via elementFromPoint */}
        <div
          className="absolute inset-y-0 left-0 w-1/4 z-10 cursor-pointer"
          onClick={(e) => {
            const iframe = readerRef.current?.querySelector('iframe');
            if (iframe?.contentDocument) {
              const rect = iframe.getBoundingClientRect();
              const el = iframe.contentDocument.elementFromPoint(e.clientX - rect.left, e.clientY - rect.top);
              const link = (el as HTMLElement)?.closest('a');
              if (link) { (link as HTMLElement).click(); return; }
            }
            rendition.current?.prev();
          }}
        />
        <div
          className="absolute inset-y-0 right-0 w-1/4 z-10 cursor-pointer"
          onClick={(e) => {
            const iframe = readerRef.current?.querySelector('iframe');
            if (iframe?.contentDocument) {
              const rect = iframe.getBoundingClientRect();
              const el = iframe.contentDocument.elementFromPoint(e.clientX - rect.left, e.clientY - rect.top);
              const link = (el as HTMLElement)?.closest('a');
              if (link) { (link as HTMLElement).click(); return; }
            }
            rendition.current?.next();
          }}
        />
        </div>
        <div className="flex flex-row items-center justify-between w-full gap-3 px-3 py-2 bg-surface border-t border-border">
          <div className="shrink-0">
            <span className="tabular-nums whitespace-nowrap font-text text-textMute text-xs">
              {currentChapter > 0 && totalChapters > 0 ? `Chapter ${currentChapter} / ${totalChapters}` : '—'}
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
            <span className="tabular-nums whitespace-nowrap font-text text-textMute text-xs">{progress}%</span>
          </div>
        </div>
      </div>
    </div>
  )
}