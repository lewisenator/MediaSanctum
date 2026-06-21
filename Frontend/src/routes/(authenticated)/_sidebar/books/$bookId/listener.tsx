import { createFileRoute, Link } from '@tanstack/react-router';
import { useEffect, useRef, useState } from 'react';
import { queryOptions, useMutation, useSuspenseQuery } from '@tanstack/react-query';
import { type Chapter, getBook, reportAudiobookProgress, type AudiobookProgressRequest } from '#/client/bookClient.ts';
import RightHandMenu from '#/components/listener/RightHandMenu.tsx';
import { IoMdPlay, IoMdPause, IoMdSkipForward, IoMdSkipBackward } from "react-icons/io";
import { PiFastForwardFill, PiRewindFill } from "react-icons/pi";
import Time from '#/components/formatting/Time.tsx';
import { BsBook } from 'react-icons/bs';
import Slider from '#/components/widgets/Slider.tsx';
import { queryClient } from '#/router.tsx';
import Breadcrumbs, { breadcrumbClassName } from '#/components/layout/Breadcrumbs.tsx';

const REPORT_PROGRESS_EVERY_SECONDS = 30;

const bookQueryOptions = (bookId: string) => queryOptions({
  queryKey: ['book', bookId],
  queryFn: async () => {
    console.log('Fetching book ', bookId);
    const res = await getBook(bookId)
    console.log('Fetched book book ', bookId);
    return res;
  }
});

export const Route = createFileRoute(
  '/(authenticated)/_sidebar/books/$bookId/listener',
)({
  component: ListenerPage,
  loader: async ({params: {bookId}, context: {queryClient}}) => {
    return queryClient.ensureQueryData(bookQueryOptions(bookId));
  }
});

