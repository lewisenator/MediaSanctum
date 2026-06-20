import { createFileRoute, useNavigate } from '@tanstack/react-router';
import { useEffect, useRef, useState } from 'react';
import { ReactReader } from 'react-reader';
import { queryOptions, useMutation, useSuspenseQuery } from '@tanstack/react-query';
import { getBook } from '#/client/bookClient.ts';
import { type NavItem, Rendition } from 'epubjs';
import { useTheme } from '#/context/ThemeContext.tsx';
import ProgressBar from '#/components/reader/ProgressBar.tsx';
import { readerStyles } from './readerStyles.ts';
import TitleBar from '#/components/reader/TitleBar.tsx';
import TOC from '#/components/reader/TOC.tsx';
import ReaderSettings from '#/components/reader/ReaderSettings.tsx';
import { reportEbookProgress, type Progress } from '#/client/bookClient.ts';
import { queryClient } from '#/router.tsx';

const bookQueryOptions = (bookId: string) => queryOptions({
  queryKey: ['book', bookId],
  queryFn: async () => {
    console.log('Fetching book ', bookId);
    const res = await getBook(bookId)
    console.log('Fetched book book ', bookId);
    return res;
  }
});

export const Route = createFileRoute('/(authenticated)/books/$bookId/reader')({
  component: EbookReaderPage,
  loader: async ({params: {bookId}, context: {queryClient}}) => {
    return queryClient.ensureQueryData(bookQueryOptions(bookId));
  }
});

