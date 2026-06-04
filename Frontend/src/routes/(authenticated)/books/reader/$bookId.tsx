import { createFileRoute, Link } from '@tanstack/react-router';
import { useEffect, useRef, useState } from 'react';
import { type IReactReaderStyle, ReactReader, ReactReaderStyle } from 'react-reader';
import { queryOptions, useSuspenseQuery } from '@tanstack/react-query';
import { getBook } from '#/client/mediaSanctumClient.ts';
import { Rendition } from 'epubjs';
import { IoIosArrowBack } from "react-icons/io";

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

  console.log(`Rendering epub at url ${book.ebookFile.url}`);

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
      background: "none",
      height: "100%",
      top: "0",
      width: "25%",
      fontSize: "0px !important",
      color: "transparent",
    },
    arrowHover: {
      ...ReactReaderStyle.arrowHover,
      opacity: "20%",
      color: "var(--c-text)",
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
          <div>
            <Link to="/books/$bookId" params={{bookId}} className="flex flex-row font-ui items-center gap-2 rounded-md text-sm transition-colors
            hover:bg-surfaceAlt hover:text-text px-2.5 py-1 text-textDim border border-transparent">
              <IoIosArrowBack /> Back
            </Link>
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

        <ReactReader
          url={book.ebookFile.url}
          title={`${book.title} · <span>${book.author.name}</span>`}
          location={location}
          locationChanged={(epubcfi: string) => setLocation(epubcfi)}
          showToc={false}
          readerStyles={readerStyles}
          epubInitOptions={{
            openAs: 'epub',
          }}
          epubOptions={{
            spread,
          }}
          getRendition={(_rendition: Rendition) => {
            rendition.current = _rendition;
            applyTheme(rendition.current);
          }}
          tocChanged={(chapters) => {
            console.log('chapters', chapters);
          }}
        />
      </div>
    </div>
  )
}