import { createFileRoute } from '@tanstack/react-router';
import { useEffect, useRef, useState } from 'react';
import { ReactReader } from 'react-reader';
import { queryOptions, useSuspenseQuery } from '@tanstack/react-query';
import { getBook } from '#/client/mediaSanctumClient.ts';
import { type NavItem, Rendition } from 'epubjs';

import ProgressBar from '#/components/reader/ProgressBar.tsx';
import { readerStyles } from './readerStyles.ts';
import TitleBar from '#/components/reader/TitleBar.tsx';
import TOC from '#/components/reader/TOC.tsx';

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
  const [showToc, setShowToc] = useState<boolean>(false);
  const [toc, setToc] = useState<Array<NavItem>>([]);

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

  const lastCfiRef = useRef<string>('');

  const [currentChapter, setCurrentChapter] = useState(0);
  const [totalChapters, setTotalChapters] = useState(0);

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

  return (
    <div className="flex flex-col flex-1 overflow-hidden mx-auto w-full p-[-4] md:p-[-6] lg:p-[-8]">
      <div className="flex-1 flex flex-col w-full">
        <TitleBar
          bookId={bookId}
          book={book}
          tocClicked={() => setShowToc(!showToc)}
        />

        <div className="center-wrapper flex flex-row h-full w-full">
          {showToc && (
            <TOC
              toc={toc}
              dismiss={() => setShowToc(false)}
              clickedItem={(href) => {
                rendition.current?.display(href);
                setShowToc(false);
              }}
            />
          )}

          <div ref={readerRef} className="flex-1">
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
                setToc(chapters);
                setTotalChapters(chapters.length);
                if (lastCfiRef.current) locationChanged(lastCfiRef.current);
              }}
              showToc={true}
            />
            {/* Nav zones: catch left/right clicks; yield to epub links via elementFromPoint */}
            <div
              className="click-left absolute top-11.75 inset-y-0 left-0 w-1/4 z-10 cursor-pointer"
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
              className="click-right absolute top-11.75 inset-y-0 right-0 w-1/4 z-10 cursor-pointer"
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
        </div>
        <ProgressBar
          currentChapter={currentChapter}
          totalChapters={totalChapters}
          progress={progress}
          setProgress={setProgress}
          rendition={rendition}
        />
      </div>
    </div>
  )
}