function EbookReaderPage() {
  const { bookId } = Route.useParams();
  const { data: book } = useSuspenseQuery(bookQueryOptions(bookId));
  const { theme, themes } = useTheme();
  const currentTheme = themes.find(t => t.id === theme)!;

  const [progress, setProgress] = useState<number>(0);

  const [showToc, setShowTocUnwrapped] = useState<boolean>(false);
  const [showSettings, setShowSettingsUnwrapped] = useState<boolean>(false);

  const setShowToc = (value: boolean) => {
    if (value && showSettings) {
      setShowSettingsUnwrapped(false);
    }
    setShowTocUnwrapped(value);
  };

  const setShowSettings = (value: boolean) => {
    if (value && showToc) {
      setShowTocUnwrapped(false);
    }
    setShowSettingsUnwrapped(value);
  };

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
  const [currentPage, setCurrentPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);

  // Ref so the rendered event handler always sees the latest applyTheme closure.
  const applyThemeRef = useRef<(r?: Rendition) => void>(() => {});

  const applyTheme = (rendition?: Rendition) => {
    if (!rendition) return;
    rendition.themes.default({
      "body": {
        "font-size": `${fontSize}em !important`,
        "margin": "0 !important",
        "color": `${currentTheme.text} !important`,
        "background": `${currentTheme.bg} !important`,
      },
      "p": {
        "line-height": lineHeight,
        "font-family": `${font}`,
        "margin-top": `${paragraphSpacing}em !important`,
      }
    });
  };

  applyThemeRef.current = applyTheme;


  type ReaderSettings = {
    fontSize?: number;
    lineHeight?: number;
    font?: string;
    paragraphSpacing?: number;
    pageMargins?: number;
    spread?: string;
    brightness?: number;
  };

  const getSettings = () => {
    const storedSettings = localStorage.getItem('reader-settings');
    const settings: ReaderSettings|null = storedSettings ? JSON.parse(storedSettings) : null;
    return settings;
  };

  const [spread, setSpread] = useState<boolean>(() => {
    const spreadString = getSettings()?.spread;
    if (spreadString) {
      return spreadString === "true";
    }
    return true;
  });
  const [brightness, setBrightness] = useState<number>(() => getSettings()?.brightness || 100);
  const [fontSize, setFontSize] = useState<number>(() => getSettings()?.fontSize || 1.0);
  const [lineHeight, setLineHeight] = useState<number>(() => getSettings()?.lineHeight || 1.0);
  const [font, setFont] = useState<string>(() => getSettings()?.font || "'Helvetica', sans-serif");
  const [paragraphSpacing, setParagraphSpacing] = useState<number>(() => getSettings()?.paragraphSpacing || 1.0);
  const [pageMargins, setPageMargins] = useState<number>(() => getSettings()?.pageMargins || 1.0);

  useEffect(() => {
    applyTheme(rendition?.current);
    const settings: ReaderSettings = {
      fontSize, lineHeight, font, paragraphSpacing, pageMargins, spread: `${spread}`, brightness
    };
    localStorage.setItem("reader-settings", JSON.stringify(settings));
  }, [fontSize, lineHeight, font, paragraphSpacing, pageMargins, spread, brightness, theme]);

  useEffect(() => {
    rendition.current?.layout({ spread: spread ? "auto" : "none" });
  }, [spread]);

  useEffect(() => {
    window.dispatchEvent(new Event('resize'));
  }, [pageMargins]);


  const cacheLocations = (rendition: Rendition) => {
    const locKey = `epub-locations-${book.ebookFile!.id}`;
    const renditionBook = rendition.book;
    const saved = localStorage.getItem(locKey);

    if (saved) {
      renditionBook.locations.load(saved);
    } else {
      renditionBook.locations.generate(1024).then(() => {
        localStorage.setItem(locKey, renditionBook.locations.save());
        if (lastCfiRef.current) {
          locationChanged(lastCfiRef.current);
        }
      });
    }
  };

  const { mutateAsync: reportProgressMutation } = useMutation({
    mutationFn: (progress: Progress) => reportEbookProgress(bookId, progress)
  });

  const locationChanged = async (epubcfi: string) => {
    lastCfiRef.current = epubcfi;
    setLocation(epubcfi);
    if (!rendition.current) return;

    // epub.js types incorrectly declare locationFromCfi as returning Location; it actually returns number
    const locs = rendition.current.book.locations as any;
    const currentPageCalc = locs.locationFromCfi(epubcfi) as number;
    const totalPagesCalc = locs.total as number ?? 0;
    // locationFromCfi returns -1 before locations are generated; skip update until then
    if (currentPageCalc >= 0 && totalPagesCalc > 0) {
      setCurrentPage(currentPageCalc);
      setTotalPages(totalPagesCalc);
    }

    const pct = rendition.current.book.locations.percentageFromCfi(epubcfi);
    const progressPct = (pct != null && !isNaN(pct)) ? Math.round(pct * 100.0) : 0;
    setProgress(progressPct);

    const bookToc = rendition.current.book.navigation?.toc ?? [];
    const spineItem = rendition.current.book.spine.get(epubcfi);
    let currentChapterCalc = currentChapter;
    if (spineItem && bookToc.length > 0) {
      const spineFile = spineItem.href.split('/').pop()?.split('#')[0] ?? '';
      for (let i = bookToc.length - 1; i >= 0; i--) {
        if (bookToc[i].href.split('/').pop()?.split('#')[0] === spineFile) {
          currentChapterCalc = i + 1;
          setCurrentChapter(currentChapterCalc);
          break;
        }
      }
    }

    try {
      const progressInput: Progress = {
        epubcfi,
        percent: progressPct,
        currentChapter: currentChapterCalc,
        totalChapters,
        currentPage: currentPageCalc >= 0 ? currentPageCalc : currentPage,
        totalPages: totalPagesCalc > 0 ? totalPagesCalc : totalPages,
      };
      await reportProgressMutation(progressInput);
    } catch (_) {}
  };

  const navigate = useNavigate();

  const bookDetailsPageClicked = async () => {
    await queryClient.invalidateQueries({
      queryKey: ['book', bookId]
    });
    navigate({
      to: "/books/$bookId",
      params: {
        bookId: bookId
      }
    });
  };

  return (
    <div className="flex flex-col flex-1 overflow-hidden mx-auto w-full p-[-4] md:p-[-6] lg:p-[-8]">
      <div className="flex-1 flex flex-col w-full">
        <div
          id="brightness-filter"
          className="pointer-events-none fixed inset-0 z-100 bg-black"
          style={{ opacity: (100 - brightness) / 100 }}
        >

        </div>
        <TitleBar
          book={book}
          tocClicked={() => setShowToc(!showToc)}
          settingsClicked={() => setShowSettings(!showSettings)}
          toc={toc}
          backClicked={() => bookDetailsPageClicked()}
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

          <div ref={readerRef} className="flex-1" style={{ padding: `${pageMargins}em` }}>
            <ReactReader
              url={book.ebookFile!.url}
              title={`${book.title} · <span>${book.author.name}</span>`}
              location={location}
              readerStyles={readerStyles}
              epubInitOptions={{
                openAs: 'epub',
              }}
              epubOptions={{
                spread: spread ? "auto" : "none",
              }}
              locationChanged={() => {}}
              getRendition={(_rendition: Rendition) => {
                rendition.current = _rendition;
                applyTheme(rendition.current);
                cacheLocations(rendition.current);
                _rendition.on('relocated', (loc: any) => locationChanged(loc.start.cfi));
                // rendered fires after layout.format() applies its inline padding, so our
                // override lands last and wins the cascade.
                _rendition.on('rendered', () => applyThemeRef.current(rendition.current));
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

            {showSettings && (
              <ReaderSettings
                dismiss={() => setShowSettings(false)}
                fontSize={fontSize}
                setFontSize={setFontSize}
                lineHeight={lineHeight}
                setLineHeight={setLineHeight}
                font={font}
                setFont={setFont}
                paragraphSpacing={paragraphSpacing}
                setParagraphSpacing={setParagraphSpacing}
                pageMargins={pageMargins}
                setPageMargins={setPageMargins}
                brightness={brightness}
                setBrightness={setBrightness}
                spread={spread}
                setSpread={setSpread}
              />
            )}
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