function ListenerPage() {
  const { bookId } = Route.useParams();
  const { data: book } = useSuspenseQuery(bookQueryOptions(bookId));
  const audioRef = useRef<HTMLAudioElement>(null);

  const [playing, setPlayingUnwrapped] = useState(false);
  const localStorageKey = `book-${bookId}-audio-position`;
  const [seconds, setSeconds] = useState(() => {
    const audiobookProgressSeconds = book.audiobookProgress?.seconds;
    const positionString = localStorage.getItem(localStorageKey);
    return audiobookProgressSeconds
      ? audiobookProgressSeconds
      : (positionString ? Number.parseInt(positionString) : 0);
  });
  const durationSeconds = Number.parseFloat(book.audiobookFile?.ffProbe?.format.duration || "1");
  const [lastProgressSeconds, setLastProgressSeconds] = useState<number|undefined>();

  const [currentChapter, setCurrentChapter] = useState<Chapter|undefined>();
  const [currentChapterIndex, setCurrentChapterIndex] = useState<number>(0);
  const chapters = book.audiobookFile?.ffProbe?.chapters.sort(
    (a, b) => a.id - b.id
  ) || [];

  const narrator: string | undefined = book.audiobookFile?.ffProbe?.format?.tags?.["composer"];

  const setPlaying = (value: boolean) => {
    if (value) {
      audioRef.current?.play();
    } else {
      audioRef.current?.pause();
    }
    setPlayingUnwrapped(value);
  };

  const setPosition = (value: number) => {
    console.log('setPosition', value);
    if (audioRef.current) {
      audioRef.current.currentTime = value;
    }
  };

  const fastForward = () => {
    const audio = audioRef.current;
    if (!audio) return;
    audio.currentTime = audio.currentTime + 15;
  };

  const rewind = () => {
    const audio = audioRef.current;
    if (!audio) return;
    audio.currentTime = audio.currentTime - 15;
  };

  const nextChapter = () => {
    const audio = audioRef.current;
    if (!audio) return;
    const newIndex = currentChapterIndex + 1;
    setCurrentChapterIndex(newIndex)
    const newChapter = chapters[newIndex]
    setCurrentChapter(newChapter);
    audio.currentTime = (newChapter?.start || 0) / 1000;
  };

  const previousChapter = () => {
    const audio = audioRef.current;
    if (!audio) return;
    const newIndex = currentChapterIndex - 1;
    setCurrentChapterIndex(newIndex)
    const newChapter = chapters[newIndex]
    setCurrentChapter(newChapter);
    audio.currentTime = (newChapter?.start || 0) / 1000;
  };

  useEffect(() => {
    const audio = audioRef.current;
    if (!audio) return;
    audio.currentTime = seconds;
    const timeupdateHandler = () => {
      setSeconds(Math.floor(audio.currentTime));
    };
    audio.addEventListener('timeupdate', timeupdateHandler);
    return () => {
      audio.removeEventListener('timeupdate', timeupdateHandler);
    }
  }, []);

  const { mutateAsync: reportProgressMutation } = useMutation({
    mutationFn: (progress: AudiobookProgressRequest) => reportAudiobookProgress(bookId, progress)
  });

  const reportProgress = async () => {
    setLastProgressSeconds(seconds);
    const progress: AudiobookProgressRequest = {
      seconds,
      duration: durationSeconds,
      percent: seconds / durationSeconds * 100.0,
      totalChapters: chapters.length,
      currentChapter: currentChapterIndex + 1,
    }
    // console.log("Reporting progress at: ", seconds, "\n", JSON.stringify(progress))
    await reportProgressMutation(progress);
    await queryClient.invalidateQueries({
      queryKey: ['book', bookId]
    });
  };

  useEffect(() => {
    reportProgress();
  }, [playing]);

  useEffect(() => {
    if (!lastProgressSeconds || (Math.abs(seconds - lastProgressSeconds) > REPORT_PROGRESS_EVERY_SECONDS)) {
      reportProgress();
    }
    localStorage.setItem(localStorageKey, seconds.toString());
    const newChapter: Chapter = chapters.filter((chapter, index) => {
      const audio = audioRef.current;
      if (!audio) return;
      const milliseconds = audio.currentTime * 1000;
      const current = chapter.start <= milliseconds && chapter.end > milliseconds;
      if (current) {
        setCurrentChapterIndex(index);
      }
      return current;
    })[0];
    setCurrentChapter(newChapter);
    // console.log(`Setting seconds: ${seconds} chapter: ${newChapter?.id}`);
  }, [seconds]);

  return (
    <div className="min-h-screen w-full">
      <audio src={book.audiobookFile!.url} className="hidden" ref={audioRef}></audio>
      <div className="flex overflow-y-auto w-full">
        <div id="audiobook-player" className="flex flex-col bg-bg grow items-center justi mt-4 md:mt-6 lg:mt-8 min-w-100">
          <Breadcrumbs
            className="self-start mb-5 ml-4 md:ml-6 lg:ml-8"
            items={[
              <Link to="/books/" className={breadcrumbClassName}>Books</Link>,
              <Link to="/books/$bookId/" className={breadcrumbClassName} params={{bookId: bookId}}>{book.title}</Link>,
              <Link to="/books/$bookId/listener" className={breadcrumbClassName} params={{bookId: bookId}}>Audiobook</Link>
            ]}
          />
          <div
            className="uppercase text-accent font-ui text-xs tracking-[0.2em]"
          >
            Now Playing · Audiobook
          </div>

          <div className="mt-5">
            { book.audiobookEdition && book.audiobookEdition.image && book.audiobookEdition.image.url ? (
              <img
                src={book.audiobookEdition?.image?.url}
                alt={book.title}
                className="max-w-65 h-50 w-50 sm:h-55 sm:w-55 md:h-65 md:w-65 object-cover drop-shadow"
              />
            ) : (
              <div className="shrink-0 grow-0 rounded-md border border-border bg-surfaceAlt flex items-center justify-center
                max-w-65 h-50 w-50 sm:h-55 sm:w-55 md:h-65 md:w-65 text-center text-4xl drop-shadow">
                <BsBook />
              </div>
            )}
          </div>

          <div className="mt-5 title font-display font-semibold text-2xl md:text-3xl lg:text-4xl text-text">
            {book.title}
          </div>

          <div className="mt-1 text-md text-textDim italic text-xs md:text-[16px]">
            <Link
              to="/authors/$authorId"
              params={{authorId: book.author.id}}
              className="hover:underline ml-1"
            >
              {book.author.name}
            </Link>
          </div>

          {narrator && (
            <div className="mt-1 text-xs md:text-[14px] font-ui tracking-widest text-textMute uppercase">
              Narrated by {narrator}
            </div>
          )}

          <div className="mt-6 w-3/4">
            <div className="flex justify-between items-center w-full gap-1 text-sm text-textDim font-display">
              <span className="min-w-22">
                <Time milliseconds={seconds * 1000} showHoursMinutesSeconds={true} />
              </span>
              <span className="font-ui grow text-center capitalize text-textMute">
                {currentChapterIndex + 1} · {currentChapter?.title.toLowerCase()}
              </span>
              <span className="min-w-22 text-right">
                <Time
                  milliseconds={(durationSeconds - seconds) * 1000}
                  showHoursMinutesSeconds={true}
                  className="mr-1"
                />
                Left
              </span>
            </div>

            <Slider
              min={(currentChapter?.start || 0) / 1000}
              max={(currentChapter?.end || 0) / 1000}
              value={seconds}
              setValue={setPosition}
              stepSize={1}
              className="w-full mt-2"
            />
          </div>

          <div className="button-row flex items-center gap-3 mt-3 mb-12">
            <button
              className="w-13 h-13 btn btn-rounded border-2 border-border hover:bg-surfaceAlt"
              onClick={previousChapter}
            >
              <IoMdSkipBackward />
            </button>
            <button
              className="w-13 h-13 btn btn-rounded border-2 border-border hover:bg-surfaceAlt"
              onClick={rewind}
            >
              <PiRewindFill />
            </button>
            <button
              onClick={() => setPlaying(!playing)}
              className="w-18 h-18 btn btn-primary btn-rounded text-xl! drop-shadow border-2 border-accent/90"
            >
              {playing ? <IoMdPause /> : <IoMdPlay />}
            </button>
            <button
              className="w-13 h-13 btn btn-rounded border-2 border-border hover:bg-surfaceAlt"
              onClick={fastForward}
            >
              <PiFastForwardFill />
            </button>
            <button
              className="w-13 h-13 btn btn-rounded border-2 border-border hover:bg-surfaceAlt"
              onClick={nextChapter}
            >
              <IoMdSkipForward />
            </button>
          </div>
        </div>
        <RightHandMenu
          book={book}
          setPosition={setPosition}
          currentChapter={currentChapter}
          setCurrentChapter={setCurrentChapter}
          className="hidden lg:block min-h-screen"
        />
      </div>
    </div>
  );
}
