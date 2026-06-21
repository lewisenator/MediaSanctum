import { type Book } from '#/client/bookClient.ts';
import Progress from '#/components/formatting/Progress.tsx';
import FileSize from '#/components/formatting/FileSize.tsx';
import { Link } from '@tanstack/react-router';
import { IoIosPlay } from 'react-icons/io';
import { IoCloudDownloadOutline } from 'react-icons/io5';
import { useState } from 'react';
import AudioDuration from '#/components/formatting/AudioDuration.tsx';
import Time from '#/components/formatting/Time.tsx';

type AvailableAudiobookProps = {
  book: Book;
};

const AvailableAudiobook = (
  {book}: AvailableAudiobookProps
) => {

  const percent = book.audiobookProgress?.percent || 0;
  const seconds = book.audiobookProgress?.seconds || 0;

  const durationString = book.audiobookFile?.ffProbe?.format.duration;
  const duration = durationString
    ? Number.parseFloat(durationString)
    : (book.audiobookProgress?.duration || 0);

  const chapters = book.audiobookFile?.ffProbe?.chapters;
  const currentChapterIndex = (book.audiobookProgress?.currentChapter || 1) - 1;
  const currentChapter = chapters && currentChapterIndex
    ? chapters[currentChapterIndex]
    : undefined;

  const [downloading, setDownloading] = useState<boolean>(false);
  const downloadBook = async () => {
    if (!book.audiobookFile) return;
    setDownloading(true);

    try {
      const response = await fetch(book.audiobookFile.url);
      console.log('fetching: ', book.audiobookFile.url);

      const contentLength = response.headers.get('content-length');
      const totalBytes = contentLength ? parseInt(contentLength, 10) : 0;

      if (!response.body) return;

      const reader = response.body.getReader();
      let receivedBytes = 0;
      const chunks = [];

      while (true) {
        const { done, value } = await reader.read();
        if (done) break;

        chunks.push(value);
        receivedBytes += value.length;

        if (totalBytes) {
          console.log('downloading... totalBytes:', totalBytes,
            ' receivedBytes:', receivedBytes,
            ' ', Math.round((receivedBytes / totalBytes) * 100), '%');
        }
      }

      const audiobookFileBlob = new Blob(chunks);

      const url = window.URL.createObjectURL(audiobookFileBlob);
      const link = document.createElement('a');
      link.href = url;
      link.setAttribute('download', book.audiobookFile.filename);
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
            <span>
              {currentChapter && (
                <span className="capitalize">
                  {currentChapterIndex + 1}. {currentChapter?.title?.toLowerCase()} -
                </span>
              )}
              <span className="ml-1">
                <Time
                  milliseconds={(duration - seconds) * 1000}
                  showHoursMinutesSeconds={false}
                  className="mr-1"
                />
                Left
              </span>
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
              <div className="uppercase text-xs font-ui text-textMute tracking-widest">Duration</div>
              <div className="text-sm">
                <AudioDuration seconds={duration} className="tabular-nums" />
              </div>
            </div>
            <div className="flex flex-col gap-2">
              <div className="uppercase text-xs font-ui text-textMute tracking-widest">Format</div>
              <div className="text-sm uppercase">{book.audiobookFile?.ffProbe?.streams[0].codecName}</div>
            </div>
            <div className="flex flex-col gap-2">
              <div className="uppercase text-xs font-ui text-textMute tracking-widest">File Size</div>
              <div className="text-sm"><FileSize bytes={book.audiobookFile?.size} /></div>
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
          to='/books/$bookId/listener'
          params={{ bookId: book.id }}
        >
          <IoIosPlay /> Continue Listening
        </Link>
        <div className="flex flex-row sm:flex-col gap-3">
          <button
            className="btn rounded-lg border border-sidebarTextDim grow shrink-0"
            disabled={downloading}
            onClick={downloadBook}
          >
            <IoCloudDownloadOutline /> {downloading ? "Downloading..." : "Download M4B"}
          </button>
          {/* TODO: implement bookmarks */}
          <button disabled={true} className="btn rounded-lg border border-sidebarTextDim grow shrink-0 disabled:cursor-not-allowed!">Bookmarks</button>
        </div>
      </div>
    </div>
  );
};

export default AvailableAudiobook;