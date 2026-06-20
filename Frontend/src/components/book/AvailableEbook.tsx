import { type Book } from '#/client/bookClient.ts';
import FileSize from '#/components/formatting/FileSize.tsx';
import PageDuration from '#/components/formatting/PageDuration.tsx';
import { useState } from 'react';
import { IoCloudDownloadOutline } from 'react-icons/io5';
import { IoIosPlay } from "react-icons/io";
import { Link } from '@tanstack/react-router';
import Progress from '#/components/formatting/Progress.tsx';

type AvailableEbookProps = {
  book: Book;
};

const AvailableEbook = (
  {
    book,
  }: AvailableEbookProps
) => {

  const pages = book?.ebookProgress?.totalPages || book.pages || 1;
  const currentPage = book?.ebookProgress?.currentPage || 0;
  const percent = book.ebookProgress?.percent || 0;

  const [downloading, setDownloading] = useState<boolean>(false);
  const downloadBook = async () => {
    if (!book.ebookFile) return;
    setDownloading(true);
    try {
      const response = await fetch(book.ebookFile.url);
      const bookFileBlob: Blob = await response.blob();
      const url = window.URL.createObjectURL(bookFileBlob);
      const link = document.createElement('a');
      link.href = url;
      link.setAttribute('download', book.ebookFile.filename);
      document.body.appendChild(link);
      link.click();
      document.body.removeChild(link);
      window.URL.revokeObjectURL(url);
    } catch (error) {
      console.log(error);
    }
    setDownloading(false);
  }

  return (
    <div
      className="bg-surface/98 mt-4 p-5 rounded-xl border border-border flex flex-col md:flex-row justify-between flex-wrap"
    >
      <div className="flex flex-col sm:flex-row flex-1 w-full">
        <div className="flex flex-col w-full">
          {/*stats*/}
          <div className="flex flex-row mb-3 sm:mb-5 w-full items-baseline">
            <span className="text-[40px] font-bolder mr-2 font-display">{percent}%</span>
            <span>p. {currentPage} / {pages} ⋅
              <PageDuration
                className="text-textDim tabular-nums ml-1"
                pagesLeft={pages! - currentPage!}
                prefix="≈"
                suffix="left"
              />
            </span>
          </div>
          {/*Progress*/}
          <Progress
            className="-mt-3 mb-4 sm:mr-20"
            heightPx={6}
            percent={percent}
          />
          {/*Meta*/}
          <div className="grid grid-cols-2 gap-3 sm:gap-5 w-full mb-5">
            <div className="flex flex-col gap-2">
              <div className="uppercase text-xs font-ui text-textMute tracking-widest">Pages</div>
              <div className="text-sm">{pages}</div>
            </div>
            <div className="flex flex-col gap-2">
              <div className="uppercase text-xs font-ui text-textMute tracking-widest">Format</div>
              <div className="text-sm">EPUB</div>
            </div>
            <div className="flex flex-col gap-2">
              <div className="uppercase text-xs font-ui text-textMute tracking-widest">File Size</div>
              <div className="text-sm"><FileSize bytes={book.ebookFile?.size} /></div>
            </div>
            <div className="flex flex-col gap-2">
              <div className="uppercase text-xs font-ui text-textMute tracking-widest">Last Opened</div>
              <div className="text-sm">Yesterday</div>
            </div>
          </div>
        </div>
      </div>
      <div className="flex flex-col gap-3 shrink-0">
        {/*buttons*/}
        <Link
          className="btn btn-primary rounded-lg"
          to='/books/$bookId/reader'
          params={{ bookId: book.id }}
        >
          <IoIosPlay /> Continue Reading
        </Link>
        <div className="flex flex-row sm:flex-col gap-3">
          <button
            className="btn rounded-lg border border-sidebarTextDim grow shrink-0"
            disabled={downloading}
            onClick={downloadBook}
          >
            <IoCloudDownloadOutline /> Download EPUB
          </button>
          {/* TODO: implement bookmarks */}
          <button disabled={true} className="btn rounded-lg border border-sidebarTextDim grow shrink-0 disabled:cursor-not-allowed!">Bookmarks</button>
        </div>
      </div>
    </div>
  );
};

export default AvailableEbook;